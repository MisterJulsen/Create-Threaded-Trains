package de.mrjulsen.ctt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.simibubi.create.content.trains.GlobalRailwayManager;

import net.minecraft.world.level.Level;

@Mixin(GlobalRailwayManager.class)
public interface GlobalRailwayManagerAccessor {

    @Invoker("tickTrains")
    void invokeTickTrains(Level level);
}
