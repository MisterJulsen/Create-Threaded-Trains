package de.mrjulsen.ctt.config;

import de.mrjulsen.ctt.CreateThreadedTrains;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SYNC_WITH_SERVER_TICK;

    static {
        BUILDER.push(CreateThreadedTrains.MOD_ID + "_common_config");
        BUILDER.comment("These settings determine the default time system in DragonLib. Mods can use a fixed time system, meaning these settings may not always work. Datapacks can override all these settings!");

        SYNC_WITH_SERVER_TICK = BUILDER.comment("Synchronizes the Global Railway Manager tick with the server tick. When one task is complete, the system waits until the other task is also complete before starting the next game tick. The TPS is determined by the process that takes the longest. If this option is disabled, each process operates at its own pace. This can cause the Global Railway Manager to become out of sync, especially with a very large number of trains, where the server tick might have already calculated 100 ticks, while the Global Railway Manager has only calculated 80 (for example). This is usually only noticeable with slower-moving trains. The TPS is significantly better when synchronization is disabled; however, use this option is at your own risk, as it can have unexpected side effects.")
                .define("sync_with_server_tick", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}