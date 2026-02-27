package de.mrjulsen.ctt.mixin;

import com.simibubi.create.content.trains.graph.*;
import de.mrjulsen.ctt.ConcurrentList;
import de.mrjulsen.ctt.util.NullSafeConcurrentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(TrackGraph.class)
public class TrackGraphMixin {

    @Shadow Map<TrackNodeLocation, TrackNode> nodes;
    @Shadow Map<Integer, TrackNode> nodesById;
    @Shadow Map<TrackNode, Map<TrackNode, TrackEdge>> connectionsByNode;
    @Shadow EdgePointStorage edgePoints;
    @Shadow Map<ResourceKey<Level>, TrackGraphBounds> bounds;

    @Shadow List<TrackEdge> deferredIntersectionUpdates;

    @Inject(method = "<init>(Ljava/util/UUID;)V", at = @At("RETURN"))
    public void ctt$onCreateTrackGraph(UUID graphID, CallbackInfo ci) {
        this.nodes = new NullSafeConcurrentMap<>();
        this.nodesById = new NullSafeConcurrentMap<>();
        this.bounds = new NullSafeConcurrentMap<>();
        this.connectionsByNode = Collections.synchronizedMap(new IdentityHashMap<>());
        this.edgePoints = new EdgePointStorage();
        this.deferredIntersectionUpdates = new ConcurrentList<>();
    }
}
