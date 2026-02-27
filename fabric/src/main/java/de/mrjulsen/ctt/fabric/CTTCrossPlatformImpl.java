package de.mrjulsen.ctt.fabric;

import de.mrjulsen.ctt.CreateThreadedTrains;
import de.mrjulsen.ctt.config.ModServerConfig;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import net.minecraftforge.fml.config.ModConfig;

public class CTTCrossPlatformImpl {
    public static void registerConfig() {
        ForgeConfigRegistryImpl.INSTANCE.register(CreateThreadedTrains.MOD_ID, ModConfig.Type.SERVER, ModServerConfig.SPEC, CreateThreadedTrains.MOD_ID + "-server.toml");
    }
}
