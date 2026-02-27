package de.mrjulsen.ctt.mixin;

import java.util.Map;
import java.util.UUID;

import de.mrjulsen.ctt.util.NullSafeConcurrentMap;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;

@Mixin(RailwaySavedData.class)
public abstract class RailwaySavedDataMixin {

    @Shadow private Map<UUID, TrackGraph> trackNetworks;
    @Shadow private Map<UUID, SignalEdgeGroup> signalEdgeGroups;
    @Shadow private Map<UUID, Train> trains;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.trackNetworks = new NullSafeConcurrentMap<>();
        this.signalEdgeGroups = new NullSafeConcurrentMap<>();
        this.trains = new NullSafeConcurrentMap<>();
    }

    @Redirect(method = "load(Lnet/minecraft/nbt/CompoundTag;)Lcom/simibubi/create/content/trains/RailwaySavedData;", remap = false, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/RailwaySavedData;trackNetworks:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private static void ctt$trackNetworks(RailwaySavedData instance, Map<UUID, TrackGraph> value) {
        ((RailwaySavedDataMixin)(Object)instance).trackNetworks = new NullSafeConcurrentMap<>();
    }

    @Redirect(method = "load(Lnet/minecraft/nbt/CompoundTag;)Lcom/simibubi/create/content/trains/RailwaySavedData;", remap = false, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/RailwaySavedData;signalEdgeGroups:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private static void ctt$signalEdgeGroups(RailwaySavedData instance, Map<UUID, SignalEdgeGroup> value) {
        ((RailwaySavedDataMixin)(Object)instance).signalEdgeGroups = new NullSafeConcurrentMap<>();
    }

    @Redirect(method = "load(Lnet/minecraft/nbt/CompoundTag;)Lcom/simibubi/create/content/trains/RailwaySavedData;", remap = false, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/RailwaySavedData;trains:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private static void ctt$trains(RailwaySavedData instance, Map<UUID, Train> value) {
        ((RailwaySavedDataMixin)(Object)instance).trains = new NullSafeConcurrentMap<>();
    }
}


