package com.skybird.create_jp_signal.block.signal.signal_mast;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseSignalMastBlock extends BaseSignalBlock {

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 15);
    public static final IntegerProperty X_POS = IntegerProperty.create("x_pos", 0, 16);
    public static final IntegerProperty Z_POS = IntegerProperty.create("z_pos", 0, 16);
    public static final BooleanProperty WALL_MOUNTED = BooleanProperty.create("wall_mounted");
    public static final DirectionProperty ATTACH_FACE = HorizontalDirectionalBlock.FACING;

    public BaseSignalMastBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(ROTATION, 0)
            .setValue(X_POS, 8)
            .setValue(Z_POS, 8)
            .setValue(WALL_MOUNTED, false)
            .setValue(ATTACH_FACE, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, X_POS, Z_POS, WALL_MOUNTED, ATTACH_FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickFace = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        Vec3 clickLocation = context.getClickLocation();
        
        boolean wallMounted;
        Direction attachFace;
        int xPos, zPos;

        // 回転を8方向にスナップさせる (0, 2, 4, ..., 14)
        int rotation = (((int)Math.round(context.getRotation() / 45.0)) & 7) * 2;

        // 壁面への設置か
        if (clickFace.getAxis().isVertical()) {
            wallMounted = false;
            attachFace = context.getHorizontalDirection().getOpposite();
            
            double hitX = clickLocation.x - clickedPos.getX();
            double hitZ = clickLocation.z - clickedPos.getZ();
            
            xPos = snapToSignalGrid(hitX);
            zPos = snapToSignalGrid(hitZ);

        } else {
            wallMounted = true;
            attachFace = clickFace;

            switch (clickFace) {
                case NORTH -> { xPos = snapToSignalGrid(clickLocation.x - clickedPos.getX()); zPos = 13; }
                case SOUTH -> { xPos = snapToSignalGrid(clickLocation.x - clickedPos.getX()); zPos = 3;  }
                case WEST  -> { xPos = 13; zPos = snapToSignalGrid(clickLocation.z - clickedPos.getZ()); }
                case EAST  -> { xPos = 3;  zPos = snapToSignalGrid(clickLocation.z - clickedPos.getZ()); }
                default    -> { xPos = 8; zPos = 8; }
            }
        }
        
        return this.defaultBlockState()
            .setValue(ROTATION, rotation)
            .setValue(X_POS, xPos)
            .setValue(Z_POS, zPos)
            .setValue(WALL_MOUNTED, wallMounted)
            .setValue(ATTACH_FACE, attachFace);
    }
    
    private int snapToSignalGrid(double value) {
        if (value < (1.0 / 3.0)) return 3;
        if (value < (2.0 / 3.0)) return 8;
        return 13;
    }

    // 当たり判定
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(state.getValue(X_POS) - 2, 0, state.getValue(Z_POS) - 2, state.getValue(X_POS) + 2, 16, state.getValue(Z_POS) + 2);
    }
    

}