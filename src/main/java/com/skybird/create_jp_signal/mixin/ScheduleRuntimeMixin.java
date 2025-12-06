package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime.State;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;
import com.skybird.create_jp_signal.create.train.schedule.MinimumReservationDistanceInstruction;
import com.skybird.create_jp_signal.create.train.schedule.OperationTypeInstruction;

@Mixin(value = ScheduleRuntime.class, remap = false)
public abstract class ScheduleRuntimeMixin {

    @Shadow Train train;
    @Shadow public State state;
    @Shadow public int currentEntry;
    @Shadow Schedule schedule;
    
    @Inject(
        method = "startCurrentInstruction",
        at = @At("HEAD"),
        cancellable = true
    )
    private void create_jp_signal_onStartCurrentInstructionEnd(CallbackInfoReturnable<DiscoveredPath> cir) {
        
        ScheduleEntry entry = schedule.entries.get(currentEntry);
		ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof OperationTypeInstruction operation) {
			((ITrain)train).setOperationType(operation.getOperationType());
			state = State.PRE_TRANSIT;
			currentEntry++;
			cir.setReturnValue(null);
		}
        if (instruction instanceof MinimumReservationDistanceInstruction reservationInstruction) {
			((ITrain)train).setMinimumReservationDistance(reservationInstruction.getMinimumReservationDistance());
			state = State.PRE_TRANSIT;
			currentEntry++;
			cir.setReturnValue(null);
		}
    }

}
