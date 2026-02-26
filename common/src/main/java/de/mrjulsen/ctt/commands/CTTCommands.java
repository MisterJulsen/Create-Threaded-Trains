package de.mrjulsen.ctt.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.mrjulsen.ctt.CreateThreadedTrains;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TimeUtil;

import java.util.concurrent.TimeUnit;

public class CTTCommands {
    private static final String CMD_NAME = "ctt";

    private static final String SUB_LAST_TICK_TIME = "lastTickTime";
    private static final String SUB_AVG_TICK_TIME = "avgTickTime";
    private static final String SUB_CLEAR_AVG_TICK_TIME = "clearAvgTickTime";


    @SuppressWarnings("all")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {

        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(CMD_NAME)
                .then(Commands.literal(SUB_LAST_TICK_TIME)
                        .executes(x -> printLastTickTime(x.getSource()))
                )
                .then(Commands.literal(SUB_AVG_TICK_TIME)
                        .executes(x -> printAvgTickTime(x.getSource()))
                )
                .then(Commands.literal(SUB_CLEAR_AVG_TICK_TIME)
                        .executes(x -> clearAvgTickTime(x.getSource()))
                );

        dispatcher.register(builder);
    }

    private static int printLastTickTime(CommandSourceStack cmd) throws CommandSyntaxException {
        cmd.sendSuccess(() -> Component.literal("The last railway tick took: " + TimeUnit.NANOSECONDS.toMillis(CreateThreadedTrains.getLastTickTime()) + "ms"), true);
        return 1;
    }

    private static int printAvgTickTime(CommandSourceStack cmd) throws CommandSyntaxException {
        cmd.sendSuccess(() -> Component.literal("The average railway tick time is: " + TimeUnit.NANOSECONDS.toMillis(CreateThreadedTrains.getAvgTickTime()) + "ms"), true);
        return 1;
    }

    private static int clearAvgTickTime(CommandSourceStack cmd) throws CommandSyntaxException {
        cmd.sendSuccess(() -> Component.literal("Cleared the average railway tick time."), true);
        CreateThreadedTrains.clearAvgTickTime();
        return 1;
    }
}
