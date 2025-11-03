package com.skybird.create_jp_signal.block.signal;

import java.util.List;

import com.skybird.create_jp_signal.JpSignals;

import net.minecraft.nbt.CompoundTag;

// このファイルを SignalAppearance.java として保存
public class ColorLightSignalAppearance implements ISignalAppearance {

    // 信号機の形状タイプ
    public enum HeadType {
        TWO_LAMP_YR("2YR", 2, List.of(SignalAspect.State.Y_2YR, SignalAspect.State.R_2YR)),
        TWO_LAMP_GR("2GR", 2, List.of(SignalAspect.State.G_2GR, SignalAspect.State.R_2GR)),
        TWO_LAMP_GY("2GY", 2, List.of(SignalAspect.State.G_2GY, SignalAspect.State.Y_2GY)),
        THREE_LAMP("3", 3, List.of(SignalAspect.State.G_3, SignalAspect.State.Y_3, SignalAspect.State.R_3)),
        FOUR_LAMP_A("4A", 4, List.of(SignalAspect.State.G_4A, SignalAspect.State.Y_4A, SignalAspect.State.YY_4A, SignalAspect.State.R_4A)),
        FOUR_LAMP_B("4B", 4, List.of(SignalAspect.State.G_4B, SignalAspect.State.YGF_4B, SignalAspect.State.YG_4B, SignalAspect.State.Y_4B, SignalAspect.State.R_4B)),
        FIVE_LAMP_A("5A", 5, List.of(SignalAspect.State.G_5A, SignalAspect.State.YGF_5A, SignalAspect.State.YG_5A, SignalAspect.State.Y_5A, SignalAspect.State.YY_5A, SignalAspect.State.R_5A)),
        FIVE_LAMP_B("5B", 5, List.of(SignalAspect.State.GG_5B, SignalAspect.State.G_5B, SignalAspect.State.Y_5B, SignalAspect.State.R_5B)),
        SIX_LAMP("6", 6, List.of(SignalAspect.State.GG_6, SignalAspect.State.G_6, SignalAspect.State.YGF_6, SignalAspect.State.YG_6, SignalAspect.State.Y_6, SignalAspect.State.R_6));
        

        private final String translationKey;
        private final int lampCount;
        private final List<SignalAspect.State> validStates;

        HeadType(String key, int count, List<SignalAspect.State> states) {
            this.translationKey = key;
            this.lampCount = count;
            this.validStates = states;
        }

        public int getLampCount() { return lampCount; }
        public List<SignalAspect.State> getValidStates() { return validStates; }
        public String getTranslationKey() { return "signal.color.type." + translationKey; }
    }

    public enum BackplateType {
        ROUND("round"),
        SQUARE("square"),
        NONE("none");
    
        private final String translationKey;
    
        BackplateType(String key) {
            this.translationKey = key;
        }
    
        public String getTranslationKey() {
            return "signal.color.backplate_type." + translationKey;
        }
    }

    private HeadType headType;
    private BackplateType backplateType;
    private boolean isRepeater;
    private SignalAccessory accessory = new SignalAccessory();

    private static final List<SignalAccessory.Type> validAccesoryTypes = List.of(SignalAccessory.Type.NONE, SignalAccessory.Type.FORECAST, SignalAccessory.Type.INDICATOR_HOME, SignalAccessory.Type.INDICATOR_DEPARTURE);

    public ColorLightSignalAppearance(HeadType headType, BackplateType backplateType, boolean isRepeater) {
        this.headType = headType;
        this.backplateType = backplateType;
        this.isRepeater = isRepeater;
    }

    public static ColorLightSignalAppearance createDefault() {
        return new ColorLightSignalAppearance(HeadType.THREE_LAMP, BackplateType.ROUND, false);
    }

    @Override
    public ISignalAppearance copy() {
        ColorLightSignalAppearance newAppearance = new ColorLightSignalAppearance(this.headType, this.backplateType, this.isRepeater);
        newAppearance.setAccessory(this.accessory.copy());
        return newAppearance;
    }

    public HeadType getHeadType() { return headType; }
    public void setHeadType(HeadType headType) { this.headType = headType; }
    public BackplateType getBackplateType() { return backplateType; }
    public void setBackplateType(BackplateType backplateType) { this.backplateType = backplateType; }
    public boolean isRepeater() { return isRepeater; }
    public void setRepeater(boolean isRepeater) { this.isRepeater = isRepeater; }
    public SignalAccessory getAccessory() {return accessory;}
    public void setAccessory(SignalAccessory accessory) {this.accessory = accessory;}
    public static List<SignalAccessory.Type> getValidAccesoryTypes() {
        return validAccesoryTypes;
    }

    @Override
    public void writeNbt(CompoundTag tag) {
        tag.putString("HeadType", this.headType.name());
        tag.putString("BackplateType", this.backplateType.name());
        tag.putBoolean("IsRepeater", this.isRepeater);
        CompoundTag accessoryTag = new CompoundTag();
        this.accessory.writeNbt(accessoryTag);
        tag.put("Accessory", accessoryTag);
    }

    public static ColorLightSignalAppearance fromNbt(CompoundTag tag) {
        ColorLightSignalAppearance newAppearance;
        try {
            HeadType headType = HeadType.valueOf(tag.getString("HeadType"));
            BackplateType backplateType = BackplateType.valueOf(tag.getString("BackplateType"));
            boolean isRepeater = tag.getBoolean("IsRepeater");
            newAppearance = new ColorLightSignalAppearance(headType, backplateType, isRepeater);
        } catch (Exception e) {
            newAppearance = createDefault();
        }

        if (tag.contains("Accessory", CompoundTag.TAG_COMPOUND)) {
            CompoundTag accessoryTag = tag.getCompound("Accessory");
            newAppearance.setAccessory(SignalAccessory.fromNbt(accessoryTag));
        }
        
        return newAppearance;
    }
    
    @Override
    public String getTypeId() { return "color_light_signal"; }
}