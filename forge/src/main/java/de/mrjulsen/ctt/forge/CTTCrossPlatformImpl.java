package de.mrjulsen.ctt.forge;

import de.mrjulsen.ctt.CreateThreadedTrains;
import de.mrjulsen.ctt.config.ModServerConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CTTCrossPlatformImpl {
    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModServerConfig.SPEC, CreateThreadedTrains.MOD_ID + "-server.toml");
    }
}
