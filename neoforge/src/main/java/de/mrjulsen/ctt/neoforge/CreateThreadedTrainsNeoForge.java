package de.mrjulsen.ctt.neoforge;

import de.mrjulsen.ctt.CreateThreadedTrains;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CreateThreadedTrains.MOD_ID)
public class CreateThreadedTrainsNeoForge {

    public CreateThreadedTrainsNeoForge(ModContainer container) {
        CreateThreadedTrains.init();
    }
}
