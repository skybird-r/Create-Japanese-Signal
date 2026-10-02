package com.skybird.create_jp_signal.block.track;

import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.skybird.create_jp_signal.create.train.track.AllEdgePointTypes;

import net.minecraft.world.level.block.Block;

public class ReservationLimitBlockItem extends TrackTargetingBlockItem {
    public ReservationLimitBlockItem(Block block, Properties properties) {
        super(block, properties, AllEdgePointTypes.RESERVATION_LIMIT);
    }
}
