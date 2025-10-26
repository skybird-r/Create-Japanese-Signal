package com.skybird.create_jp_signal.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

// BaseSignalMastBlockEntity をインポートしておく
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;

import java.util.List;

import javax.annotation.Nullable;

// BaseSignalMastBlock をインポートしておく
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlock;

public class SignalMastWithSignalItem extends Item {

    public SignalMastWithSignalItem(Properties pProperties) {
        super(pProperties);
    }

    // Shift + 右クリックでGUIを開くなどの処理は use メソッドに書く (今回は省略)
    // @Override
    // public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) { ... }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        CompoundTag nbt = pStack.getTag();
        if (nbt != null && nbt.contains("SelectedBlockType")) {
            String blockId = nbt.getString("SelectedBlockType");
            // "create_jp_signal:color_single_round_signal_mast" -> "Color Single Round Signal Mast" のように整形
            String formattedName = formatBlockId(blockId);

            pTooltipComponents.add(Component.translatable("tooltip.create_jp_signal.selected_signal", formattedName)
                .withStyle(ChatFormatting.GRAY));
        } else {
            // NBTがない場合のデフォルト表示
            pTooltipComponents.add(Component.translatable("tooltip.create_jp_signal.selected_signal_default")
                .withStyle(ChatFormatting.DARK_GRAY));
        }

        // 操作方法のヒントを追加
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.create_jp_signal.signal_tool.shift_instructions")
                .withStyle(ChatFormatting.AQUA));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.create_jp_signal.signal_tool.instructions")
                .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private String formatBlockId(String blockId) {
        // "create_jp_signal:" のような名前空間部分を削除
        if (blockId.contains(":")) {
            blockId = blockId.substring(blockId.indexOf(":") + 1);
        }
        // "_mast" を削除
        blockId = blockId.replace("_mast", "");
        // "_" を " " に置換
        String[] parts = blockId.split("_");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
        }
        return String.join(" ", parts);
    }

    // ブロックへの右クリックで設置処理を行う
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
        
        // 1. アイテムのNBTから、設置するブロックのIDを取得する
        CompoundTag nbt = stack.getOrCreateTag();
        String blockIdStr = nbt.getString("SelectedBlockType");
        if (blockIdStr.isEmpty()) {
            // NBTがなければデフォルトのブロックIDを設定する（例）
            blockIdStr = "create_jp_signal:color_single_round_signal_mast"; 
        }

        Block blockToPlace = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockIdStr));
        if (blockToPlace == null || !(blockToPlace instanceof BaseSignalMastBlock)) {
            return InteractionResult.FAIL; // 設置するブロックが見つからない
        }

        // 2. ブロックを設置
        BlockState placementState = blockToPlace.getStateForPlacement(new BlockPlaceContext(context));
        if (level.setBlock(placePos, placementState, 3)) {
            BlockEntity be = level.getBlockEntity(placePos);
            if (be instanceof BaseSignalMastBlockEntity mastBE) {
                
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