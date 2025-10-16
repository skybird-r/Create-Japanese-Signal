package com.skybird.create_jp_signal.block.signal.source;

import java.util.Set;

import net.minecraft.core.BlockPos;

public interface ISignalRepeater {
    public boolean isAnyRed(Set<BlockPos> visited);
}
