package de.mrjulsen.ctt.mixin;

import com.simibubi.create.content.trains.entity.Train;
import de.mrjulsen.ctt.util.ConcurrentList;
import de.mrjulsen.ctt.util.NullSafeConcurrentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.GlobalRailwayManager;

import java.util.*;

@Mixin(GlobalRailwayManager.class)
public abstract class GlobalRailwayManagerMixin {

    @Shadow private List<Train> movingTrains;
    @Shadow private List<Train> waitingTrains;

    @Inject(method = "cleanUp", remap = false, at = @At("RETURN"))
    private void ctt$cleanUp(CallbackInfo ci) {
        GlobalRailwayManager self = (GlobalRailwayManager)(Object)this;
		self.trackNetworks = new NullSafeConcurrentMap<>();
		self.signalEdgeGroups = new NullSafeConcurrentMap<>();
		self.trains = new NullSafeConcurrentMap<>();
		this.movingTrains = new ConcurrentList<>();
		this.waitingTrains = new ConcurrentList<>();
    }
}


