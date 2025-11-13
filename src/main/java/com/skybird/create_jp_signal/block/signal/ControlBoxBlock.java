package com.skybird.create_jp_signal.block.signal;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.AllItems;
import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ControlBoxBlock extends BaseEntityBlock implements IWrenchable {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ControlBoxBlock(Properties properties) {
        super(
            properties
            .noOcclusion()
            .destroyTime(1.5F)
            .explosionResistance(6.0F)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ControlBoxBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        return Collections.singletonList(new ItemStack(AllBlocks.CONTROL_BOX_ITEM.get()));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).is(AllItems.SIGNAL_CONFIGURATOR.get())) {
            return InteractionResult.PASS;
        }

        // コンフィギュレーター以外で右クリックされた場合 (素手など)
        if (!pLevel.isClientSide) {
            if (pLevel.getBlockEntity(pPos) instanceof ControlBoxBlockEntity be) {
            
                NetworkHooks.openScreen((ServerPlayer) pPlayer, be, buf -> {
                    buf.writeBlockPos(pPos);

                    be.syncAppearanceFromSignal(); // 開く直前に同期
                    CompoundTag tag = new CompoundTag();
                    ISignalAppearance appearance = be.getAppearance();

                    if (appearance != null) {
                        buf.writeUtf(appearance.getTypeId());
                        appearance.writeNbt(tag);
                    } else {
                        buf.writeUtf("");
                    }
                    buf.writeNbt(tag);
                });
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, AllBlockEntities.CONTROL_BOX_ENTITY.get(), ControlBoxBlockEntity::tick);
    }
}