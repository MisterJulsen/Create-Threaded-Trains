package de.mrjulsen.ctt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.foundation.events.CommonEvents;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.world.level.Level;

@Mixin(CommonEvents.class)
public class CommonEventsMixin {

    @PlatformOnly(PlatformOnly.FORGE)
    @Redirect(method = "onServerWorldTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/GlobalRailwayManager;tick(Lnet/minecraft/world/level/Level;)V", remap = false))
    private static void disableGlobalManagerTickForge(GlobalRailwayManager manager, Level level) {
        // Disable default behaviour
	}
    
    @PlatformOnly(PlatformOnly.FABRIC)
    @Redirect(method = "onServerWorldTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/GlobalRailwayManager;tick(Lnet/minecraft/world/level/Level;)V"))
    private static void disableGlobalManagerTickFabric(GlobalRailwayManager manager, Level level) {
        // Disable default behaviour
	}
}
