package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;

/** Executable geometry checks against the actual OBJ and shared rendering transforms. */
public final class GradientPostLayoutTest {
    private static final float EPSILON = 0.00001F;

    public static void main(String[] args) throws Exception {
        var vertices = new ArrayList<Vector3f>();
        for (String line : Files.readAllLines(Path.of(
                "src/main/resources/assets/create_jp_signal/models/block/sign/gradient_post_bar.obj"))) {
            if (!line.startsWith("v ")) continue;
            String[] values = line.split("\\s+");
            vertices.add(new Vector3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
        }
        require(vertices.size() == 8, "Expected the supplied bar's eight vertices");
        int checks = 0;
        for (boolean tunnel : new boolean[] {false, true}) {
            for (int side = 0; side < 2; side++) {
                for (int value : new int[] {-9999, -1, 0, 1, 9999}) {
                    PoseStack model = new PoseStack();
                    GradientPostLayout.barRotation(model, value, side, tunnel);
                    GradientPostLayout.barModel(model, side, tunnel);
                    float minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
                    for (Vector3f vertex : vertices) {
                        Vector3f point = model.last().pose().transformPosition(new Vector3f(vertex));
                        minZ = Math.min(minZ, point.z); maxZ = Math.max(maxZ, point.z);
                        if (tunnel) {
                            require(point.z >= 1.5F / 16 - EPSILON && point.z <= 2F / 16 + EPSILON, "Board must be in front of the pillar");
                            require(side == 0 ? point.x <= 1.5F / 16 + EPSILON : point.x >= -1.5F / 16 - EPSILON,
                                "Board root must not protrude past the opposite side of the pillar");
                            require(Math.abs(point.x) <= 12.5F / 16 + EPSILON, "Board exceeds hitbox width");
                            require(point.y >= 4.75F / 16 - EPSILON && point.y <= 20.25F / 16 + EPSILON,
                                "Board exceeds hitbox height");
                        }
                    }
                    Vector3f whiteNormal = model.last().pose().transformDirection(new Vector3f(0, 0, 1));
                    require(tunnel || side == 0 ? whiteNormal.z > .99F : whiteNormal.z < -.99F,
                        "White face points in the wrong direction");
                    Vector3f tip = model.last().pose().transformPosition(new Vector3f(-.125F, 12.5F / 16, .609375F));
                    require(value == 0 ? Math.abs(tip.y - 12.5F / 16) < EPSILON
                        : value > 0 ? tip.y > 12.5F / 16 : tip.y < 12.5F / 16,
                        "Grade sign must control outward rise/fall on both boards");
                    PoseStack text = new PoseStack();
                    GradientPostLayout.barRotation(text, value, side, tunnel);
                    GradientPostLayout.textFace(text, side, tunnel);
                    Vector3f center = text.last().pose().transformPosition(new Vector3f());
                    require(tunnel || side == 0 ? center.z > maxZ : center.z < minZ,
                        "Text must sit in front of the white face");
                    checks++;
                }
            }
        }
        System.out.println("Verified " + checks + " actual bar/text transforms: normal and tunnel modes, both sides, slopes, face orientation and bounds.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
