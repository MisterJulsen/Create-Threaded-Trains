package de.mrjulsen.ctt.util;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import de.mrjulsen.ctt.CTTCrossPlatform;
import de.mrjulsen.ctt.config.ModServerConfig;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

public final class CreateThreadedTrains {

    public static final String MOD_ID = "createthreadedtrains";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static MinecraftServer serverInstance;
    private static final ReentrantLock RAILWAYS_LOCK = new ReentrantLock();
    private static final Queue<Future<?>> tasks = new ConcurrentLinkedQueue<>();

    private static Thread globalRailwayThread;
    private static Thread ioThread;

    private static ExecutorService globalRailwayExecutor;
    private static ExecutorService ioExecutor;

    public static boolean serverTickScheduled = true;
    public static boolean railwayTickScheduled = true;

    private static boolean isShuttingDown = false;

    private static long lastTickTime;
    private static long avgTickTime;

    public static void init() {
        CTTCrossPlatform.registerConfig();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOGGER.error("Thread crashed in " + t.getName(), e);
        });
    }

    public static void setTickTime(long l) {
        lastTickTime = l;
        if (avgTickTime == 0) {
            avgTickTime = lastTickTime;
        } else {
            avgTickTime += lastTickTime;
            avgTickTime /= 2;
        }
    }

    public static long getLastTickTime() {
        return lastTickTime;
    }

    public static long getAvgTickTime() {
        return avgTickTime;
    }

    public static void clearAvgTickTime() {
        avgTickTime = 0;
    }


    public static void start(MinecraftServer server) {
        serverInstance = server;
        isShuttingDown = false;

        globalRailwayExecutor = Executors.newSingleThreadExecutor(r -> {
            globalRailwayThread = new Thread(r, "Train Worker");
            globalRailwayThread.setDaemon(true);
            return globalRailwayThread;
        });

        ioExecutor = Executors.newSingleThreadExecutor(r -> {
            ioThread = new Thread(r, "Global Railway Manager IO Worker");
            ioThread.setDaemon(true);
            return ioThread;
        });
    }

    public static void stop(MinecraftServer server) {
        isShuttingDown = true;
        LOGGER.info("[CTT] Stopping global railway worker thread...");
        globalRailwayExecutor.shutdown();
        try {
            globalRailwayExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("[CTT] Stopped global railway worker thread!");
        LOGGER.info("[CTT] Stopping global railway IO worker thread...");
        ioExecutor.shutdown();
        try {
            ioExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("[CTT] Stopped global railway IO worker thread!");
        serverInstance = null;
    }

    public static void preTick(MinecraftServer server) {
        serverTickScheduled = false;
        railwayTickScheduled = false;
        submitRailwayManagerAsync(() -> {
            try {
                Create.RAILWAYS.tick(server.overworld());
            } catch (Throwable throwable) {
                LOGGER.error("Error while executing railway manager tick.", throwable);
            } finally {
                railwayTickScheduled = true;
                if (!ModServerConfig.SYNC_WITH_SERVER_TICK.get()) {
                    if (serverTickScheduled) {
                        preTick(server);
                    }
                }
            }
        });
    }

    public static void postTick(MinecraftServer server) {
        if (!ModServerConfig.SYNC_WITH_SERVER_TICK.get()) {
            return;
        }
        try {
            while (!tasks.isEmpty()) {
                tasks.poll().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn("Error while waiting for train worker.", e);
        }
    }

    public static void submitRailwayManagerAsync(Runnable task) {
        if (!isShuttingDown && globalRailwayExecutor != null && !globalRailwayExecutor.isShutdown()) {
            Future<?> fut = (globalRailwayExecutor.submit(task));
            if (ModServerConfig.SYNC_WITH_SERVER_TICK.get()) {
                tasks.add(fut);
            }
        } else {
            task.run();
        }
    }

    public static void submitAsyncIO(Runnable task) {
        if (!isShuttingDown && ioExecutor != null && !ioExecutor.isShutdown()) {
            ioExecutor.submit(task);
        } else {
            task.run();
        }
    }

    public static Optional<MinecraftServer> getServer() {
        return Optional.ofNullable(serverInstance);
    }

    public static boolean isOnWorkerThread() {
        return Thread.currentThread() == globalRailwayThread;
    }

    public static void runSafelyOnMain(Runnable r) {
        MinecraftServer srv = serverInstance;
        if (srv == null) return;

        RAILWAYS_LOCK.lock();
        try {
            srv.execute(r);
        } catch (Exception e) {
            LOGGER.warn("Error while executing tasks on the server thread.", e);
        } finally {
            RAILWAYS_LOCK.unlock();
        }
    }

}
