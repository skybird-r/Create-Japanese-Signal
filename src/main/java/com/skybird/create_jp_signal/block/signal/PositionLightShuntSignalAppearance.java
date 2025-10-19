package com.skybird.create_jp_signal.block.signal;

import java.util.List;

import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.RepeaterForm;

import net.minecraft.nbt.CompoundTag;

public class PositionLightShuntSignalAppearance implements ISignalAppearance {

    public enum ShuntType {
        TWO_WHITE("2位式白", List.of(SignalAspect.State.STOP_S2, SignalAspect.State.PROCEED_S2)),
        TWO_RED("2位式赤", List.of(SignalAspect.State.STOP_S2R, SignalAspect.State.PROCEED_S2R)),
        THREE_WHITE("3位式白", List.of(SignalAspect.State.STOP_S3, SignalAspect.State.CAUTION_S3, SignalAspect.State.PROCEED_S3)),
        THREE_RED("3位式赤", List.of(SignalAspect.State.STOP_S3R, SignalAspect.State.CAUTION_S3R, SignalAspect.State.PROCEED_S3R));

        private final String displayName;
        private final List<SignalAspect.State> validStates;

        ShuntType (String name, List<SignalAspect.State> states) {
            this.displayName = name;
            this.validStates = states;
        }

        public List<SignalAspect.State> getValidStates() { return validStates; }
        public String getDisplayName() { return displayName; }
    }

    private ShuntType shuntType;
    private SignalAccessory accessory = new SignalAccessory();

    private static final List<SignalAccessory.Type> validAccesoryTypes = List.of(SignalAccessory.Type.NONE, SignalAccessory.Type.INDICATOR_SHUNT);

    public PositionLightShuntSignalAppearance(ShuntType type) {
        this.shuntType = type;
    }



    public ShuntType getType() { return shuntType; }
    public void setType(ShuntType type) { this.shuntType = type; }
    public SignalAccessory getAccessory() {return accessory;}
    public void setAccessory(SignalAccessory accessory) {this.accessory = accessory;}
    public static List<SignalAccessory.Type> getValidAccesoryTypes() {
        return validAccesoryTypes;
    }

    @Override
    public void writeNbt(CompoundTag tag) {
        tag.putString("ShuntType", this.shuntType.name());

        CompoundTag accessoryTag = new CompoundTag();
        this.accessory.writeNbt(accessoryTag);
        tag.put("Accessory", accessoryTag);
    }

    public static PositionLightShuntSignalAppearance fromNbt(CompoundTag tag) {
        PositionLightShuntSignalAppearance newAppearance;
        try {
            ShuntType type = ShuntType.valueOf(tag.getString("ShuntType"));
            newAppearance = new PositionLightShuntSignalAppearance(type);
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
    public String getTypeId() {
        return "position_light_shunt_signal";
    }

    @Override
    public ISignalAppearance copy() {
        PositionLightShuntSignalAppearance newAppearance = new PositionLightShuntSignalAppearance(this.shuntType);
        newAppearance.setAccessory(this.accessory.copy());
        return newAppearance;
    }

    public static PositionLightShuntSignalAppearance createDefault() {
        return new PositionLightShuntSignalAppearance(ShuntType.TWO_WHITE);
    }

}
