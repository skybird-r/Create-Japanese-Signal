package com.skybird.create_jp_signal.block.signal;

import java.util.List;

import org.jline.terminal.Terminal.Signal;

import net.minecraft.nbt.CompoundTag;

public class PositionLightRepeaterSignalAppearance implements ISignalAppearance {

    // 灯列式専用の形状タイプ (円盤の枚数)
    public enum RepeaterForm {
        SINGLE_DISC("通常", List.of(SignalAspect.State.STOP_R, SignalAspect.State.RESTRICTED_R, SignalAspect.State.PROCEED_R)),
        DOUBLE_DISC("高速対応", List.of(SignalAspect.State.STOP_R2, SignalAspect.State.RESTRICTED_R2, SignalAspect.State.PROCEED_R2, SignalAspect.State.HIGH_SPEED_R2));
    
        private final String displayName;
        private final List<SignalAspect.State> validStates;

        RepeaterForm(String name, List<SignalAspect.State> states) {
            this.displayName = name;
            this.validStates = states;
        }

        public List<SignalAspect.State> getValidStates() { return validStates; }
        public String getDisplayName() { return displayName; }
    }
    
    public enum SignalSize {
        NORMAL, TUNNEL;
    }

    private RepeaterForm form;
    private final SignalSize signalSize;
    private SignalAccessory accessory = new SignalAccessory();

    private static final List<SignalAccessory.Type> validAccesoryTypes = List.of(SignalAccessory.Type.NONE, SignalAccessory.Type.FORECAST);


    public PositionLightRepeaterSignalAppearance(RepeaterForm form, SignalSize size) {
        this.form = form;
        this.signalSize = size;
    }
    
    public static PositionLightRepeaterSignalAppearance createDefault() {
        return new PositionLightRepeaterSignalAppearance(RepeaterForm.SINGLE_DISC, SignalSize.NORMAL);
    }

    @Override
    public ISignalAppearance copy() {
        PositionLightRepeaterSignalAppearance newAppearance = new PositionLightRepeaterSignalAppearance(this.form, this.signalSize);
        newAppearance.setAccessory(this.accessory.copy());
        return newAppearance;
    }

    public RepeaterForm getForm() { return form; }
    public void setForm(RepeaterForm form) { this.form = form; }
    public SignalAccessory getAccessory() {return accessory;}
    public void setAccessory(SignalAccessory accessory) {this.accessory = accessory;}
    public SignalSize getSignalSize() { return this.signalSize; }
    public static List<SignalAccessory.Type> getValidAccesoryTypes() {
        return validAccesoryTypes;
    }
    
    @Override
    public void writeNbt(CompoundTag tag) {
        tag.putString("Form", this.form.name());
        tag.putString("SignalSize", this.signalSize.name());

        CompoundTag accessoryTag = new CompoundTag();
        this.accessory.writeNbt(accessoryTag);
        tag.put("Accessory", accessoryTag);
    }

    public static PositionLightRepeaterSignalAppearance fromNbt(CompoundTag tag) {
        PositionLightRepeaterSignalAppearance newAppearance;
        try {
            RepeaterForm form = RepeaterForm.valueOf(tag.getString("Form"));
            SignalSize size = SignalSize.valueOf(tag.getString("SignalSize"));
            newAppearance = new PositionLightRepeaterSignalAppearance(form, size);
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