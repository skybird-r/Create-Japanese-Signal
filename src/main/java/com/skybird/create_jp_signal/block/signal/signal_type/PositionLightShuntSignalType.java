package com.skybird.create_jp_signal.block.signal.signal_type;

import java.util.List;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect.State;
import com.skybird.create_jp_signal.client.blockentityrenderer.ISignalHeadRenderer;
import com.skybird.create_jp_signal.client.blockentityrenderer.PositionLightRepeaterSignalRenderer;

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
        throw new UnsupportedOperationException("Unimplemented method 'getRenderer'");
        // return new PositionLightShuntSignalRenderer();
    }

    @Override
    public ISignalAppearance createDefaultAppearance() {
        return PositionLightShuntSignalAppearance.createDefault();
    }

    @Override
    public AspectMapping createDefaultMapping(ISignalAppearance appearance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDefaultMapping'");
    }

    @Override
    public List<State> getValidStates(ISignalAppearance appearance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValidStates'");
    }
    
}
