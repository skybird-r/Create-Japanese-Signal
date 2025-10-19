package com.skybird.create_jp_signal.block.signal.signal_type;

import java.util.List;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect.State;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.ISignalHeadRenderer;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.PositionLightShuntSignalRenderer;

import net.minecraft.resources.ResourceLocation;

public class PositionLightShuntSignalType implements ISignalType {

    public static final ResourceLocation ID = new ResourceLocation(JpSignals.MODID, "position_light_shunt_signal");

    @Override public ResourceLocation getId() { return ID; }
    @Override public String getDisplayName() { return "灯列式入換信号機"; }
    @Override public int getMaxAttachmentsPerMast() { return 1; }

    @Override public boolean isCompatibleWith(ISignalType otherType) {
        return false;
    }

    @Override
    public ISignalHeadRenderer getRenderer() {
        return new PositionLightShuntSignalRenderer();
    }

    @Override
    public ISignalAppearance createDefaultAppearance() {
        return PositionLightShuntSignalAppearance.createDefault();
    }

    @Override
    public AspectMapping createDefaultMapping(ISignalAppearance appearance) {
        if (!(appearance instanceof PositionLightShuntSignalAppearance plsAppearance)) {
            return new AspectMapping();
        }

        AspectMapping defaultMapping = new AspectMapping();
        switch (plsAppearance.getType()) {
            case TWO_WHITE:
                defaultMapping.addRule(0, State.STOP_S2);
                defaultMapping.addRule(1, State.PROCEED_S2);
                break;
            case TWO_RED:
                defaultMapping.addRule(0, State.STOP_S2R);
                defaultMapping.addRule(1, State.PROCEED_S2R);
                break;
            case THREE_WHITE:
                defaultMapping.addRule(0, State.STOP_S3);
                defaultMapping.addRule(1, State.CAUTION_S3);
                defaultMapping.addRule(2, State.PROCEED_S3);
                break;
            case THREE_RED:
                defaultMapping.addRule(0, State.STOP_S3R);
                defaultMapping.addRule(1, State.CAUTION_S3R);
                defaultMapping.addRule(2, State.PROCEED_S3R);
                break;
        }
        return defaultMapping;
    }

    @Override
    public List<State> getValidStates(ISignalAppearance appearance) {
        if (appearance instanceof PositionLightShuntSignalAppearance plsAppearance) {
            return plsAppearance.getType().getValidStates();
        }
        return List.of();
    }
    
}
