package com.skybird.create_jp_signal.block.signal.debug;

import com.skybird.create_jp_signal.AllItems; // AllItemsをインポート
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import java.nio.file.OpenOption;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DebugInputBlock extends BaseEntityBlock {

    public DebugInputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (pPlayer.getItemInHand(pHand).is(AllItems.SIGNAL_CONFIGURATOR.get())) {
            // configurator持っている場合、ブロックは何もしない(PASS)で、アイテム側の処理に任せる
            return InteractionResult.PASS;
        }

        if (!pLevel.isClientSide) {
            if (pLevel.getBlockEntity(pPos) instanceof DebugInputBlockEntity be) {
                be.cycleIndex();
                pPlayer.displayClientMessage(Component.literal("Index: " + be.getRedSignalIndex(3) + ", Shunt: " + (be.getReserverOperationType() == OperationType.SHUNT)), true);
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DebugInputBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}