package de.mrjulsen.ctt.forge;

import de.mrjulsen.ctt.CreateThreadedTrains;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateThreadedTrains.MOD_ID)
public class CreateThreadedTrainsForge {
    public CreateThreadedTrainsForge() {
        EventBuses.registerModEventBus(CreateThreadedTrains.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CreateThreadedTrains.init();
    }
}
