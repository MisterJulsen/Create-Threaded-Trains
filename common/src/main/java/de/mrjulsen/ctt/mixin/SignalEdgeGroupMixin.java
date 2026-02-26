package de.mrjulsen.ctt.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import de.mrjulsen.ctt.NullSafeConcurrentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(SignalEdgeGroup.class)
public class SignalEdgeGroupMixin {

    @Shadow public Set<Train> trains;

    @Shadow public Map<UUID, UUID> intersecting;
    @Shadow public Set<SignalEdgeGroup> intersectingResolved;
    @Shadow public Set<UUID> adjacent;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.trains = ConcurrentHashMap.newKeySet();
        this.intersecting = new NullSafeConcurrentMap<>();
        this.intersectingResolved = ConcurrentHashMap.newKeySet();
        this.adjacent = ConcurrentHashMap.newKeySet();
    }
}
