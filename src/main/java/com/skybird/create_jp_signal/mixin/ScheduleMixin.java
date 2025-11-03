package com.skybird.create_jp_signal.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.train.schedule.MinimumReservationDistanceInstruction;
import com.skybird.create_jp_signal.create.train.schedule.OperationTypeInstruction;

import net.minecraft.resources.ResourceLocation;

@Mixin(value = Schedule.class, remap = false)
public abstract class ScheduleMixin {

    @Shadow public static List<Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>>> INSTRUCTION_TYPES;
	@Shadow public static List<Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>>> CONDITION_TYPES;


    // @Invoker("registerInstruction")
    // private static void create_jp_signal_callRegisterInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
    //     throw new AssertionError();
    // }

    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void create_jp_signal_onStaticInit(CallbackInfo ci) {
        INSTRUCTION_TYPES.add(Pair.of(new ResourceLocation(JpSignals.MODID, "operation_type"), OperationTypeInstruction::new)); 
        INSTRUCTION_TYPES.add(Pair.of(new ResourceLocation(JpSignals.MODID, "minimum_reservation_distance"), MinimumReservationDistanceInstruction::new)); 
    }
}