package de.mrjulsen.ctt.mixin;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;

import de.mrjulsen.ctt.CreateThreadedTrains;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

@Mixin(Train.class)
public class TrainMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"), remap = false)
    private void onTickTrain(List<Carriage> carriages, Consumer<? super Carriage> consumer, Level level) {
        CreateThreadedTrains.getServer().ifPresent(x -> x.execute(() -> {            
            for (Carriage c : carriages) {
                c.manageEntities(level);
            }
        }));
    }
    
    @Redirect(method = "collideWithOtherTrains", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_254849_", remap = false))
    private Explosion redirectTrainExplosion(Level level, Entity source, double x, double y, double z, float power, ExplosionInteraction interaction) {
        CreateThreadedTrains.getServer().ifPresent(s -> s.execute(() -> {            
            level.explode(source, x, y, z, power, interaction);
        }));
        return null; // Not needed, because Create doesn't use the explosion result.
    }
    
}
