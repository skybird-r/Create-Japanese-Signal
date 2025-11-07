package com.skybird.create_jp_signal.mixin;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.create.mixin_interface.ISignalBoundary;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;



@Mixin(value = Navigation.class, remap = false)
public abstract class NavigationMixin {
    
    @Shadow Train train;
    @Shadow public double distanceToDestination;
    @Shadow public Pair<UUID, Boolean> waitingForSignal;
    @Shadow private Map<UUID, Pair<SignalBoundary, Boolean>> waitingForChainedGroups;
    @Shadow public int ticksWaitingForSignal;
    @Shadow public double distanceToSignal;
    
    @Unique private Integer ticksWaitingBuffer;
    @Unique public Pair<SignalBoundary, Boolean> chainEndEntrySignal = null;

    


    // preDepartureLookAhead

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 4.5))
    private double create_jp_signal_changePreDepartureLookAhead(double originalValue) {
        return 20.0;
    }

    //scanDistance
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(DDD)D", // Lnet/minecraft/util/Mth;clamp(DDD)D = Lnet/minecraft/util/Mth;m_14008_(DDD)D
            ordinal = 0,
            remap = true
        )
    )
    private double create_jp_signal_redirectScanDistanceCalculation(double value, double min, double max) {
        // value = brakingDistanceNoFlicker
        // min   = preDepartureLookAhead
        // max   = this.distanceToDestination
        // return Mth.clamp(value + Math.abs(this.train.speed) * 20 * 5 + 30, min, max);
        double reservationDistance = Math.max(value, ((ITrain)train).getMinimumReservationDistance());
        return Mth.clamp(reservationDistance, min, max);
    }

    @ModifyVariable(
        method = "lambda$tick$0",
        index = 5,
        at = @At(
            value = "LOAD"
        )
    )
    private double create_jp_signal_modifyBrakingDistanceForReservation(
        double originalValue,
        MutableObject<Pair<UUID, Boolean>> trackingCrossSignal,
        double scanDistance,
        MutableDouble crossSignalDistanceTracker,
        double brakingDistanceNoFlicker,
        Double distance,
        Pair<TrackEdgePoint, Couple<TrackNode>> couple
    ) {
        return scanDistance;
    }

    
    @Inject(
        method = "lambda$tick$0",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/trains/signal/SignalEdgeGroup;reserved:Lcom/simibubi/create/content/trains/signal/SignalBoundary;",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void create_jp_signal_setReservedTrainMaxSpeed(
        MutableObject<Pair<UUID, Boolean>> trackingCrossSignal,
        double scanDistance,
        MutableDouble crossSignalDistanceTracker,
        double brakingDistanceNoFlicker,
        Double distance,
        Pair<TrackEdgePoint, Couple<TrackNode>> couple,
        CallbackInfoReturnable<Boolean> cir,
        boolean crossSignalTracked,
        Couple<TrackNode> nodes,
        TrackEdgePoint boundary,
        SignalBoundary signal,
        UUID entering,
        SignalEdgeGroup signalEdgeGroup,
        boolean primary
    ) {
        ((ISignalBoundary)signal).getReserverMaxSpeeds().set(primary, this.train.maxSpeed() * this.train.throttle);
        ((ISignalBoundary)signal).getReserverOperationTypes().set(primary, ((ITrain)this.train).getOperationType());
    }

    @Inject(
        method = "lambda$tick$0",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/entity/Navigation;reserveChain()V",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void create_jp_signal_setChainEndEntrySignal(
        MutableObject<Pair<UUID, Boolean>> trackingCrossSignal,
        double scanDistance,
        MutableDouble crossSignalDistanceTracker,
        double brakingDistanceNoFlicker,
        Double distance,
        Pair<TrackEdgePoint, Couple<TrackNode>> couple,
        CallbackInfoReturnable<Boolean> cir,
        boolean crossSignalTracked,
        Couple<TrackNode> nodes,
        TrackEdgePoint boundary,
        SignalBoundary signal,
        UUID entering,
        SignalEdgeGroup signalEdgeGroup,
        boolean primary,
        boolean crossSignal,
        boolean occupied
    ) {
        chainEndEntrySignal = Pair.of(signal, primary);
    }


    @Inject(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/trains/entity/Navigation;waitingForSignal:Lcom/simibubi/create/foundation/utility/Pair;",
            ordinal = 6
        )
    )
    private void create_jp_signal_addReservationUpdateLogic(Level level, CallbackInfo ci) {
        if (this.train.graph != null && this.waitingForSignal == null) {
            ((ITrain)train).getActiveReservations().forEach((groupId, signal) -> {
                SignalEdgeGroup group = Create.RAILWAYS.signalEdgeGroups.get(groupId);
                if (group != null && signal != null) {
                    SignalBoundary boundary = signal.getFirst();
                    if (boundary != null) {
                        ((ISignalBoundary)boundary).getReserverMaxSpeeds().set(signal.getSecond(), this.train.maxSpeed() * this.train.throttle);
                        ((ISignalBoundary)boundary).getReserverOperationTypes().set(signal.getSecond(), ((ITrain)this.train).getOperationType());
                        if (group.reserved == null) {
                            group.reserved = boundary;
                        }
                    }
                    
                }
            });
        }
    }


    @Inject(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/trains/entity/Navigation;waitingForSignal:Lcom/simibubi/create/foundation/utility/Pair;",
            ordinal = 7
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void create_jp_signal_addDepartureBuffer(
        Level level,
        CallbackInfo ci,
        double acceleration,
        double brakingDistance,
        double speedMod,
        double preDepartureLookAhead
    ) {
        if (this.ticksWaitingBuffer != null && this.ticksWaitingBuffer < ((ITrain)train).getTickWaitBeforeDeparture()) {
            if (waitingForSignal != null && distanceToSignal < preDepartureLookAhead) {
				ticksWaitingForSignal++;
				ci.cancel();
                return;
			}
            this.ticksWaitingBuffer++;
            ci.cancel();
            return;
        }
        this.ticksWaitingBuffer = null;
    }

    
    @Inject(
        method = "reserveChain", // 本当はlambda$reserveChain$2に突っ込みたいがstaticなのでむり
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;clear()V",
            shift = At.Shift.BEFORE
        )
    ) 
    private void create_jp_signal_onReserveChain(CallbackInfo ci) {
        waitingForChainedGroups.forEach((groupId, boundary) -> {
            SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(groupId);
            if (signalEdgeGroup != null) {
                ((ITrain)train).getActiveReservations().put(groupId, boundary);
                if (chainEndEntrySignal != null && chainEndEntrySignal.getFirst() != boundary.getFirst()){
                    ((ISignalBoundary)boundary.getFirst()).getNextEntrySignals().set(boundary.getSecond(), chainEndEntrySignal.copy());
                }
            }
            if (boundary.getFirst() != null) {
				((ISignalBoundary)boundary.getFirst()).getReserverMaxSpeeds().set(boundary.getSecond(), this.train.maxSpeed() * this.train.throttle);
                ((ISignalBoundary)boundary.getFirst()).getReserverOperationTypes().set(boundary.getSecond(), ((ITrain)this.train).getOperationType());
			} 
        });
        chainEndEntrySignal = null;
    }

    @Inject(
        method = "currentSignalResolved",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;clear()V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void create_jp_signal_onCurrentSignalResolved(CallbackInfoReturnable<Boolean> cir) {
        ((ITrain)train).getActiveReservations().clear();
    }

    @Inject(
        method = "cancelNavigation",
        at = @At("TAIL")
    )
    private void create_jp_signal_onCancelNavigationEnd(CallbackInfo ci) {
        ((ITrain)train).getActiveReservations().clear();
    }

    @Inject(
        method = "startNavigation",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;clear()V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void create_jp_signal_onStartNavigation1(CallbackInfoReturnable<Double> cir) {
        ((ITrain)train).getActiveReservations().clear();
    }

    @Inject(
        method = "startNavigation", 
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/entity/TrainStatus;foundConductor()V",
            shift = At.Shift.AFTER
        )
    )
    private void create_jp_signal_onStartNavigation2(CallbackInfoReturnable<Double> cir) {
        this.ticksWaitingBuffer = 0;
    }
}
