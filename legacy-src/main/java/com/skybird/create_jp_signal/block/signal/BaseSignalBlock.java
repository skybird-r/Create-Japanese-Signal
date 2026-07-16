package com.skybird.create_jp_signal.block.signal;

import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseSignalBlock extends BaseEntityBlock implements IWrenchable {

    public BaseSignalBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof BaseSignalBlockEntity be) {
            be.cycleLayout();
        }
        return InteractionResult.SUCCESS;
    }

    // getTickerは個別で実装
}
