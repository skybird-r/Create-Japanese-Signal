package com.skybird.create_jp_signal.block.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class KilometerPostBlockItem extends BlockItem {
    public KilometerPostBlockItem(Block block, Properties properties) { super(block, properties); }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (!super.placeBlock(context, state)) return false;
        BlockPos pos = context.getClickedPos();
        if (context.getLevel().getBlockEntity(pos) instanceof KilometerPostBlockEntity marker) {
            Vec3 hit = context.getClickLocation();
            int x = snap(hit.x - pos.getX()), z = snap(hit.z - pos.getZ());
            switch (context.getClickedFace()) {
                case NORTH -> z = 13;
                case SOUTH -> z = 3;
                case WEST -> x = 13;
                case EAST -> x = 3;
                default -> { }
            }
            marker.setPlacementData((int) Math.round(context.getRotation() / 22.5) & 15, x, z);
        }
        return true;
    }

    private static int snap(double value) {
        return value < 1.0 / 3 ? 3 : value < 2.0 / 3 ? 8 : 13;
    }
}
