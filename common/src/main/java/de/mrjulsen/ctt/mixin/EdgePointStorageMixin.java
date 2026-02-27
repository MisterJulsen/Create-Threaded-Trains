package de.mrjulsen.ctt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.graph.EdgePointStorage;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import de.mrjulsen.ctt.util.NullSafeConcurrentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Mixin(EdgePointStorage.class)
public class EdgePointStorageMixin {

    @Shadow private Map<EdgePointType<?>, Map<UUID, TrackEdgePoint>> pointsByType;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ctt$init(CallbackInfo ci) {
        this.pointsByType = new NullSafeConcurrentMap<>();
    }

    @WrapOperation(
            method = "getMap",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"
            )
    )
    private Object ctt$wrapComputeIfAbsent(Map<EdgePointType<?>, Map<UUID, TrackEdgePoint>> instance, Object type, Function<?, ?> original, Operation<Object> operation) {
        return instance.computeIfAbsent(
                (EdgePointType<?>) type,
                t -> new NullSafeConcurrentMap<>()
        );
    }
}
