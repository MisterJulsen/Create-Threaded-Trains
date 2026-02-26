package de.mrjulsen.ctt.mixin;

import de.mrjulsen.ctt.NullSafeConcurrentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.GlobalRailwayManager;

@Mixin(GlobalRailwayManager.class)
public abstract class GlobalRailwayManagerMixin {

    @Inject(method = "cleanUp", remap = false, at = @At("RETURN"))
    private void onCleanup(CallbackInfo ci) {
        GlobalRailwayManager self = (GlobalRailwayManager)(Object)this;
		self.trackNetworks = new NullSafeConcurrentMap<>();
		self.signalEdgeGroups = new NullSafeConcurrentMap<>();
		self.trains = new NullSafeConcurrentMap<>();
    }
}


