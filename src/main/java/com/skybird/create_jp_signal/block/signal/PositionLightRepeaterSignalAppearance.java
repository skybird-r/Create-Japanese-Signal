package com.skybird.create_jp_signal.block.signal;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public class PositionLightRepeaterSignalAppearance implements ISignalAppearance {

    // 灯列式専用の形状タイプ (円盤の枚数)
    public enum RepeaterForm {
        SINGLE_DISC("通常", List.of(SignalAspect.State.STOP_R, SignalAspect.State.RESTRICTED_R, SignalAspect.State.PROCEED_R)),
        DOUBLE_DISC("高速対応", List.of(SignalAspect.State.STOP_2R, SignalAspect.State.RESTRICTED_2R, SignalAspect.State.PROCEED_2R, SignalAspect.State.HIGH_SPEED_2R));
    
        private final String displayName;
        private final List<SignalAspect.State> validStates;

        RepeaterForm(String name, List<SignalAspect.State> states) {
            this.displayName = name;
            this.validStates = states;
        }

        public List<SignalAspect.State> getValidStates() { return validStates; }
        public String getDisplayName() { return displayName; }
    }
    
    private RepeaterForm form;
    private SignalAccessory accessory = new SignalAccessory();

    private static final List<SignalAccessory.Type> validAccesoryTypes = List.of(SignalAccessory.Type.NONE, SignalAccessory.Type.FORECAST);


    public PositionLightRepeaterSignalAppearance(RepeaterForm form) {
        this.form = form;
    }
    
    public static PositionLightRepeaterSignalAppearance createDefault() {
        return new PositionLightRepeaterSignalAppearance(RepeaterForm.SINGLE_DISC);
    }

    @Override
    public ISignalAppearance copy() {
        PositionLightRepeaterSignalAppearance newAppearance = new PositionLightRepeaterSignalAppearance(this.form);
        newAppearance.setAccessory(this.accessory.copy());
        return newAppearance;
    }

    public RepeaterForm getForm() { return form; }
    public void setForm(RepeaterForm form) { this.form = form; }
    public SignalAccessory getAccessory() {return accessory;}
    public void setAccessory(SignalAccessory accessory) {this.accessory = accessory;}
    public static List<SignalAccessory.Type> getValidAccesoryTypes() {
        return validAccesoryTypes;
    }
    
    @Override
    public void writeNbt(CompoundTag tag) {
        tag.putString("Form", this.form.name());

        CompoundTag accessoryTag = new CompoundTag();
        this.accessory.writeNbt(accessoryTag);
        tag.put("Accessory", accessoryTag);
    }

    public static PositionLightRepeaterSignalAppearance fromNbt(CompoundTag tag) {
        PositionLightRepeaterSignalAppearance newAppearance;
        try {
            RepeaterForm form = RepeaterForm.valueOf(tag.getString("Form"));
            newAppearance = new PositionLightRepeaterSignalAppearance(form);
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
    public String getTypeId() { return "position_light_repeater_signal"; }

}