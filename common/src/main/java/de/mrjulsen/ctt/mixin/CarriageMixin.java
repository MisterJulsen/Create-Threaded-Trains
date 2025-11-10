package de.mrjulsen.ctt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.trains.entity.Carriage;

import de.mrjulsen.ctt.CreateThreadedTrains;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.world.level.Level;

@Mixin(Carriage.class)
public class CarriageMixin {

    @PlatformOnly(PlatformOnly.FORGE)
    @Redirect(method = "travel", remap = false, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Carriage;manageEntities(Lnet/minecraft/world/level/Level;)V", remap = false))
    public void onTravelForge(Carriage carriage, Level level) {
        CreateThreadedTrains.getServer().ifPresent(x -> x.execute(() -> {
            carriage.manageEntities(level);
        }));
    }
    
    @PlatformOnly(PlatformOnly.FABRIC)
    @Redirect(method = "travel", remap = false, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Carriage;manageEntities(Lnet/minecraft/class_1937;)V", remap = false))
    public void onTravelFabric(Carriage carriage, Level level) {
        CreateThreadedTrains.getServer().ifPresent(x -> x.execute(() -> {
            carriage.manageEntities(level);
        }));
    }
}
