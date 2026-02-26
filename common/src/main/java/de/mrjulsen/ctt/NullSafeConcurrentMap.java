package de.mrjulsen.ctt;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class NullSafeConcurrentMap<K, V> implements ConcurrentMap<K, V> {

    private static final Object NULL_KEY = new Object();
    private static final Object NULL_VALUE = new Object();

    private final ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>();

    private Object maskKey(Object key) {
        return key == null ? NULL_KEY : key;
    }

    private Object maskValue(Object value) {
        return value == null ? NULL_VALUE : value;
    }

    @SuppressWarnings("unchecked")
    private V unmaskValue(Object value) {
        return value == NULL_VALUE ? null : (V) value;
    }

    @Override
    public V get(Object key) {
        return unmaskValue(map.get(maskKey(key)));
    }

    @Override
    public V put(K key, V value) {
        return unmaskValue(map.put(maskKey(key), maskValue(value)));
    }

    @Override
    public V remove(Object key) {
        return unmaskValue(map.remove(maskKey(key)));
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(maskKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(maskValue(value));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return unmaskValue(map.putIfAbsent(maskKey(key), maskValue(value)));
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(maskKey(key), maskValue(value));
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(maskKey(key), maskValue(oldValue), maskValue(newValue));
    }

    @Override
    public V replace(K key, V value) {
        return unmaskValue(map.replace(maskKey(key), maskValue(value)));
    }

    @Override
    public Set<K> keySet() {
        return map.keySet().stream()
                .map(k -> k == NULL_KEY ? null : (K) k)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return map.values().stream()
                .map(this::unmaskValue)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(
                        e.getKey() == NULL_KEY ? null : (K) e.getKey(),
                        unmaskValue(e.getValue())
                ))
                .collect(Collectors.toSet());
    }
}
