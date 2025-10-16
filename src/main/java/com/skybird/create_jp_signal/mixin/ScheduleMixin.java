package com.skybird.create_jp_signal.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.skybird.create_jp_signal.create.train.schedule.OperationTypeInstruction;

@Mixin(value = Schedule.class, remap = false)
public abstract class ScheduleMixin {

    @Invoker("registerInstruction")
    private static void create_jp_signal_callRegisterInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        throw new AssertionError();
    }

    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void create_jp_signal_onStaticInit(CallbackInfo ci) {
        create_jp_signal_callRegisterInstruction("operation_type", OperationTypeInstruction::new); 
    }
}
