package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.renderer.RenderStateShard.EmptyTextureStateShard.class)
public interface TextTextureStateAccessor {
    @Invoker("cutoutTexture")
    java.util.Optional<net.minecraft.resources.ResourceLocation> jpSignal$cutoutTexture();
}
