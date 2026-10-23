package com.skybird.create_jp_signal.block.sign;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

/** World data independent of client rendering; old/missing fields keep safe defaults. */
public final class RailwaySignSettings {
    public enum Variant { NORMAL, RIGHT, LEFT }
    private int rotation;
    private int xPos = 8;
    private int zPos = 8;
    private int heightStep;
    private int speed = 100;
    private int number;
    private Variant variant = Variant.NORMAL;
    private boolean useLetterO = true;

    public int rotation() { return rotation; }
    public int xPos() { return xPos; }
    public int zPos() { return zPos; }
    public int heightStep() { return heightStep; }
    public double bottomHeight() { return heightStep * .5; }
    public int speed() { return speed; }
    public int number() { return number; }
    public Variant variant() { return variant; }
    public boolean useLetterO() { return useLetterO; }
    public void placement(int rotation, int x, int z) {
        this.rotation = rotation & 15;
        xPos = Mth.clamp(x, 0, 16); zPos = Mth.clamp(z, 0, 16);
    }
    public void cycleHeight() { heightStep = (heightStep + 1) % 4; }
    public void configure(int speed, Variant variant, boolean useLetterO) {
        this.speed = Mth.clamp(speed, 5, 500);
        this.variant = variant;
        this.useLetterO = useLetterO;
    }
    public void configureNumber(int number, boolean useLetterO) {
        this.number = Mth.clamp(number, 0, 99); this.useLetterO = useLetterO;
    }
    public void save(CompoundTag tag) {
        tag.putInt("Rotation", rotation); tag.putInt("XPos", xPos); tag.putInt("ZPos", zPos);
        tag.putInt("HeightStep", heightStep); tag.putInt("Speed", speed); tag.putInt("Number", number);
        tag.putString("Variant", variant.name()); tag.putBoolean("UseLetterO", useLetterO);
    }
    public void load(CompoundTag tag) {
        placement(tag.getInt("Rotation"), tag.contains("XPos") ? tag.getInt("XPos") : 8,
            tag.contains("ZPos") ? tag.getInt("ZPos") : 8);
        heightStep = Mth.clamp(tag.getInt("HeightStep"), 0, 3);
        speed = tag.contains("Speed") ? Mth.clamp(tag.getInt("Speed"), 5, 500) : 100;
        number = Mth.clamp(tag.getInt("Number"), 0, 99);
        try { variant = Variant.valueOf(tag.getString("Variant")); }
        catch (IllegalArgumentException exception) { variant = Variant.NORMAL; }
        useLetterO = !tag.contains("UseLetterO") || tag.getBoolean("UseLetterO");
    }
}
