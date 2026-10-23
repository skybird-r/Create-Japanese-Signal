package com.skybird.create_jp_signal.block.signal;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/** Saved plain text and vertical writing cells, shared by both render backends. */
public final class SignalName {
    public static final int MAX_CODE_POINTS = 32;
    public record Cell(String text, boolean horizontalDigits) {}

    private SignalName() {}

    public static String normalize(String text) {
        if (text == null) return "";
        StringBuilder result = new StringBuilder();
        Normalizer.normalize(text, Normalizer.Form.NFC).codePoints()
            .filter(cp -> !Character.isISOControl(cp) && cp != 0xA7 && (cp < 0xD800 || cp > 0xDFFF))
            .limit(MAX_CODE_POINTS).forEach(result::appendCodePoint);
        return result.toString().strip();
    }

    public static List<Cell> cells(String name) {
        int[] points = normalize(name).codePoints().toArray();
        List<Cell> result = new ArrayList<>();
        for (int i = 0; i < points.length;) {
            int end = i;
            if (isDigit(points[i])) {
                while (end < points.length && isDigit(points[end])) end++;
            }
            if (end - i == 2) {
                result.add(new Cell(new String(points, i, 2), true));
                i = end;
            } else {
                // Runs of three or more digits stay vertical, as do single digits.
                int stop = Math.max(i + 1, end);
                while (i < stop) result.add(new Cell(new String(points, i++, 1), false));
            }
        }
        return List.copyOf(result);
    }

    private static boolean isDigit(int point) { return Character.getType(point) == Character.DECIMAL_DIGIT_NUMBER; }

    public static double boardHeightPixels(int cells) { return Math.max(6, 2 + 4 * cells); }
    public static double boxTopPixels(int lamps) { return 3.5 + 5 * lamps; }
    public static double backplateTopPixels(int lamps) { return 7 + 5 * lamps; }
    public static double boardBottomPixels(int lamps) { return backplateTopPixels(lamps) + 2; }
}
