package com.skybird.create_jp_signal.block.sign;

import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.client.blockentityrenderer.sign.RailwaySignLayout;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Executable checks of sign persistence, model dimensions and actual rendering transforms. */
public final class RailwaySignTest {
    private static int checks;
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); checks++; }
    private static void near(double actual, double expected, String message) { check(Math.abs(actual - expected) < .00001, message); }

    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        RailwaySignSettings settings = new RailwaySignSettings();
        settings.load(new CompoundTag());
        check(settings.speed() == 100 && settings.useLetterO(), "Missing fields must retain 100 and O defaults");
        check(settings.xPos() == 8 && settings.zPos() == 8 && settings.heightStep() == 0, "Default placement");
        for (RailwaySignType type : RailwaySignType.values()) {
            if (type.numberedMast()) continue;
            var path = Path.of(System.getProperty("jpSignal.resources"), "assets/create_jp_signal/models/block/sign/" + type.id() + ".json");
            var element = JsonParser.parseString(Files.readString(path)).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();
            near(element.getAsJsonArray("to").get(1).getAsDouble(), type.height(), "Actual supplied sign height");
            settings = new RailwaySignSettings();
            for (int step = 0; step < 4; step++) {
                near(settings.bottomHeight(), step * .5, "Wrench height step");
                PoseStack pole = new PoseStack(); RailwaySignLayout.pole(pole, settings, type);
                Vector3f bottom = pole.last().pose().transformPosition(new Vector3f(.5F, 0, .5F));
                Vector3f top = pole.last().pose().transformPosition(new Vector3f(.5F, 1, .5F));
                near(bottom.y, -14.0 / 16, "Pole remains buried by 14px");
                near(top.y, settings.bottomHeight() + type.height() / 16.0, "Pole top matches sign top");
                PoseStack board = new PoseStack(); RailwaySignLayout.board(board, settings);
                Vector3f back = board.last().pose().transformPosition(new Vector3f(.5F, 0, 8.5F / 16));
                Vector3f poleFront = pole.last().pose().transformPosition(new Vector3f(.5F, 0, 8.5F / 16));
                near(back.z, poleFront.z, "Board back touches pole front");
                PoseStack text = new PoseStack(); RailwaySignLayout.numberFace(text, settings);
                Vector3f face = text.last().pose().transformPosition(new Vector3f());
                check(face.z > 1.0 / 16, "Text must stand in front of board");
                near(face.y, settings.bottomHeight() + 4.5 / 16, "Text vertical centering");
                settings.cycleHeight();
            }
            check(settings.heightStep() == 0, "Wrench wraps after 1.5 blocks");
        }
        for (int rotation = 0; rotation < 16; rotation++) {
            settings.placement(rotation, 3, 13);
            settings.cycleHeight();
            settings.configure(500, RailwaySignSettings.Variant.LEFT, false);
            CompoundTag tag = new CompoundTag(); settings.save(tag);
            RailwaySignSettings restored = new RailwaySignSettings(); restored.load(tag);
            CompoundTag again = new CompoundTag(); restored.save(again);
            check(tag.equals(again), "NBT round trip must retain all settings");
        }
        CompoundTag broken = new CompoundTag(); broken.putInt("Speed", -100); broken.putString("Variant", "UNKNOWN");
        settings.load(broken);
        check(settings.speed() == 5 && settings.variant() == RailwaySignSettings.Variant.NORMAL, "Bad input is clamped safely");
        for (int speed = 5; speed <= 500; speed++) {
            for (boolean letterO : new boolean[] {false, true}) {
                var digits = RailwaySignLayout.digits(speed, letterO, text -> text.equals("O") ? 7 : 6);
                String actual = digits.stream().map(RailwaySignLayout.Digit::text).reduce("", String::concat);
                String expected = Integer.toString(speed); if (letterO) expected = expected.replace('0', 'O');
                check(actual.equals(expected) && digits.size() <= 3, "Three-digit number / O substitution");
                var first = digits.get(0); var last = digits.get(digits.size() - 1);
                int finalWidth = last.text().equals("O") ? 6 : 5;
                near(first.x() + last.x() + finalWidth, 0, "Text centered by visible character width");
                check(digits.size() == 3 ? first.scaleX() <= .75F : first.scaleX() == 1,
                    "Only three-digit numbers are horizontally compressed");
                PoseStack firstPose = new PoseStack(); RailwaySignLayout.digit(firstPose, first);
                PoseStack lastPose = new PoseStack(); RailwaySignLayout.digit(lastPose, last);
                Vector3f left = firstPose.last().pose().transformPosition(new Vector3f());
                Vector3f right = lastPose.last().pose().transformPosition(new Vector3f(finalWidth, 8, 0));
                near(left.x + right.x, 0, "Scaled text remains centered");
                check(right.x - left.x <= 12F / 16 + .00001F, "Enlarged digits fit 12px board");
                near(left.y - right.y, 6F / 16, "Digits are 6px high in both render paths");
            }
        }
        for (RailwaySignType type : RailwaySignType.values()) {
            for (int x : new int[] {3, 8, 13}) for (int z : new int[] {3, 8, 13}) {
                for (int rotation = 0; rotation < 16; rotation++) {
                    settings = new RailwaySignSettings(); settings.placement(rotation, x, z);
                    for (int height = 0; height < 4; height++) {
                        var shape = RailwaySignBlock.selectionShape(settings, type);
                        check(!shape.isEmpty(), "Post remains selectable at every height");
                        for (Direction.Axis axis : Direction.Axis.values()) {
                            check(shape.min(axis) >= 0 && shape.max(axis) <= 1,
                                "Selection outline stays inside the ray-selectable block cell");
                        }
                        check(shape.clip(new Vec3(x / 16.0, .125, -1),
                            new Vec3(x / 16.0, .125, 2), BlockPos.ZERO) != null,
                            "The enlarged pole click target still receives ray hits");
                        settings.cycleHeight();
                    }
                }
            }
        }
        settings = new RailwaySignSettings();
        check(settings.number() == 0 && settings.useLetterO(), "Numbered mast defaults to O");
        for (int height = 0; height < 4; height++) {
            var type = RailwaySignType.BLOCK_SIGNAL_NO_PLATE;
            near(type.boardBottom(settings), height * .25, "Number plate moves by quarter blocks");
            PoseStack pole = new PoseStack(); RailwaySignLayout.pole(pole, settings, type);
            near(pole.last().pose().transformPosition(new Vector3f(.5F, 0, .5F)).y, 0, "Mast stays on block floor");
            near(pole.last().pose().transformPosition(new Vector3f(.5F, 1, .5F)).y, 1, "Mast stays 16px high");
            PoseStack board = new PoseStack(); RailwaySignLayout.board(board, settings, type);
            var back = board.last().pose().transformPosition(new Vector3f(.5F, 0, 10F / 16));
            near(back.y, height * .25, "Plate lower edge matches selected height");
            near(back.z, 2F / 16, "Plate back touches mast front");
            PoseStack text = new PoseStack(); RailwaySignLayout.numberFace(text, settings, type);
            var center = text.last().pose().transformPosition(new Vector3f());
            near(center.y, height * .25 + 2.5 / 16, "Plate text centered vertically");
            check(center.z > 2.25 / 16, "Plate text stands outside its front face");
            settings.cycleHeight();
        }
        for (int number = 0; number <= 99; number++) for (boolean letterO : new boolean[] {false, true}) {
            settings.configureNumber(number, letterO);
            CompoundTag tag = new CompoundTag(); settings.save(tag);
            var restored = new RailwaySignSettings(); restored.load(tag);
            check(restored.number() == number && restored.useLetterO() == letterO, "Plate number and font choice persist");
            var digits = RailwaySignLayout.numberPlateDigits(number, letterO, c -> c.equals("O") ? 7 : 6);
            String value = digits.stream().map(RailwaySignLayout.Digit::text).reduce("", String::concat);
            check(value.equals(letterO ? Integer.toString(number).replace('0', 'O') : Integer.toString(number)), "Plate number/O text");
            var first = digits.get(0); var last = digits.get(digits.size() - 1);
            PoseStack firstPose = new PoseStack(); RailwaySignLayout.digit(firstPose, first);
            PoseStack lastPose = new PoseStack(); RailwaySignLayout.digit(lastPose, last);
            var left = firstPose.last().pose().transformPosition(new Vector3f());
            var right = lastPose.last().pose().transformPosition(new Vector3f(last.text().equals("O") ? 6 : 5, 8, 0));
            near(left.x + right.x, 0, "Number plate text remains centered");
            check(right.x - left.x <= 4.5 / 16 + .00001, "Two digits leave margins inside the 5px plate");
            near(left.y - right.y, 4F / 16, "Plate text is 4px high");
        }
        System.out.println("Verified " + checks + " railway sign checks: persistence, height cycle, buried pole, board attachment, and speed labels.");
    }
}
