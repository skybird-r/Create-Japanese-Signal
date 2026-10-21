package com.skybird.create_jp_signal.client.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

/** Shared text layout in font pixels. Slopes carry the sign, so labels use magnitudes. */
public final class GradientPostText {
    public record Glyph(String text, float x, float y, float scale) { }
    private static final float FRACTION_SCALE = 2F / 3;
    private GradientPostText() { }

    public static List<Glyph> glyphs(int tenths, boolean decimals, boolean useLetterO, ToIntFunction<String> advance) {
        if (tenths == 0) return List.of(new Glyph("L", -(advance.applyAsInt("L") - 1) / 2F, -4, 1));
        int magnitude = Math.abs(tenths);
        String main = Integer.toString(magnitude / 10);
        String text = decimals ? main + "." + magnitude % 10 : main;
        if (useLetterO) text = text.replace('0', 'O');
        float width = 0;
        for (int i = 0; i < text.length(); i++) {
            float scale = decimals && i == text.length() - 1 ? FRACTION_SCALE : 1;
            width += advance.applyAsInt(text.substring(i, i + 1)) * scale;
        }
        width -= decimals ? FRACTION_SCALE : 1;
        float x = -width / 2;
        List<Glyph> glyphs = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            String character = text.substring(i, i + 1);
            float scale = decimals && i == text.length() - 1 ? FRACTION_SCALE : 1;
            // The fractional digit shares the main text's top edge, at two-thirds size.
            glyphs.add(new Glyph(character, x, -4, scale));
            x += advance.applyAsInt(character) * scale;
        }
        return List.copyOf(glyphs);
    }
}
