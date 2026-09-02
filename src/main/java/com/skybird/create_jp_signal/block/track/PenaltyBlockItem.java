package com.skybird.create_jp_signal.block.track;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.skybird.create_jp_signal.create.train.track.AllEdgePointTypes;

import net.minecraft.world.level.block.Block;

public class PenaltyBlockItem extends TrackTargetingBlockItem {
    public PenaltyBlockItem(Block block, Properties properties) {
        super(block, properties, AllEdgePointTypes.PENALTY);
    }
}
