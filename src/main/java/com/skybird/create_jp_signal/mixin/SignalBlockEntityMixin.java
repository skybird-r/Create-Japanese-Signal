package com.skybird.create_jp_signal.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity.SignalState;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.skybird.create_jp_signal.block.signal.ISignalIndexSource;
import com.skybird.create_jp_signal.create.mixin_interface.ISignalBoundary;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(value = SignalBlockEntity.class, remap = false)
public abstract class SignalBlockEntityMixin implements ISignalIndexSource {


    // @Unique int redSignalIndex = 0;
    // @Unique double reserverMaxSpeed = 0.0;

    //@Shadow protected Level level;
    //@Shadow protected BlockPos worldPosition;

    @Shadow public abstract SignalState getState();
    @Shadow public abstract SignalBoundary getSignal();

    // @Inject(
    //     method = "tick",
    //     at = @At("HEAD")
    // )
    // private void create_jp_signal_onTickHead(CallbackInfo ci) {
    //     if (!(((BlockEntity)(Object)this).getLevel().isClientSide)){
    //         // redSignalIndex = 0;
    //         // reserverMaxSpeed = 0.0;
	// 	}
    // }

    // @Inject(
    //     method = "tick",
    //     at = @At("TAIL")
    // )
    // private void create_jp_signal_onTickEnd(CallbackInfo ci) {
    //     SignalBoundary boundary = getSignal();
    //     if (getState() != SignalState.INVALID){
	// 		TrackGraph graph = Create.RAILWAYS.getGraph(((BlockEntity)(Object)this).getLevel(), boundary.edgeLocation.getFirst());
	// 		if (graph != null) {

	// 			// redSignalIndex = ((ISignalBoundary)boundary).getNextRedIndex(graph, ((BlockEntity)(Object)this).getBlockPos());
    //             // reserverMaxSpeed = ((ISignalBoundary)boundary).getReserverMaxSpeed(graph, ((BlockEntity)(Object)this).getBlockPos());


				

	// 			// if (boundary.getStateFor(worldPosition) != SignalState.RED) {
	// 			// 	if (redSignalIndex == 1)
	// 			// 		enterState(SignalState.YELLOW);
	// 			// }

	// 			// if (boundary.getStateFor(worldPosition) != SignalState.RED) {
	// 			// 	if (redSignalIndex == 1)
	// 			// 		enterState(SignalState.YELLOW);
	// 			// }
	// 		}
	// 	}
    // }

    @Override
    public int getRedSignalIndex(int max) {
        SignalBoundary boundary = getSignal();
        if (boundary == null) {
            return 0;
        }
        TrackGraph graph = Create.RAILWAYS.getGraph(((BlockEntity)(Object)this).getLevel(), boundary.edgeLocation.getFirst());
        if (graph == null) {
            return 0;
        } else {
            return ((ISignalBoundary)boundary).getNextRedIndex(graph, ((BlockEntity)(Object)this).getBlockPos(), max);
        } 
    }

    @Override
    public boolean isRed() {
        SignalBoundary boundary = getSignal();
        if (boundary == null) return true;
        return ((ISignalBoundary)boundary).isRed(((BlockEntity)(Object)this).getBlockPos());
    }

    @Override
    public double getReserverMaxSpeed() {
        SignalBoundary boundary = getSignal();
        if (boundary == null) {
            return 0;
        }
        return ((ISignalBoundary)boundary).getReserverMaxSpeed(((BlockEntity)(Object)this).getBlockPos());
        
    }

    @Override
    public OperationType getReserverOperationType() {
        SignalBoundary boundary = getSignal();
        if (boundary == null) {
            return OperationType.TRAIN;
        }
        return ((ISignalBoundary)boundary).getReserverOperationType(((BlockEntity)(Object)this).getBlockPos());
    }
}
