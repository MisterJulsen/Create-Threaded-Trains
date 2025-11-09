package de.mrjulsen.ctt;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;

public final class CreateThreadedTrains {

    public static final String MOD_ID = "createthreadedtrains";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static WorkerThread thread;
    private static Future<?> future;

    public static void init() {

        TickEvent.SERVER_PRE.register((server) -> {
            CreateThreadedTrains.future = CreateThreadedTrains.thread.submitTask(() -> {
                Create.RAILWAYS.tick(server.overworld());
            });
        });
        
        TickEvent.SERVER_POST.register((server) -> {
            if (CreateThreadedTrains.future != null) {
                try {
                    CreateThreadedTrains.future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        LifecycleEvent.SERVER_STARTED.register(server -> {
            future = null;
            thread = new WorkerThread("Train Worker");
        });

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            thread.shutdown();
            future = null;
        });
    }

}
