package de.mrjulsen.ctt.mixin;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.mrjulsen.ctt.CreateThreadedTrains;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    
    @Inject(method = "runServer", at = @At(value = "HEAD"))
    public void ctt$start(CallbackInfo ci) {
        CreateThreadedTrains.start((MinecraftServer)(Object)this);
    }

    @Inject(method = "runServer", at = @At(value = "TAIL"))
    public void ctt$stop(CallbackInfo ci) {
        CreateThreadedTrains.stop((MinecraftServer)(Object)this);
    }

    @Inject(method = "tickServer", at = @At(value = "HEAD"))
    public void ctt$preTick(BooleanSupplier b, CallbackInfo ci) {
        CreateThreadedTrains.preTick((MinecraftServer)(Object)this);
    }

    @Inject(method = "tickServer", at = @At(value = "TAIL"))
    public void ctt$postTick(BooleanSupplier b, CallbackInfo ci) {
        CreateThreadedTrains.postTick((MinecraftServer)(Object)this);        
    }
    
}
