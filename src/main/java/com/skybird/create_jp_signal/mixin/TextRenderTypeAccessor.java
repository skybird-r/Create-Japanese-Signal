package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public interface TextRenderTypeAccessor {
    @Accessor("state")
    net.minecraft.client.renderer.RenderType.CompositeState jpSignal$state();
}
