// package com.skybird.create_jp_signal.block;

// import javax.annotation.Nullable;
// import net.minecraft.core.BlockPos;
// import net.minecraft.world.item.context.BlockPlaceContext;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.level.block.BaseEntityBlock;
// import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.block.HorizontalDirectionalBlock;
// import net.minecraft.world.level.block.RenderShape;
// import net.minecraft.world.level.block.entity.BlockEntity;
// import net.minecraft.world.level.block.state.BlockState;
// import net.minecraft.world.level.block.state.StateDefinition;
// import net.minecraft.world.level.block.state.properties.DirectionProperty;

// public class Signal3LBlock extends BaseEntityBlock { //old
//     // 1. ブロックが向ける方角を定義するプロパティ
//     public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

//     public Signal3LBlock (Properties properties) {
//         //透過
//         super(properties.noOcclusion());
//         // 2. ブロックの初期状態を設定 (デフォルトでは北を向く)
//         this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH));
//     }


//     // 3. ブロック設置時に呼ばれ、向きを決定するメソッド
//     @Override
//     public BlockState getStateForPlacement(BlockPlaceContext pContext) {
//         return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
//     }

//     // 4. ブロックが持つことができるプロパティを登録するメソッド
//     @Override
//     protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
//         pBuilder.add(FACING);
//     }


//     @Nullable
//     @Override
//     public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
//         return new Signal3LBlockEntity(pPos, pState);
//     }


//     @Override
//     public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
//         super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

//         // サーバーサイドでのみ処理を実行
//         if (!pLevel.isClientSide) {
//             // このブロックが受け取っているレッドストーン信号の最強強度を取得
//             int power = pLevel.getBestNeighborSignal(pPos);

//             // BlockEntityを取得して、状態更新メソッドを呼び出す
//             BlockEntity be = pLevel.getBlockEntity(pPos);
//             if (be instanceof Signal3LBlockEntity signalBE) {
//                 signalBE.updateSignalState(power);
//             }
//         }
//     }



//     @Override
//     public RenderShape getRenderShape(BlockState pState) {
//         // この設定で、MinecraftはBlockEntityRendererを呼び出して描画するようになる
//         return RenderShape.ENTITYBLOCK_ANIMATED;
//     }
    
// }
