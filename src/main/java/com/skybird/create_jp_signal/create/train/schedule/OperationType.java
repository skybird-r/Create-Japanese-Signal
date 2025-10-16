package com.skybird.create_jp_signal.create.train.schedule;

import com.skybird.create_jp_signal.JpSignals;

import net.minecraft.network.chat.Component;

public enum OperationType {
    TRAIN("train"), SHUNT("shunt");

    private String translationKey;

    OperationType(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(JpSignals.MODID + ".schedule.operation" + translationKey);//Lang.translateDirect("schedule.instruction.operation_type." + this.translationKey);
    }
}
