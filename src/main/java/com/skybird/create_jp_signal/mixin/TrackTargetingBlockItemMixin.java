package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;

@Mixin(value = TrackTargetingBlockItem.class, remap = false)
public class TrackTargetingBlockItemMixin {
    @ModifyArg(
        method = "useOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"
        ),
        index = 1,
        remap = true
    )
    private double create_jp_signal_modifyMaxDistance(double original) {
        return 2048.0;
    }
}
