package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.sign.RailwaySignSettings;
import com.skybird.create_jp_signal.block.sign.RailwaySignSettings.Variant;
import com.skybird.create_jp_signal.block.sign.RailwaySignType;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

/** Models are centered on (8, Y, 8), with the board back flush with the pole's +Z face. */
public final class RailwaySignLayout {
    public record Digit(String text, float x, float scaleX, float fontScale) { }
    public static final double POLE_BOTTOM = -14.0 / 16;
    private static final float FONT_SCALE = 6F / (16 * 8);
    private RailwaySignLayout() { }
    public static void placement(PoseStack pose, RailwaySignSettings settings) {
        pose.translate(settings.xPos() / 16.0, 0, settings.zPos() / 16.0);
        pose.mulPose(Axis.YP.rotationDegrees(180 - settings.rotation() * 22.5F));
    }
    public static void pole(PoseStack pose, RailwaySignSettings settings, RailwaySignType type) {
        if (type.numberedMast()) {
            pose.translate(-.5, 0, -.5);
            return;
        }
        double top = settings.bottomHeight() + type.height() / 16.0;
        pose.translate(-.5, POLE_BOTTOM, -.5);
        pose.scale(1, (float)(top - POLE_BOTTOM), 1);
    }
    public static PartialModel poleModel(RailwaySignType type) {
        return type.numberedMast() ? PartialModelRegistry.SIGNAL_MAST : PartialModelRegistry.SIGN_POST;
    }
    public static void board(PoseStack pose, RailwaySignSettings settings) {
        board(pose, settings, RailwaySignType.SPEED_LIMIT);
    }
    public static void board(PoseStack pose, RailwaySignSettings settings, RailwaySignType type) {
        pose.translate(-.5, type.boardBottom(settings), -.5);
    }
    public static void numberFace(PoseStack pose, RailwaySignSettings settings) {
        numberFace(pose, settings, RailwaySignType.SPEED_LIMIT);
    }
    public static void numberFace(PoseStack pose, RailwaySignSettings settings, RailwaySignType type) {
        pose.translate(0, type.boardBottom(settings) + type.height() / 32.0,
            (type.numberedMast() ? 2.25 : 1.0) / 16 + 1.0 / 128);
    }
    public static void digit(PoseStack pose, Digit digit) {
        pose.scale(digit.fontScale() * digit.scaleX(), -digit.fontScale(), digit.fontScale());
        pose.translate(digit.x(), -4, 0);
    }
    public static List<Digit> digits(int speed, boolean useLetterO, ToIntFunction<String> advance) {
        return digits(speed, useLetterO, advance, false);
    }
    public static List<Digit> numberPlateDigits(int number, boolean useLetterO, ToIntFunction<String> advance) {
        return digits(number, useLetterO, advance, true);
    }
    private static List<Digit> digits(int value, boolean useLetterO, ToIntFunction<String> advance, boolean plate) {
        String text = Integer.toString(value);
        if (useLetterO) text = text.replace('0', 'O');
        int width = -1;
        for (int i = 0; i < text.length(); i++) width += advance.applyAsInt(text.substring(i, i + 1));
        // Keep one/two digits at full size. Three digits start at 3/4 width;
        // wider glyphs such as O may need further compression to fit the 12px board.
        float fontScale = plate ? 4F / (16 * 8) : FONT_SCALE;
        float scaleX = text.length() >= (plate ? 2 : 3)
            ? Math.min(plate ? .9F : .75F, (plate ? 4.5F : 12F) / (width * fontScale * 16)) : 1;
        float x = -width / 2F;
        List<Digit> digits = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            String character = text.substring(i, i + 1);
            digits.add(new Digit(character, x, scaleX, fontScale)); x += advance.applyAsInt(character);
        }
        return List.copyOf(digits);
    }
    public static PartialModel model(RailwaySignType type, Variant variant) {
        return switch (type) {
            case SPEED_LIMIT -> switch (variant) {
                case NORMAL -> PartialModelRegistry.SPEED_LIMIT_SIGN;
                case RIGHT -> PartialModelRegistry.SPEED_LIMIT_SIGN_RIGHT;
                case LEFT -> PartialModelRegistry.SPEED_LIMIT_SIGN_LEFT;
            };
            case SPEED_LIMIT_END -> PartialModelRegistry.SPEED_LIMIT_END_SIGN;
            case TRAIN_STOP_LIMIT -> PartialModelRegistry.TRAIN_STOP_LIMIT_SIGN;
            case SHUNT_LIMIT -> PartialModelRegistry.SHUNT_LIMIT_SIGN;
            case TRACK_END -> PartialModelRegistry.TRACK_END_SIGN;
            case BLOCK_SIGNAL_NO_PLATE -> PartialModelRegistry.BLOCK_SIGNAL_NO_PLATE;
        };
    }
}
