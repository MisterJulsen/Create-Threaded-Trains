package de.mrjulsen.ctt.mixin;

import java.util.function.BooleanSupplier;

import de.mrjulsen.ctt.config.ModServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.mrjulsen.ctt.util.CreateThreadedTrains;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Unique private long ctt$tps;

    @Inject(method = "tickServer", at = @At(value = "HEAD"))
    public void ctt$preTick(BooleanSupplier b, CallbackInfo ci) {
        CreateThreadedTrains.serverTickScheduled = true;
        ctt$tps = System.nanoTime();
        if (ModServerConfig.SYNC_WITH_SERVER_TICK.get()) {
            CreateThreadedTrains.preTick((MinecraftServer)(Object)this);
        } else if (CreateThreadedTrains.railwayTickScheduled) {
            CreateThreadedTrains.preTick((MinecraftServer)(Object)this);
        }
    }

    @Inject(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickChildren(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    public void ctt$postTick(BooleanSupplier b, CallbackInfo ci) {
        CreateThreadedTrains.postTick((MinecraftServer)(Object)this);
        CreateThreadedTrains.setTickTime(System.nanoTime() - ctt$tps);
    }

}
