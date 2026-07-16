package com.skybird.create_jp_signal.block.signal.signal_mast;

import com.skybird.create_jp_signal.AllBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class SignalMastBlockItem extends BlockItem {

    public SignalMastBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos clickedPos = context.getClickedPos();
        Direction clickFace = context.getClickedFace();
        BlockPos placePos = clickedPos.relative(clickFace);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player == null) {
            // プレイヤーがいない場合 (ディスペンサーなど) は処理を中断
            return InteractionResult.FAIL;
        }

        if (!level.getBlockState(placePos).canBeReplaced(new BlockPlaceContext(context))) {
            return InteractionResult.FAIL;
        }

        // 2. ブロックを設置
        BlockState placementState = AllBlocks.SIGNAL_MAST.get().getStateForPlacement(new BlockPlaceContext(context));
        if (level.setBlock(placePos, placementState, 3)) {
            BlockEntity be = level.getBlockEntity(placePos);
            if (be instanceof SignalMastBlockEntity mastBE) {
                
                // 3. クリック位置から xPos と zPos を計算 (元のロジックをここに持ってくる)
                Vec3 clickLocation = context.getClickLocation();
                int xPos, zPos;
                
                // 壁面への設置か
                if (clickFace.getAxis().isVertical()) {
                    double hitX = clickLocation.x - clickedPos.getX();
                    double hitZ = clickLocation.z - clickedPos.getZ();
                    xPos = snapToSignalGrid(hitX);
                    zPos = snapToSignalGrid(hitZ);
                } else {
                        switch (clickFace) {
                        case NORTH -> { xPos = snapToSignalGrid(clickLocation.x - clickedPos.getX()); zPos = 13; }
                        case SOUTH -> { xPos = snapToSignalGrid(clickLocation.x - clickedPos.getX()); zPos = 3;  }
                        case WEST  -> { xPos = 13; zPos = snapToSignalGrid(clickLocation.z - clickedPos.getZ()); }
                        case EAST  -> { xPos = 3;  zPos = snapToSignalGrid(clickLocation.z - clickedPos.getZ()); }
                        default    -> { xPos = 8; zPos = 8; }
                    }
                }

                // setPlacedByにあった計算式をそのまま持ってくる
                int rotation = (((int)Math.round(player.getYRot() / 45.0)) & 7) * 2;

                // 4. BlockEntityにデータを書き込む (RotationはsetPlacedByで設定されるので不要)
                mastBE.setPlacementData(rotation, xPos, zPos); // rotationは後で上書きされる
            }

            // 5. アイテムを消費する処理
            if (context.getPlayer() != null && !((ServerPlayer)context.getPlayer()).isCreative()) {
                stack.shrink(1);
            }

            return InteractionResult.CONSUME;

        }
        return InteractionResult.FAIL;
    }
    
    // 元のBlockクラスにあったヘルパーメソッド
    private int snapToSignalGrid(double value) {
        if (value < 0) value += 1.0; // 負の座標に対応
        if (value > 1) value -= 1.0;
        
        if (value < (1.0 / 3.0)) return 3;
        if (value < (2.0 / 3.0)) return 8;
        return 13;
    }

}
