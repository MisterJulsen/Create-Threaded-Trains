package de.mrjulsen.ctt.util;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConcurrentList<E> implements List<E>, RandomAccess, Cloneable {

    private final StampedLock lock = new StampedLock();
    private Object[] elements;
    private int size;

    private static final int DEFAULT_CAPACITY = 16;

    public ConcurrentList() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public ConcurrentList(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
        elements = new Object[Math.max(initialCapacity, 1)];
    }

    public ConcurrentList(Collection<? extends E> c) {
        Object[] arr = c.toArray();
        elements = Arrays.copyOf(arr, Math.max(arr.length, DEFAULT_CAPACITY), Object[].class);
        size = arr.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(minCapacity, elements.length + (elements.length >> 1));
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    @SuppressWarnings("unchecked")
    private E[] snapshot(int currentSize) {
        return (E[]) Arrays.copyOf(elements, currentSize);
    }

    @Override
    public int size() {
        long stamp = lock.tryOptimisticRead();
        int s = size;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try { s = size; } finally { lock.unlockRead(stamp); }
        }
        return s;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean add(E e) {
        long stamp = lock.writeLock();
        try {
            ensureCapacity(size + 1);
            elements[size++] = e;
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void add(int index, E element) {
        long stamp = lock.writeLock();
        try {
            rangeCheckForAdd(index);
            ensureCapacity(size + 1);
            System.arraycopy(elements, index, elements, index + 1, size - index);
            elements[index] = element;
            size++;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] arr = c.toArray();
        if (arr.length == 0) return false;
        long stamp = lock.writeLock();
        try {
            ensureCapacity(size + arr.length);
            System.arraycopy(arr, 0, elements, size, arr.length);
            size += arr.length;
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Object[] arr = c.toArray();
        if (arr.length == 0) return false;
        long stamp = lock.writeLock();
        try {
            rangeCheckForAdd(index);
            ensureCapacity(size + arr.length);
            System.arraycopy(elements, index, elements, index + arr.length, size - index);
            System.arraycopy(arr, 0, elements, index, arr.length);
            size += arr.length;
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        long stamp = lock.tryOptimisticRead();
        E val = (E) elements[index];
        int s = size;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                rangeCheck(index);
                val = (E) elements[index];
            } finally {
                lock.unlockRead(stamp);
            }
        } else {
            if (index < 0 || index >= s)
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + s);
        }
        return val;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        long stamp = lock.writeLock();
        try {
            rangeCheck(index);
            E old = (E) elements[index];
            elements[index] = element;
            return old;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        long stamp = lock.writeLock();
        try {
            rangeCheck(index);
            E old = (E) elements[index];
            int moved = size - index - 1;
            if (moved > 0) System.arraycopy(elements, index + 1, elements, index, moved);
            elements[--size] = null;
            return old;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean remove(Object o) {
        long stamp = lock.writeLock();
        try {
            int idx = indexOfUnlocked(o);
            if (idx < 0) return false;
            int moved = size - idx - 1;
            if (moved > 0) System.arraycopy(elements, idx + 1, elements, idx, moved);
            elements[--size] = null;
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true);
    }

    private boolean batchRemove(Collection<?> c, boolean retain) {
        Objects.requireNonNull(c);
        long stamp = lock.writeLock();
        try {
            int w = 0;
            boolean modified = false;
            for (int r = 0; r < size; r++) {
                if (c.contains(elements[r]) == retain) {
                    elements[w++] = elements[r];
                } else {
                    modified = true;
                }
            }
            for (int i = w; i < size; i++) elements[i] = null;
            size = w;
            return modified;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        long stamp = lock.writeLock();
        try {
            int w = 0;
            boolean modified = false;
            for (int r = 0; r < size; r++) {
                @SuppressWarnings("unchecked") E e = (E) elements[r];
                if (filter.test(e)) {
                    modified = true;
                } else {
                    elements[w++] = e;
                }
            }
            for (int i = w; i < size; i++) elements[i] = null;
            size = w;
            return modified;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        long stamp = lock.readLock();
        try {
            for (Object e : c) {
                if (indexOfUnlocked(e) < 0) return false;
            }
            return true;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public int indexOf(Object o) {
        long stamp = lock.readLock();
        try {
            return indexOfUnlocked(o);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        long stamp = lock.readLock();
        try {
            if (o == null) {
                for (int i = size - 1; i >= 0; i--)
                    if (elements[i] == null) return i;
            } else {
                for (int i = size - 1; i >= 0; i--)
                    if (o.equals(elements[i])) return i;
            }
            return -1;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    private int indexOfUnlocked(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) if (elements[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++) if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    @Override
    public void clear() {
        long stamp = lock.writeLock();
        try {
            Arrays.fill(elements, 0, size, null);
            size = 0;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Object[] toArray() {
        long stamp = lock.readLock();
        try {
            return Arrays.copyOf(elements, size);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        long stamp = lock.readLock();
        try {
            if (a.length < size)
                return (T[]) Arrays.copyOf(elements, size, a.getClass());
            System.arraycopy(elements, 0, a, 0, size);
            if (a.length > size) a[size] = null;
            return a;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        long stamp = lock.readLock();
        try {
            subListRangeCheck(fromIndex, toIndex, size);
            @SuppressWarnings("unchecked")
            E[] snap = (E[]) Arrays.copyOfRange(elements, fromIndex, toIndex);
            return Collections.unmodifiableList(Arrays.asList(snap));
        } finally {
            lock.unlockRead(stamp);
        }
    }

    private static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size) throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex) throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        long stamp = lock.writeLock();
        try {
            for (int i = 0; i < size; i++) {
                @SuppressWarnings("unchecked") E e = (E) elements[i];
                elements[i] = operator.apply(e);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        long stamp = lock.writeLock();
        try {
            Arrays.sort((E[]) elements, 0, size, c);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return snapshotIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return snapshotListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return snapshotListIterator(index);
    }

    private Iterator<E> snapshotIterator() {
        E[] snap = getSnapshot();
        return new Iterator<E>() {
            int cursor = 0;
            int lastReturned = -1;

            public boolean hasNext() { return cursor < snap.length; }

            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturned = cursor;
                return snap[cursor++];
            }

            public void remove() {
                if (lastReturned < 0) throw new IllegalStateException();
                ConcurrentList.this.remove(snap[lastReturned]);
                lastReturned = -1;
            }
        };
    }

    private ListIterator<E> snapshotListIterator(int startIndex) {
        E[] snap = getSnapshot();
        if (startIndex < 0 || startIndex > snap.length)
            throw new IndexOutOfBoundsException("Index: " + startIndex);
        return new ListIterator<E>() {
            int cursor = startIndex;
            int lastReturned = -1;

            public boolean hasNext()     { return cursor < snap.length; }
            public boolean hasPrevious() { return cursor > 0; }

            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturned = cursor;
                return snap[cursor++];
            }

            public E previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                lastReturned = --cursor;
                return snap[cursor];
            }

            public int nextIndex()     { return cursor; }
            public int previousIndex() { return cursor - 1; }

            public void remove() {
                if (lastReturned < 0) throw new IllegalStateException();
                ConcurrentList.this.remove(snap[lastReturned]);
                lastReturned = -1;
            }

            public void set(E e) {
                if (lastReturned < 0) throw new IllegalStateException();
                ConcurrentList.this.replace(snap[lastReturned], e);
                snap[lastReturned] = e;
            }

            public void add(E e) {
                ConcurrentList.this.add(cursor, e);
                lastReturned = -1;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private E[] getSnapshot() {
        long stamp = lock.readLock();
        try {
            return snapshot(size);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : getSnapshot()) action.accept(e);
    }

    @Override
    public Spliterator<E> spliterator() {
        return Arrays.spliterator(getSnapshot());
    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof List)) return false;
        E[] snap = getSnapshot();
        List<?> other = (List<?>) o;
        if (snap.length != other.size()) return false;
        Iterator<?> it = other.iterator();
        for (E e : snap) {
            if (!Objects.equals(e, it.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        E[] snap = getSnapshot();
        int result = 1;
        for (E e : snap) result = 31 * result + (e == null ? 0 : e.hashCode());
        return result;
    }

    @Override
    public String toString() {
        E[] snap = getSnapshot();
        if (snap.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < snap.length; i++) {
            sb.append(snap[i] == this ? "(this Collection)" : snap[i]);
            if (i < snap.length - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConcurrentList<E> clone() {
        long stamp = lock.readLock();
        try {
            ConcurrentList<E> copy = new ConcurrentList<>(size);
            System.arraycopy(elements, 0, copy.elements, 0, size);
            copy.size = size;
            return copy;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public boolean addIfAbsent(E e) {
        long stamp = lock.writeLock();
        try {
            if (indexOfUnlocked(e) >= 0) return false;
            ensureCapacity(size + 1);
            elements[size++] = e;
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean replace(E oldVal, E newVal) {
        long stamp = lock.writeLock();
        try {
            int idx = indexOfUnlocked(oldVal);
            if (idx < 0) return false;
            elements[idx] = newVal;
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}