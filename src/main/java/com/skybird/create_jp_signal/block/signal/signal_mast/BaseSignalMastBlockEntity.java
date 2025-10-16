package com.skybird.create_jp_signal.block.signal.signal_mast;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseSignalMastBlockEntity extends BaseSignalBlockEntity {

    public BaseSignalMastBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ISignalType signal) {
        super(type, pos, state, signal);
    }
}