package com.skybird.create_jp_signal.block.signal;

import java.util.EnumMap;
import java.util.Map;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity.AttachmentSlot;

import net.minecraft.nbt.CompoundTag;

public class SignalLayout {

    public int globalHorizontalStep = 0;
    public Map<AttachmentSlot, Integer> verticalSteps = new EnumMap<>(AttachmentSlot.class);
    public Map<AttachmentSlot, Integer> pitchSteps = new EnumMap<>(AttachmentSlot.class);

    public SignalLayout() {
        for (AttachmentSlot slot : AttachmentSlot.values()) {
            verticalSteps.put(slot, 0);
            pitchSteps.put(slot, 0); // ★ 新しいステップも初期化
        }
    }
    
    // --- NBT (セーブデータ) の保存と読み込み ---
    public void writeNbt(CompoundTag tag) {
        tag.putInt("GlobalHorizontalStep", this.globalHorizontalStep);
        
        CompoundTag verticalTags = new CompoundTag();
        verticalSteps.forEach((slot, step) -> verticalTags.putInt(slot.name(), step));
        tag.put("VerticalSteps", verticalTags);

        CompoundTag pitchTags = new CompoundTag();
        pitchSteps.forEach((slot, step) -> pitchTags.putInt(slot.name(), step));
        tag.put("PitchSteps", pitchTags);
    }

    public static SignalLayout fromNbt(CompoundTag tag) {
        SignalLayout layout = new SignalLayout();
        layout.globalHorizontalStep = tag.getInt("GlobalHorizontalStep");

        if (tag.contains("VerticalSteps", CompoundTag.TAG_COMPOUND)) {
            CompoundTag verticalTags = tag.getCompound("VerticalSteps");
            for (String key : verticalTags.getAllKeys()) {
                try {
                    AttachmentSlot slot = AttachmentSlot.valueOf(key);
                    layout.verticalSteps.put(slot, verticalTags.getInt(key));
                } catch (IllegalArgumentException e) { /* Ignore */ }
            }
        }

        if (tag.contains("PitchSteps", CompoundTag.TAG_COMPOUND)) {
            CompoundTag pitchTags = tag.getCompound("PitchSteps");
            for (String key : pitchTags.getAllKeys()) {
                try {
                    AttachmentSlot slot = AttachmentSlot.valueOf(key);
                    layout.pitchSteps.put(slot, pitchTags.getInt(key));
                } catch (IllegalArgumentException e) { /* ignore */ }
            }
        }
        return layout;
    }
}
