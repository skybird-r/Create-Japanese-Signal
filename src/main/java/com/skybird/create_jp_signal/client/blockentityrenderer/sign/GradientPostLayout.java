package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.sign.GradientPostBlockEntity;
import com.skybird.create_jp_signal.client.text.GradientPostText;

/** Shared normal and tunnel placement for Flywheel and the BER fallback. */
public final class GradientPostLayout {
    private static final double PIVOT_Y = 12.5 / 16;
    private static final double TEXT_X = -6.0 / 16;
    private static final double TEXT_Z = 2.0 / 16 + 1.0 / 128;
    private static final double TUNNEL_ROOT_SHIFT = 2.5 / 16;
    // Main text is 2.25px high in a 3px-high bar; fractional digits are 1.5px high.
    private static final float FONT_SCALE = 2.25F / (16 * 8);
    private GradientPostLayout() { }

    public static void placement(PoseStack pose, GradientPostBlockEntity post) {
        pose.translate(post.getXPos() / 16.0, 0, post.getZPos() / 16.0);
        pose.mulPose(Axis.YP.rotationDegrees(180 - post.getRotation() * 22.5F));
    }

    public static void barRotation(PoseStack pose, int tenths, int side, boolean tunnelMode) {
        float angle = tenths == 0 ? 0 : tenths > 0 ? -30 : 30;
        // In tunnel mode the second bar extends right; positive grades still rise outward.
        if (tunnelMode && side == 1) angle = -angle;
        pose.translate(0, PIVOT_Y, 0);
        pose.mulPose(Axis.ZP.rotationDegrees(angle));
        pose.translate(0, -PIVOT_Y, 0);
    }

    public static void barModel(PoseStack pose, int side, boolean tunnelMode) {
        if (tunnelMode) {
            // Both white faces point forward, just in front of the pillar. Move each root
            // outward so even tilted board corners remain within the pillar's width.
            pose.translate(side == 0 ? -TUNNEL_ROOT_SHIFT : TUNNEL_ROOT_SHIFT, 0, 0);
            if (side == 1) {
                pose.translate(0, PIVOT_Y, 0);
                pose.mulPose(Axis.ZP.rotationDegrees(180));
                pose.translate(0, -PIVOT_Y, 0);
            }
            pose.translate(-.5, 0, -.5);
            return;
        }
        // Turn the rear bar around its longitudinal axis: its black inner face
        // must face the post, and its white outer face must face the rear text.
        // The bar is vertically symmetric about PIVOT_Y, so its bounds stay the same.
        if (side == 1) {
            pose.translate(0, PIVOT_Y, 0);
            pose.mulPose(Axis.XP.rotationDegrees(180));
            pose.translate(0, -PIVOT_Y, 0);
        }
        pose.translate(-.5, 0, -.5);
    }

    public static void textFace(PoseStack pose, int side, boolean tunnelMode) {
        if (tunnelMode) {
            pose.translate(side == 0 ? TEXT_X - TUNNEL_ROOT_SHIFT : -TEXT_X + TUNNEL_ROOT_SHIFT,
                PIVOT_Y, TEXT_Z);
            return;
        }
        pose.translate(TEXT_X, PIVOT_Y, side == 0 ? TEXT_Z : -TEXT_Z);
        if (side == 1) pose.mulPose(Axis.YP.rotationDegrees(180));
    }

    public static void glyph(PoseStack pose, GradientPostText.Glyph glyph) {
        pose.translate(glyph.x() * FONT_SCALE, -glyph.y() * FONT_SCALE, 0);
        float scale = FONT_SCALE * glyph.scale();
        pose.scale(scale, -scale, scale);
    }
}
