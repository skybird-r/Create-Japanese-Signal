package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.sign.KilometerPostBlockEntity;

/** Model coordinates are preserved: the black bottom 2px intentionally sit below ground. */
public final class KilometerPostLayout {
    public static final float DIGIT_HEIGHT = 4F / 16;
    public static final float FONT_SCALE = DIGIT_HEIGHT / 8;
    public static final double TEXT_CENTER_Y = (18 - 7) / 16.0;
    private static final double HALF_SIDE = Math.sqrt(2) / 16;
    // Lift text 1/8 of a model pixel along the face normal to avoid depth fighting.
    private static final double TEXT_OFFSET = 1.0 / 128;
    private KilometerPostLayout() { }

    public static void placement(PoseStack pose, KilometerPostBlockEntity marker) {
        pose.translate(marker.getXPos() / 16.0, 0, marker.getZPos() / 16.0);
        pose.mulPose(Axis.YP.rotationDegrees(180 - marker.getRotation() * 22.5F));
    }

    public static void face(PoseStack pose, int side) {
        double sign = side == 0 ? 1 : -1;
        pose.translate(sign * HALF_SIDE, TEXT_CENTER_Y, HALF_SIDE);
        pose.mulPose(Axis.YP.rotationDegrees(side == 0 ? 45 : -45));
        pose.translate(0, 0, TEXT_OFFSET);
    }

    public static float digitCenterX(int advance) {
        // Bitmap glyph quads include transparent right padding. Use the font's
        // advance minus its 1px inter-character spacing for both render paths.
        return (advance - 1) / 2F;
    }

    public static void digit(PoseStack pose, int index, int count, float centerX, float centerY) {
        pose.translate(0, ((count - 1) / 2.0 - index) * DIGIT_HEIGHT, 0);
        pose.scale(FONT_SCALE, -FONT_SCALE, FONT_SCALE);
        pose.translate(-centerX, -centerY, 0);
    }
}
