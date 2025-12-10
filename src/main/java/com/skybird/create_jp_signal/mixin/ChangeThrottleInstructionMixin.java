package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction;

@Mixin(value = ChangeThrottleInstruction.class, remap = false)
public class ChangeThrottleInstructionMixin {
    /**
     * withRange(5, 101)
     */
    @ModifyArg(
        method = "lambda$initConfigurationWidgets$1",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/gui/widget/ScrollInput;withRange(II)Lcom/simibubi/create/foundation/gui/widget/ScrollInput;"
        ),
        index = 1
    )
    private static int modifyMaxRange(int originalMax) {
        return 201;
    }

    @ModifyArg(
        method = "lambda$initConfigurationWidgets$1",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/gui/widget/ScrollInput;withRange(II)Lcom/simibubi/create/foundation/gui/widget/ScrollInput;"
        ),
        index = 0
    )
    private static int modifyMinRange(int originalMin) {
        return 10;
    }
}
