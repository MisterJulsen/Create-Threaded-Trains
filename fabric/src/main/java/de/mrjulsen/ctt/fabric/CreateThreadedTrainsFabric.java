package de.mrjulsen.ctt.fabric;

import de.mrjulsen.ctt.CreateThreadedTrains;
import net.fabricmc.api.ModInitializer;

public class CreateThreadedTrainsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CreateThreadedTrains.init();
    }
}
