package com.skybird.create_jp_signal.block.signal.signal_mast;

import javax.annotation.Nullable;

import com.skybird.create_jp_signal.AllBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RepeaterSingleTunnelSignalMastBlock extends BaseSignalMastBlock {

    public RepeaterSingleTunnelSignalMastBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RepeaterSingleTunnelSignalMastBlockEntity(pos, state);
    }
    
    // 個別で要実装
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, AllBlockEntities.REPEATER_SINGLE_TUNNEL_SIGNAL_MAST_ENTITY.get(), RepeaterSingleTunnelSignalMastBlockEntity::tick);
    }
}
