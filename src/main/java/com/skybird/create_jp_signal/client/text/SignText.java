package com.skybird.create_jp_signal.client.text;

import com.skybird.create_jp_signal.JpSignals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/** Font selection shared by the Flywheel mesh builder and the BER fallback. */
public final class SignText {
    public enum FontMode { CURRENT, MINECRAFT_ENGLISH }
    private static final ResourceLocation ENGLISH = JpSignals.asResource("english");

    private SignText() { }

    public static Font font() { return Minecraft.getInstance().font; }

    public static FormattedCharSequence sequence(String text, FontMode mode) {
        return sequence(Component.literal(text).getVisualOrderText(), mode);
    }

    public static FormattedCharSequence sequence(FormattedCharSequence text, FontMode mode) {
        if (mode == FontMode.CURRENT) return text;
        // A separate font ID avoids Minecraft's force-Unicode remapping of minecraft:default.
        return sink -> text.accept((index, style, codePoint) -> sink.accept(index, style.withFont(ENGLISH), codePoint));
    }
}
