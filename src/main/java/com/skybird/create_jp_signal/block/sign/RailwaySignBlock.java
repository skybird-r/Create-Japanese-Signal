package com.skybird.create_jp_signal.block.sign;

import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class RailwaySignBlock extends BaseEntityBlock implements IWrenchable {
    private final RailwaySignType signType;
    public RailwaySignBlock(Properties properties, RailwaySignType type) {
        super(properties.noCollission().noOcclusion().strength(1.5F, 6)); signType = type;
    }
    public RailwaySignType signType() { return signType; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new RailwaySignBlockEntity(pos, state); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof RailwaySignBlockEntity sign) {
            var data = sign.settings();
            sign.setPlacementData((int)Math.round(placer.getYRot() / 22.5), data.xPos(), data.zPos());
        }
    }
    @Override public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getPlayer() != null && !context.getPlayer().mayBuild()) return InteractionResult.PASS;
        if (!context.getLevel().isClientSide && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof RailwaySignBlockEntity sign)
            sign.cycleHeight();
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if ((signType != RailwaySignType.SPEED_LIMIT && !signType.numberedMast()) || AllItemTags.WRENCH.matches(player.getItemInHand(hand))
                || !player.mayBuild() || player.isSpectator()) return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof RailwaySignBlockEntity sign) {
            NetworkHooks.openScreen(serverPlayer, sign, buffer -> {
                var settings = sign.settings();
                buffer.writeBlockPos(pos);
                buffer.writeVarInt(signType.numberedMast() ? settings.number() : settings.speed());
                if (!signType.numberedMast()) buffer.writeEnum(settings.variant());
                buffer.writeBoolean(settings.useLetterO());
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        RailwaySignSettings settings = level.getBlockEntity(pos) instanceof RailwaySignBlockEntity sign
            ? sign.settings() : new RailwaySignSettings();
        return selectionShape(settings, signType);
    }

    static VoxelShape selectionShape(RailwaySignSettings settings, RailwaySignType signType) {
        double x = settings.xPos(), z = settings.zPos();
        double angle = Math.toRadians(180 - settings.rotation() * 22.5);
        // Match SignalMastBlock's 4px-wide click target; entity collision remains disabled.
        double radius = 2;
        double minX = Double.POSITIVE_INFINITY, minZ = minX, maxX = Double.NEGATIVE_INFINITY, maxZ = maxX;
        for (double bx : new double[] {-signType.width() / 2.0, signType.width() / 2.0}) {
            for (double bz : signType.numberedMast() ? new double[] {2, 2.25} : new double[] {.5, 1}) {
                double px = x + bx * Math.cos(angle) + bz * Math.sin(angle);
                double pz = z - bx * Math.sin(angle) + bz * Math.cos(angle);
                minX = Math.min(minX, px); maxX = Math.max(maxX, px);
                minZ = Math.min(minZ, pz); maxZ = Math.max(maxZ, pz);
            }
        }
        double bottom = signType.boardBottom(settings) * 16, top = bottom + signType.height();
        double poleTop = signType.numberedMast() ? 16 : top;
        // Vanilla ray selection visits only this block's cell. Keep its outline in
        // that same cell instead of highlighting unreachable extensions above it.
        return Shapes.join(Shapes.or(Block.box(x - radius, 0, z - radius, x + radius, poleTop, z + radius),
            Block.box(minX, bottom, minZ, maxX, top, maxZ)), Shapes.block(), BooleanOp.AND);
    }
}
