package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime.State;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;
import com.skybird.create_jp_signal.create.train.schedule.OperationTypeInstruction;

@Mixin(value = ScheduleRuntime.class, remap = false)
public abstract class ScheduleRuntimeMixin {

    @Shadow Train train;
    @Shadow public State state;
    @Shadow public int currentEntry;
    
    @Inject(
        method = "startCurrentInstruction",
        at = @At("TAIL"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void create_jp_signal_onStartCurrentInstructionEnd(
        CallbackInfoReturnable<DiscoveredPath> cir,
        ScheduleEntry entry,
		ScheduleInstruction instruction

    ) {
        if (instruction instanceof OperationTypeInstruction operation) {
			((ITrain)train).setOperationType(operation.getOperationType());
			state = State.PRE_TRANSIT;
			currentEntry++;
			cir.setReturnValue(null);
		}
    }

}
