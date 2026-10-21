package com.skybird.create_jp_signal.mixin;

import net.minecraft.client.gui.font.FontTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontTexture.class)
public interface FontTextureAccessor {
    @Accessor("colored") boolean jpSignal$isColored();
}
