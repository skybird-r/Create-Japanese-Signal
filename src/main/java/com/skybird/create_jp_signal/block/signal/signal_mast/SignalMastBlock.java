package com.skybird.create_jp_signal.block.signal.signal_mast;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.block.signal.signal_mast.SignalMastBlockEntity;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SignalMastBlock extends BaseEntityBlock implements IWrenchable {

    // --- BlockState Properties ---
    // public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 15);
    // public static final IntegerProperty X_POS = IntegerProperty.create("x_pos", 0, 16);
    // public static final IntegerProperty Z_POS = IntegerProperty.create("z_pos", 0, 16);
    // public static final BooleanProperty WALL_MOUNTED = BooleanProperty.create("wall_mounted");
    // public static final DirectionProperty ATTACH_FACE = HorizontalDirectionalBlock.FACING;

    public SignalMastBlock(Properties properties) {
        super(
            properties
            .noOcclusion()
            .destroyTime(1.5F)
            .explosionResistance(6.0F)
        );
        this.registerDefaultState(this.stateDefinition.any());
            // .setValue(ROTATION, 0)
            // .setValue(X_POS, 8).setValue(Z_POS, 8) // デフォルトは中心(8,8)
            // .setValue(WALL_MOUNTED, false)
            // .setValue(ATTACH_FACE, Direction.NORTH));
    }

    // @Override
    // protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
    //     pBuilder.add(ROTATION, X_POS, Z_POS, WALL_MOUNTED, ATTACH_FACE);
    // }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        // YourItems.YOUR_ITEM.get() の部分を、ドロップさせたいアイテムに書き換えてください。
        return Collections.singletonList(new ItemStack(AllBlocks.SIGNAL_MAST_ITEM.get()));
    }

    // /**
    //  * ブロック設置時のロジック。クリック位置に応じて状態を決定する。
    //  */
    // @Override
    // public BlockState getStateForPlacement(BlockPlaceContext pContext) {
    //     // --- クリック情報とプレイヤーの向きを取得 ---
    //     Direction clickFace = pContext.getClickedFace();
    //     BlockPos clickedPos = pContext.getClickedPos();
    //     Vec3 clickLocation = pContext.getClickLocation();
        
    //     // --- 各状態の変数を初期化 ---
    //     boolean wallMounted;
    //     Direction attachFace;
    //     int xPos, zPos;

    //     // --- 回転の計算 (8方向, 22.5度ごと) ---
    //     // 0-7の値を計算し、2を掛けることで偶数値(0, 2, 4...14)にする
    //     int rotation = (((int)Math.round(pContext.getRotation() / 45.0)) & 7) * 2;

    //     // --- 設置位置の計算 ---
    //     if (clickFace.getAxis().isVertical()) {
    //         // --- 床・天井への設置 ---
    //         wallMounted = false;
    //         attachFace = pContext.getHorizontalDirection().getOpposite();
            
    //         // ブロック内の相対座標 (0.0 ~ 1.0) を取得
    //         double hitX = clickLocation.x - clickedPos.getX();
    //         double hitZ = clickLocation.z - clickedPos.getZ();
            
    //         xPos = snapToSignalGrid(hitX);
    //         zPos = snapToSignalGrid(hitZ);

    //     } else {
    //         // --- 壁面への設置 ---
    //         wallMounted = true;
    //         attachFace = clickFace;

    //         // 壁の向きに応じて、スナップさせる軸を変える
    //         switch (clickFace) {
    //             case NORTH:
    //                 xPos = snapToSignalGrid(clickLocation.x - clickedPos.getX());
    //                 zPos = 13; // 壁際
    //                 break;
    //             case SOUTH:
    //                 xPos = snapToSignalGrid(clickLocation.x - clickedPos.getX());
    //                 zPos = 3; // 壁際
    //                 break;
    //             case WEST:
    //                 xPos = 13; // 壁際
    //                 zPos = snapToSignalGrid(clickLocation.z - clickedPos.getZ());
    //                 break;
    //             case EAST:
    //                 xPos = 3; // 壁際
    //                 zPos = snapToSignalGrid(clickLocation.z - clickedPos.getZ());
    //                 break;
    //             default: // ありえないが念のため
    //                 xPos = 8; zPos = 8;
    //         }
    //     }
        
    //     // --- 最終的なBlockStateを返す ---
    //     return this.defaultBlockState()
    //         .setValue(ROTATION, rotation)
    //         .setValue(X_POS, xPos)
    //         .setValue(Z_POS, zPos)
    //         .setValue(WALL_MOUNTED, wallMounted)
    //         .setValue(ATTACH_FACE, attachFace);
    // }
    
    /**
     * 0.0~1.0の値を3, 8, 13のいずれかにスナップさせるヘルパーメソッド
     */
    // private int snapToSignalGrid(double value) {
    //     if (value < (1.0 / 3.0)) {
    //         return 3;
    //     } else if (value < (2.0 / 3.0)) {
    //         return 8;
    //     } else {
    //         return 13;
    //     }
    // }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SignalMastBlockEntity mastBE) {
            // BlockEntityからX, Z位置を取得して当たり判定を動的に生成
            double x = mastBE.getXPos();
            double z = mastBE.getZPos();
            return Block.box(x - 2, 0, z - 2, x + 2, 16, z + 2);
        }
        return Shapes.empty();
    }

    
    // --- 以下、必須のメソッド ---
    
    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SignalMastBlockEntity(pPos, pState);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    /*
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            System.out.println("Signal Mast State: " + pState.toString());
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
    */
}