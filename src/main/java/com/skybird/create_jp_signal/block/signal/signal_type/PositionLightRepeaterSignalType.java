package com.skybird.create_jp_signal.block.signal.signal_type;

import java.util.List;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.client.blockentityrenderer.ISignalHeadRenderer;
import com.skybird.create_jp_signal.client.blockentityrenderer.PositionLightRepeaterSignalRenderer;
import net.minecraft.resources.ResourceLocation;

public class PositionLightRepeaterSignalType implements ISignalType {
    public static final ResourceLocation ID = new ResourceLocation(JpSignals.MODID, "position_light_repeater_signal");

    @Override public ResourceLocation getId() { return ID; }
    @Override public String getDisplayName() { return "灯列式中継信号機"; }
    @Override public int getMaxAttachmentsPerMast() { return 1; }

    @Override
    public boolean isCompatibleWith(ISignalType otherType) {
        return false; 
    }

    @Override
    public ISignalHeadRenderer getRenderer() {
        return new PositionLightRepeaterSignalRenderer(); 
    }

    @Override
    public ISignalAppearance createDefaultAppearance() {
        return PositionLightRepeaterSignalAppearance.createDefault();
    }

    @Override
    public AspectMapping createDefaultMapping(ISignalAppearance appearance) {
        if (!(appearance instanceof PositionLightRepeaterSignalAppearance plrAppearance)) {
            return new AspectMapping();
        }

        AspectMapping defaultMapping = new AspectMapping();
        // 灯列式の種類(円盤の枚数)に応じてデフォルト設定を分岐
        switch (plrAppearance.getForm()) {
            case SINGLE_DISC:
                defaultMapping.addRule(0, SignalAspect.State.STOP_R);
                defaultMapping.addRule(1, SignalAspect.State.RESTRICTED_R);
                defaultMapping.addRule(2, SignalAspect.State.PROCEED_R);
                break;
            case DOUBLE_DISC:
                defaultMapping.addRule(0, SignalAspect.State.STOP_2R);
                defaultMapping.addRule(1, SignalAspect.State.RESTRICTED_2R);
                defaultMapping.addRule(2, SignalAspect.State.PROCEED_2R);
                defaultMapping.addRule(3, SignalAspect.State.HIGH_SPEED_2R);
                break;
        }
        return defaultMapping;
    }

    @Override
    public List<SignalAspect.State> getValidStates(ISignalAppearance appearance) {
        if (appearance instanceof PositionLightRepeaterSignalAppearance plrAppearance) {
            return plrAppearance.getForm().getValidStates();
        }
        return List.of();
    }
}