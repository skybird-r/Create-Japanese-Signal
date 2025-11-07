package com.skybird.create_jp_signal.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import net.minecraft.nbt.CompoundTag;

@Mixin(value = Train.class, remap = false)
public abstract class TrainMixin implements ITrain {
    
    @Unique public Map<UUID, Pair<SignalBoundary, Boolean>> activeReservations;
    @Unique public OperationType operationType;
    @Unique public double minimumReservationDistance;
    @Unique public int tickWaitBeforeDeparture;

    @Inject(
        method = "<init>",
        at = @At("RETURN") 
    )
    private void create_jp_signal_onConstructorEnd(CallbackInfo ci) {
        this.activeReservations = new HashMap<>();
        this.operationType = OperationType.TRAIN;
        this.minimumReservationDistance = 500;
        this.tickWaitBeforeDeparture = 40;
    }

    @Inject(
        method = "occupy",
        at = @At("HEAD")
    )
    private void create_jp_signal_onOccupyHead(UUID groupId, @Nullable UUID boundaryId, CallbackInfoReturnable<Boolean> cir) {
        activeReservations.remove(groupId);
    }

    @Inject(
        method = "arriveAt",
        at = @At("HEAD")
    )
    private void create_jp_signal_onArriveAtHead(GlobalStation station, CallbackInfo ci) {
        activeReservations.clear();
    }

    @Inject(
        method = "collectInitiallyOccupiedSignalBlocks",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;clear()V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void onCollectInitiallyOccupied(CallbackInfo ci) {
        this.activeReservations.clear();
    }

    @Inject(
        method = "write",
        at = @At("RETURN"),
        remap = false 
    )
    private void create_jp_signal_onWriteEnd(
        DimensionPalette dimensions,
        CallbackInfoReturnable<CompoundTag> cir
    ) {
        CompoundTag tag = cir.getReturnValue();
        if (tag == null) return;
        tag.putString("OperationType", operationType.name());
        tag.putDouble("MinimumReservationDistance", minimumReservationDistance);
    }

    @Inject(
        method = "read",
        at = @At("RETURN"),
        remap = false 
    )
    private static void create_jp_signal_onReadEnd(
        CompoundTag tag,
        Map<UUID, TrackGraph> trackNetworks, 
        DimensionPalette dimensions,
        CallbackInfoReturnable<Train> cir
    ) {
        Train train = cir.getReturnValue();
        if (train == null) return;
        if (tag.contains("OperationType")) {
            try {
                ((ITrain)train).setOperationType(OperationType.valueOf(tag.getString("OperationType")));
            } catch (IllegalArgumentException e) {
            }
        }
        if (tag.contains("MinimumReservationDistance")) {
            try {
                ((ITrain)train).setMinimumReservationDistance(tag.getDouble("MinimumReservationDistance"));
            } catch (IllegalArgumentException e) {
            }
        }
    }

    public OperationType getOperationType() {
        return this.operationType;
    }

    public void setOperationType(OperationType type) {
        this.operationType = type;
    }

    public double getMinimumReservationDistance() {
        return minimumReservationDistance;
    }

    public void setMinimumReservationDistance(double minimumReservationDistance) {
        this.minimumReservationDistance = minimumReservationDistance;
    }

    public int getTickWaitBeforeDeparture() {
        return tickWaitBeforeDeparture;
    }
    
    public void setTickWaitBeforeDeparture(int tickWaitBeforeDeparture) {
        this.tickWaitBeforeDeparture = tickWaitBeforeDeparture;
    }

    public Map<UUID, Pair<SignalBoundary, Boolean>> getActiveReservations() {
        return this.activeReservations;
    }
    
}
