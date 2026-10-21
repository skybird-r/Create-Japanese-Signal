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

public class GradientPostBlock extends BaseEntityBlock implements IWrenchable {
    public GradientPostBlock(Properties properties) {
        super(properties.noOcclusion().strength(1.5F, 6.0F));
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GradientPostBlockEntity(pos, state); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof GradientPostBlockEntity marker) {
            marker.setPlacementData((int) Math.round(placer.getYRot() / 22.5) & 15, marker.getXPos(), marker.getZPos());
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        double x = 8, z = 8;
        int rotation = 0;
        if (level.getBlockEntity(pos) instanceof GradientPostBlockEntity marker) {
            x = marker.getXPos(); z = marker.getZPos();
            rotation = marker.getRotation();
        }
        double angle = Math.toRadians(180 - rotation * 22.5);
        double shaftRadius = 1.5 * (Math.abs(Math.cos(angle)) + Math.abs(Math.sin(angle)));
        // The full sweep covers all three bar slopes, including their outer tips.
        double minX = x - shaftRadius, maxX = x + shaftRadius;
        double minZ = z - shaftRadius, maxZ = z + shaftRadius;
        for (double localX : new double[] {-10.1, 2.5}) {
            for (double localZ : new double[] {-2, 2}) {
                double worldX = x + localX * Math.cos(angle) + localZ * Math.sin(angle);
                double worldZ = z - localX * Math.sin(angle) + localZ * Math.cos(angle);
                minX = Math.min(minX, worldX); maxX = Math.max(maxX, worldX);
                minZ = Math.min(minZ, worldZ); maxZ = Math.max(maxZ, worldZ);
            }
        }
        return net.minecraft.world.phys.shapes.Shapes.or(
            Block.box(x - shaftRadius, 0, z - shaftRadius, x + shaftRadius, 18, z + shaftRadius),
            Block.box(minX, 6, minZ, maxX, 19, maxZ));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.mayBuild()) return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof GradientPostBlockEntity marker) {
            NetworkHooks.openScreen(serverPlayer, marker, buffer -> {
                buffer.writeBlockPos(pos);
                buffer.writeVarInt(marker.getFrontGradient());
                buffer.writeVarInt(marker.getBackGradient());
                buffer.writeBoolean(marker.showDecimals());
                buffer.writeBoolean(marker.useLetterO());
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, net.minecraft.world.item.context.UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof GradientPostBlockEntity marker) {
            marker.setPlacementData(marker.getRotation() + 1, marker.getXPos(), marker.getZPos());
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
