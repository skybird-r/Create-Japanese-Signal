package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.renderer.RenderType.CompositeState.class)
public interface TextCompositeStateAccessor {
    @Accessor("textureState")
    net.minecraft.client.renderer.RenderStateShard.EmptyTextureStateShard jpSignal$textureState();
}
