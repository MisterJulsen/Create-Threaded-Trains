package de.mrjulsen.ctt.forge;
import de.mrjulsen.ctt.CreateThreadedTrains;
import de.mrjulsen.ctt.commands.CTTCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateThreadedTrains.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CTTCommands.register(event.getDispatcher(), event.getCommandSelection());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        CreateThreadedTrains.start(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        CreateThreadedTrains.stop(event.getServer());
    }
}
