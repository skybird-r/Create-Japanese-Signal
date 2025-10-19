package com.skybird.create_jp_signal.block.signal.signal_type;

import java.util.List;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.ColorLightSignalRenderer;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.ISignalHeadRenderer;

import net.minecraft.resources.ResourceLocation;

public class ColorLightSignalType implements ISignalType {
    public static final ResourceLocation ID = new ResourceLocation(JpSignals.MODID, "color_light_signal");

    @Override public ResourceLocation getId() { return ID; }
    @Override public String getDisplayName() { return "色灯式信号機"; }
    @Override public int getMaxAttachmentsPerMast() { return 2; }

    @Override
    public boolean isCompatibleWith(ISignalType otherType) {
        return otherType instanceof ColorLightSignalType;
    }

    @Override
    public ISignalHeadRenderer getRenderer() {
        return new ColorLightSignalRenderer();
    }

    @Override
    public AspectMapping createDefaultMapping(ISignalAppearance appearance) {
        if (!(appearance instanceof ColorLightSignalAppearance clAppearance)) {
            return new AspectMapping();
        }

        AspectMapping defaultMapping = new AspectMapping();
        switch (clAppearance.getHeadType()) {
            case TWO_LAMP_YR:
                defaultMapping.addRule(0, SignalAspect.State.R_2YR);
                defaultMapping.addRule(1, SignalAspect.State.Y_2YR);
                break;
            case TWO_LAMP_GR:
                defaultMapping.addRule(0, SignalAspect.State.R_2GR);
                defaultMapping.addRule(1, SignalAspect.State.G_2GR);
                break;
            case TWO_LAMP_GY:
                defaultMapping.addRule(0, SignalAspect.State.Y_2GY);
                defaultMapping.addRule(1, SignalAspect.State.G_2GY);
                break;
            case THREE_LAMP:
                defaultMapping.addRule(0, SignalAspect.State.R_3);
                defaultMapping.addRule(1, SignalAspect.State.Y_3);
                defaultMapping.addRule(2, SignalAspect.State.G_3);
                break;
            case FOUR_LAMP_A:
                defaultMapping.addRule(0, SignalAspect.State.R_4A);
                defaultMapping.addRule(1, SignalAspect.State.YY_4A);
                defaultMapping.addRule(2, SignalAspect.State.Y_4A);
                defaultMapping.addRule(3, SignalAspect.State.G_4A);
                break;
            case FOUR_LAMP_B:
                defaultMapping.addRule(0, SignalAspect.State.R_4B);
                defaultMapping.addRule(1, SignalAspect.State.Y_4B);
                defaultMapping.addRule(2, SignalAspect.State.YG_4B);
                defaultMapping.addRule(3, SignalAspect.State.G_4B);
                break;
            case FIVE_LAMP_A:
                defaultMapping.addRule(0, SignalAspect.State.R_5A);
                defaultMapping.addRule(1, SignalAspect.State.YY_5A);
                defaultMapping.addRule(2, SignalAspect.State.Y_5A);
                defaultMapping.addRule(3, SignalAspect.State.YG_5A);
                defaultMapping.addRule(4, SignalAspect.State.G_5A);
                break;
            case FIVE_LAMP_B:
                defaultMapping.addRule(0, SignalAspect.State.R_5B);
                defaultMapping.addRule(1, SignalAspect.State.Y_5B);
                defaultMapping.addRule(2, SignalAspect.State.G_5B);
                defaultMapping.addRule(3, SignalAspect.State.GG_5B);
                break;
            case SIX_LAMP:
                defaultMapping.addRule(0, SignalAspect.State.R_6);
                defaultMapping.addRule(1, SignalAspect.State.Y_6);
                defaultMapping.addRule(2, SignalAspect.State.YG_6);
                defaultMapping.addRule(3, SignalAspect.State.G_6);
                defaultMapping.addRule(4, SignalAspect.State.GG_6);
                break;
        }
        return defaultMapping;
    }

    @Override
    public List<SignalAspect.State> getValidStates(ISignalAppearance appearance) {
        if (appearance instanceof ColorLightSignalAppearance clAppearance) {
            return clAppearance.getHeadType().getValidStates();
        }
        return List.of();
    }

    @Override
    public ISignalAppearance createDefaultAppearance() {
        return ColorLightSignalAppearance.createDefault();
    }
}