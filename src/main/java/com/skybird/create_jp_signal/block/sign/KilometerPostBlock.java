package com.skybird.create_jp_signal.block.sign;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class KilometerPostBlock extends BaseEntityBlock implements IWrenchable {
    public KilometerPostBlock(Properties properties) {
        super(properties.noOcclusion().strength(1.5F, 6.0F));
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new KilometerPostBlockEntity(pos, state); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof KilometerPostBlockEntity marker) {
            marker.setPlacementData((int) Math.round(placer.getYRot() / 22.5) & 15, marker.getXPos(), marker.getZPos());
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        double x = 8, z = 8;
        if (level.getBlockEntity(pos) instanceof KilometerPostBlockEntity marker) {
            x = marker.getXPos(); z = marker.getZPos();
        }
        return Block.box(x - 3, 0, z - 3, x + 3, 21, z + 3);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.mayBuild()) return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof KilometerPostBlockEntity marker) {
            NetworkHooks.openScreen(serverPlayer, marker, buffer -> { buffer.writeBlockPos(pos); buffer.writeVarInt(marker.getDistance()); buffer.writeBoolean(marker.useLetterO()); });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, net.minecraft.world.item.context.UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof KilometerPostBlockEntity marker) {
            marker.setPlacementData(marker.getRotation() + 1, marker.getXPos(), marker.getZPos());
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
