package com.skybird.create_jp_signal.block.track;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.create.train.track.PenaltyBoundary;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class PenaltyBlock extends Block implements IBE<PenaltyBlockEntity>, IWrenchable {
    public PenaltyBlock(Properties properties) {
        super(properties.noOcclusion()
            .destroyTime(1.5F)
            .explosionResistance(6.0F));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PenaltyBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public Class<PenaltyBlockEntity> getBlockEntityClass() {
        return PenaltyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PenaltyBlockEntity> getBlockEntityType() {
        return AllBlockEntities.PENALTY_ENTITY.get();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(AllBlocks.PENALTY_ITEM.get()));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof PenaltyBlockEntity blockEntity) {
            TrackTargetingBehaviour<PenaltyBoundary> target = blockEntity.edgePoint;
            if (target.getEdgePoint() == null) {
                player.displayClientMessage(Component.translatable("create_jp_signal.track_target.missing"), true);
                return InteractionResult.FAIL;
            }
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
