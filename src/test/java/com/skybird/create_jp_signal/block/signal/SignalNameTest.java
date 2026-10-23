package com.skybird.create_jp_signal.block.signal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.SignalNameBoardLayout;
import com.skybird.create_jp_signal.client.text.FlywheelText.TextModel;
import com.skybird.create_jp_signal.network.SetSignalNamesPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.createmod.catnip.data.Pair;
import org.joml.Vector3f;

/** Run with verifySignalNames; verifies supplied model geometry as well as saved data. */
public final class SignalNameTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        require(SignalName.cells("").isEmpty(), "Empty name");
        require(SignalName.normalize(" \n 第12出発\t ").equals("第12出発"), "Plain text normalization");
        require(SignalName.normalize("か\u3099").equals("が"), "Combining characters must form one cell");
        require(SignalName.normalize("§a出発").equals("a出発"), "Formatting code excluded");
        require(SignalName.normalize("漢".repeat(100)).length() == 32, "Length cap");
        require(SignalName.normalize("🚦".repeat(40)).length() == 64, "Do not split supplementary characters");
        var cells = SignalName.cells("第12出発");
        require(cells.size() == 4 && cells.get(1).text().equals("12") && cells.get(1).horizontalDigits(), "Two-digit cell");
        require(SignalName.cells("第1出発").stream().noneMatch(SignalName.Cell::horizontalDigits), "One digit stays vertical");
        require(SignalName.cells("第123出発").size() == 6
            && SignalName.cells("第123出発").stream().noneMatch(SignalName.Cell::horizontalDigits), "Long digit run stays vertical");
        require(SignalName.cells("１２").size() == 1 && SignalName.cells("１２").get(0).horizontalDigits(), "Fullwidth digits");
        require(SignalName.cells("12第34").size() == 3, "Independent pairs");
        require(SignalName.cells("🚦出発").size() == 3, "Supplementary character occupies one cell");

        SignalHead head = new SignalHead(UUID.randomUUID(), ColorLightSignalAppearance.createDefault(), null);
        require(!SignalNameBoardLayout.visible(head), "Empty board must be hidden");
        head.setSignalName("第12出発");
        CompoundTag saved = new CompoundTag();
        head.writeNbt(saved);
        SignalHead restored = SignalHead.fromNbt(saved);
        require(restored.getSignalName().equals("第12出発") && restored.getUniqueId().equals(head.getUniqueId()), "Name and identity persist");
        saved.remove("SignalName");
        require(SignalHead.fromNbt(saved).getSignalName().isEmpty(), "Old saves default to hidden");
        require(SignalNameBoardLayout.visible(head), "Round board supported");
        ((ColorLightSignalAppearance) head.getAppearance()).setBackplateType(ColorLightSignalAppearance.BackplateType.SQUARE);
        require(!SignalNameBoardLayout.visible(head), "Limit model compatibility to round signals");

        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            var packet = new SetSignalNamesPacket(net.minecraft.core.BlockPos.ZERO, 3,
                Map.of(head.getUniqueId(), "第12出発", UUID.randomUUID(), ""));
            SetSignalNamesPacket.encode(packet, buffer);
            require(SetSignalNamesPacket.decode(buffer).equals(packet), "Multi-head packet round trip");
        } finally { buffer.release(); }

        for (int lamps = 2; lamps <= 6; lamps++) {
            for (int count : new int[] {1, 4, 8, 32}) {
                PoseStack post = new PoseStack(), board = new PoseStack();
                SignalNameBoardLayout.post(post, lamps, count);
                SignalNameBoardLayout.board(board, lamps, count);
                float[] postBounds = modelBounds("signal_name_board_post", post);
                float[] boardBounds = modelBounds("signal_name_board", board);
                near(postBounds[1] * 16, SignalName.boxTopPixels(lamps), "Post root at lamp housing top");
                near(boardBounds[1] * 16 - SignalName.backplateTopPixels(lamps), 2, "Gap above backplate");
                near(postBounds[4], boardBounds[4], "Posts reach board top");
                near(postBounds[5] * 16, 1.5, "Post front touches backplate rear");
                near(boardBounds[2], postBounds[5], "Board remains attached to post front");
                near((boardBounds[4] - boardBounds[1]) * 16, SignalName.boardHeightPixels(count), "Board grows with cell count");
                require(postBounds[0] >= -2.5 / 16 && postBounds[3] <= 2.5 / 16
                    && postBounds[2] >= -1.0 / 16 && postBounds[5] <= 1.5 / 16,
                    "Post roots must be inside lamp housing footprint");
                for (boolean pair : new boolean[] {false, true}) {
                    var cell = new SignalName.Cell(pair ? "12" : "場", pair);
                    TextModel glyph = new TextModel(null, 0, 0, pair ? 12 : 8, 8, pair ? 12 : 9);
                    PoseStack text = new PoseStack();
                    SignalNameBoardLayout.cell(text, lamps, count, 0, cell, glyph);
                    Vector3f x = text.last().pose().transformDirection(new Vector3f(1, 0, 0));
                    Vector3f y = text.last().pose().transformDirection(new Vector3f(0, 1, 0));
                    near(x.length() / y.length(), pair ? 1 : 1.5, "Glyph aspect ratio");
                    Vector3f center = text.last().pose().transformPosition(new Vector3f(glyph.centerX(), glyph.centerY(), 0));
                    require(center.z > boardBounds[5], "Text must be in front of board");
                    near((boardBounds[4] - center.y) * 16, 3, "Top cell padding");
                    near(x.length() * (glyph.maxX() - glyph.minX()) <= 5.0 / 16 + 1e-6 ? 1 : 0, 1, "Text fits board width");
                }
            }
        }
        // Bitmap quad widths include a transparent trailing column. Singles and
        // pairs must center their visible advance, independent of that padding.
        for (boolean pair : new boolean[] {false, true}) {
            int advance = pair ? 12 : 6;
            TextModel glyph = new TextModel(null, 0, 0, advance, 8, advance);
            PoseStack pose = new PoseStack();
            SignalNameBoardLayout.cell(pose, 3, 1, 0, new SignalName.Cell(pair ? "12" : "1", pair), glyph);
            Vector3f visibleCenter = pose.last().pose().transformPosition(new Vector3f((advance - 1) / 2F, 4, 0));
            near(visibleCenter.x, 0, "Visible digit center excludes transparent padding");
        }
        // The Flywheel visual's block-centered origin must match the BER's mast origin.
        for (int rotation = 0; rotation < 16; rotation++) {
            PoseStack vanilla = new PoseStack(), flywheel = new PoseStack();
            float angle = 180 - rotation * 22.5F;
            vanilla.translate(.25, 0, .75);
            vanilla.mulPose(Axis.YP.rotationDegrees(angle));
            flywheel.translate(.25, .5, .75);
            flywheel.mulPose(Axis.YP.rotationDegrees(angle));
            flywheel.translate(-.5, -.5, -.5);
            flywheel.translate(.5, 0, .5);
            Vec3 offset = new Vec3(10.0 / 16, .5, 6.0 / 16);
            SignalNameBoardLayout.head(vanilla, offset, Pair.of(22.5, 0.0));
            SignalNameBoardLayout.head(flywheel, offset, Pair.of(22.5, 0.0));
            Vector3f a = vanilla.last().pose().transformPosition(new Vector3f(0, 2, .1F));
            Vector3f b = flywheel.last().pose().transformPosition(new Vector3f(0, 2, .1F));
            near(a.distance(b), 0, "Rendering backend placement parity");
        }
        checks += com.skybird.create_jp_signal.client.text.TextLightingTest.verify();
        System.out.println("Signal names: " + checks + " checks passed");
    }

    private static float[] modelBounds(String model, PoseStack pose) throws Exception {
        Path path = Path.of(System.getProperty("jpSignal.resources"),
            "assets/create_jp_signal/models/block/sign/" + model + ".json");
        var elements = JsonParser.parseString(Files.readString(path)).getAsJsonObject().getAsJsonArray("elements");
        float[] bounds = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
            Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
        for (var value : elements) {
            var element = value.getAsJsonObject();
            var from = element.getAsJsonArray("from");
            var to = element.getAsJsonArray("to");
            for (int corner = 0; corner < 8; corner++) {
                Vector3f point = new Vector3f();
                for (int axis = 0; axis < 3; axis++)
                    point.setComponent(axis, ((corner & (1 << axis)) == 0 ? from : to).get(axis).getAsFloat() / 16);
                pose.last().pose().transformPosition(point);
                for (int axis = 0; axis < 3; axis++) {
                    bounds[axis] = Math.min(bounds[axis], point.get(axis));
                    bounds[axis + 3] = Math.max(bounds[axis + 3], point.get(axis));
                }
            }
        }
        return bounds;
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }
    private static void near(double value, double expected, String message) {
        require(Math.abs(value - expected) < 1e-5, message + ": " + value + " != " + expected);
    }
}
