package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;

@Mixin(value = TrackTargetingBlockItem.class, remap = false)
public class TrackTargetingBlockItemMixin {
    @ModifyConstant(method = "useOn", constant = @Constant(doubleValue = 16.0), remap = true) //useOn = m_6225_
	private double create_jp_signal_modifyMaxDistance1(double original) {
		return 2048.0;
	}
    @ModifyConstant(method = "useOn", constant = @Constant(doubleValue = 80.0), remap = true) //useOn = m_6225_
	private double create_jp_signal_modifyMaxDistance2(double original) {
		return 2048.0;
	}
}
