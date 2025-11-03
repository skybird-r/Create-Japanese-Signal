package com.skybird.create_jp_signal.block.signal.signal_type; // 新しいパッケージ

import java.util.List;

import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.ISignalHeadRenderer;

import net.minecraft.resources.ResourceLocation;

public interface ISignalType {
    ResourceLocation getId();
    int getMaxAttachmentsPerMast();
    boolean isCompatibleWith(ISignalType otherType);
    ISignalHeadRenderer getRenderer();
    ISignalAppearance createDefaultAppearance();
    AspectMapping createDefaultMapping(ISignalAppearance appearance);
    List<SignalAspect.State> getValidStates(ISignalAppearance appearance);
}