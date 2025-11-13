package com.skybird.create_jp_signal.block.track;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.create.train.track.SpeedLimitBoundary;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class SpeedLimitBlock extends Block implements IBE<SpeedLimitBlockEntity>, IWrenchable  {
    public SpeedLimitBlock(Properties properties) {
        super(
            properties
            .noOcclusion()
            .destroyTime(1.5F)
            .explosionResistance(6.0F)
        );
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpeedLimitBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		IBE.onRemove(state, worldIn, pos, newState);
	}

    @Override
    public Class<SpeedLimitBlockEntity> getBlockEntityClass() {
        return SpeedLimitBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpeedLimitBlockEntity> getBlockEntityType() {
        return AllBlockEntities.SPEED_LIMIT_ENTITY.get();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        
        if (level.getBlockEntity(pos) instanceof SpeedLimitBlockEntity slbe) {

            TrackTargetingBehaviour<SpeedLimitBoundary> target = slbe.edgePoint;

            if (target.getEdgePoint() == null) {
                player.displayClientMessage(Component.literal("対象の線路がありません"), true);
                return InteractionResult.FAIL;
            }
            NetworkHooks.openScreen((ServerPlayer) player, slbe, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
