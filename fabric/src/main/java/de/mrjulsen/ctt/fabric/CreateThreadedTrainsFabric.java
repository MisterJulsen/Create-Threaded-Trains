package de.mrjulsen.ctt.fabric;

import de.mrjulsen.ctt.util.CreateThreadedTrains;
import de.mrjulsen.ctt.commands.CTTCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class CreateThreadedTrainsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateThreadedTrains.init();

        ServerLifecycleEvents.SERVER_STARTING.register(CreateThreadedTrains::start);
        ServerLifecycleEvents.SERVER_STOPPING.register(CreateThreadedTrains::stop);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CTTCommands.register(dispatcher, environment);
        });
    }
}
