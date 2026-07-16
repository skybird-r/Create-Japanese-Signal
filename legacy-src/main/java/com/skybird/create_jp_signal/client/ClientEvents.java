package com.skybird.create_jp_signal.client;

import com.skybird.create_jp_signal.client.gui.SelectSignalMastScreen;
import com.skybird.create_jp_signal.item.SignalMastWithSignalItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "create_jp_signal", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        // プレイヤーがスニーク中（シフトキーを押している）か
        // かつ、手に持っているアイテムが SignalMastWithSignalItem かどうか
        if (event.getEntity().isShiftKeyDown() && event.getItemStack().getItem() instanceof SignalMastWithSignalItem) {
            // クライアントサイドでのみGUIを開く
            if (event.getLevel().isClientSide) {
                Minecraft.getInstance().setScreen(new SelectSignalMastScreen());
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        // 条件判定は全く同じ
        if (event.getEntity().isShiftKeyDown() && event.getItemStack().getItem() instanceof SignalMastWithSignalItem) {
            if (event.getLevel().isClientSide) {
                Minecraft.getInstance().setScreen(new SelectSignalMastScreen());
            }
            // イベントをキャンセルして、ブロック破壊を中止させる
            event.setCanceled(true);
            // サーバー側にこれ以上処理を送らないようにする
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
