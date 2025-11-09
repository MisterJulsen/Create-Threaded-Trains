package de.mrjulsen.ctt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.foundation.events.CommonEvents;

import net.minecraft.world.level.Level;

@Mixin(CommonEvents.class)
public class CommonEventsMixin {

    @Redirect(method = "onServerWorldTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/GlobalRailwayManager;tick(Lnet/minecraft/world/level/Level;)V", remap = false))
    private static void disableGlobalManagerTick(GlobalRailwayManager manager, Level level) {
        // Disable default behaviour
	}
}
