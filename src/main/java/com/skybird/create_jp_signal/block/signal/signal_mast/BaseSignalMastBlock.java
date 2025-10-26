package com.skybird.create_jp_signal.block.signal.signal_mast;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.skybird.create_jp_signal.AllItems;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class BaseSignalMastBlock extends BaseSignalBlock {

    public static final BooleanProperty WALL_MOUNTED = BooleanProperty.create("wall_mounted");
    public static final DirectionProperty ATTACH_FACE = HorizontalDirectionalBlock.FACING;

    public BaseSignalMastBlock(Properties properties) {
        super(
            properties
            .noOcclusion()
            .destroyTime(1.5F)
            .explosionResistance(6.0F)
        );
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(WALL_MOUNTED, false)
            .setValue(ATTACH_FACE, Direction.NORTH));
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        // YourItems.YOUR_ITEM.get() の部分を、ドロップさせたいアイテムに書き換えてください。
        return Collections.singletonList(new ItemStack(AllItems.SIGNAL_MAST_WITH_SIGNAL.get()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WALL_MOUNTED, ATTACH_FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickFace = context.getClickedFace();
        
        boolean wallMounted;
        Direction attachFace;

        // 壁面への設置か
        if (clickFace.getAxis().isVertical()) {
            wallMounted = false;
            attachFace = context.getHorizontalDirection().getOpposite();
        } else {
            wallMounted = true;
            attachFace = clickFace;
        }
        
        return this.defaultBlockState()
            .setValue(WALL_MOUNTED, wallMounted)
            .setValue(ATTACH_FACE, attachFace);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(AllItems.SIGNAL_MAST_WITH_SIGNAL.get());

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BaseSignalMastBlockEntity mastBE) {
            String blockId = ForgeRegistries.BLOCKS.getKey(this).toString();
            stack.getOrCreateTag().putString("SelectedBlockType", blockId);
        }

        return stack;
    }

    // 当たり判定
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BaseSignalMastBlockEntity mastBE) {
            // BlockEntityからX, Z位置を取得して当たり判定を動的に生成
            double x = mastBE.getXPos();
            double z = mastBE.getZPos();
            return Block.box(x - 2, 0, z - 2, x + 2, 16, z + 2);
        }
        return Shapes.empty();
    }
    

}