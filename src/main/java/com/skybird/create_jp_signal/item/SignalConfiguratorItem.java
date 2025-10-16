package com.skybird.create_jp_signal.item;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
import com.skybird.create_jp_signal.block.signal.ISignalIndexSource;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class SignalConfiguratorItem extends Item {

    private static final String TAG_FIRST_POS = "FirstPos";

    public SignalConfiguratorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos clickedPos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();

        if (level.isClientSide || player == null) return InteractionResult.SUCCESS;

        // --- シフト右クリックの処理 (変更なし) ---
        // if (player.isShiftKeyDown()) {
        //     clearLinkData(stack, player, "リンクモードを中断しました。");
        //     BlockEntity be = level.getBlockEntity(clickedPos);
        //     if (be instanceof BaseSignalBlockEntity signal) {
        //         NetworkHooks.openScreen((ServerPlayer) player, signal, clickedPos);
        //     } else if (be instanceof ControlBoxBlockEntity controlBox) {
        //         if (controlBox.isLinked()) {
        //             controlBox.resetLink();
        //             player.displayClientMessage(Component.literal("信号機とのリンクを解除しました。"), true);
        //         }
        //     }
        //     return InteractionResult.SUCCESS;
        // }

        //右クリック
        BlockPos firstPos = getPos(stack);
        if (firstPos == null) {
            if (level.getBlockEntity(clickedPos) instanceof ControlBoxBlockEntity) {
                setLinkData(stack, clickedPos);
            } else {
            }
        } else {
            
            BlockEntity firstBe = level.getBlockEntity(firstPos);
            if (!(firstBe instanceof ControlBoxBlockEntity controlBox)) {
                player.displayClientMessage(Component.literal("記憶していた制御盤が見つかりません！"), true);
                clearLinkData(stack, player, null);
                return InteractionResult.FAIL;
            }
            BlockEntity secondBe = level.getBlockEntity(clickedPos);

            if (secondBe instanceof BaseSignalBlockEntity signal) {
                NetworkHooks.openScreen((ServerPlayer) player, signal.getLinkMenuProvider(firstPos), buf -> {
                    // 1番目に「信号柱」の座標を書き込む
                    buf.writeBlockPos(signal.getBlockPos());
                    // 2番目に「制御盤」の座標を書き込む
                    buf.writeBlockPos(firstPos);
                });
                clearLinkData(stack, player, null);
            } else if (secondBe instanceof BaseSignalBlockEntity signal) {
                NetworkHooks.openScreen((ServerPlayer) player, signal.getLinkMenuProvider(firstPos), buf -> {
                    // 1番目に「信号柱」の座標を書き込む
                    buf.writeBlockPos(signal.getBlockPos());
                    // 2番目に「制御盤」の座標を書き込む
                    buf.writeBlockPos(firstPos);
                });
                clearLinkData(stack, player, null);

            } else if (secondBe instanceof ISignalIndexSource) {
                controlBox.linkToIndexSource(clickedPos);
                player.displayClientMessage(Component.literal("Indexブロックを紐付けました。"), true);
                clearLinkData(stack, player, null);
            } else {
                player.displayClientMessage(Component.literal("対象のブロックではありません。リンクモードを維持します。"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // アイテムのツールチップに現在の状態を表示
    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull List<Component> pTooltip, @Nonnull TooltipFlag pFlag) {
        BlockPos pos = getPos(pStack);
        if (pos != null) {
            pTooltip.add(Component.literal("紐付け元選択中: " + pos.toShortString()));
        }
    }

    // --- NBTヘルパー ---
    private static void setLinkData(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTag().put(TAG_FIRST_POS, NbtUtils.writeBlockPos(pos));
    }

    private static void clearLinkData(ItemStack stack, Player player, @Nullable String message) {
        CompoundTag tag = stack.getTag();
        if (stack.hasTag() && tag != null && tag.contains(TAG_FIRST_POS)) {
            tag.remove(TAG_FIRST_POS);
            if (message != null) {
                player.displayClientMessage(Component.literal(message), true);
            }
        }
    }
    
    @Nullable
    private static BlockPos getPos(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (stack.hasTag() && tag != null && tag.contains(TAG_FIRST_POS)) {
            return NbtUtils.readBlockPos(tag.getCompound(TAG_FIRST_POS));
        }
        return null;
    }
}