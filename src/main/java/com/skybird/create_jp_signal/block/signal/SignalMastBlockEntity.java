package com.skybird.create_jp_signal.block.signal;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.menu.MastConfigMenu;
import com.skybird.create_jp_signal.menu.MastLinkMenu;

import io.netty.util.Signal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignalMastBlockEntity extends BlockEntity implements MenuProvider { 

    public enum AttachmentSlot { PRIMARY, SECONDARY }

    //private ISignalType signalType;
    private final Map<AttachmentSlot, SignalHead> signalHeads = new EnumMap<>(AttachmentSlot.class);

    private ISignalType signalType;

    public SignalMastBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.SIGNAL_MAST_ENTITY.get(), pPos, pState);
    }
    
    // --- 自己診断・クリーンアップ機能 ---
    public static void tick(Level level, BlockPos pos, BlockState state, SignalMastBlockEntity be) {
        if (level.isClientSide || be.signalHeads.isEmpty() || level.getGameTime() % 100 != 0) return;
        
        boolean hasChanged = false;
        for (SignalHead head : be.signalHeads.values()) {
            BlockPos controllerPos = head.getControllerPos();
            if (controllerPos == null) continue;

            // コントローラーのチャンクがロードされているか確認
            if (level.isLoaded(controllerPos)) {
                // コントローラーが本当に存在し、自分を指しているか逆確認
                if (level.getBlockEntity(controllerPos) instanceof ControlBoxBlockEntity controlBox) {
                    if (controlBox.isLinkedTo(be.getBlockPos(), head.getUniqueId())) {
                        continue; // 正常なリンクなのでスキップ
                    }
                }
                // リンクが不正（コントローラーが存在しない or 自分を指していない）なのでクリーンアップ
                head.setControllerPos(null);
                hasChanged = true;
            }
        }

        if (hasChanged) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    // --- 設定変更ロジック ---
    public void reconfigureMast(ResourceLocation typeId, CompoundTag newAppearanceData, int newHeadCount) {
        if (this.level == null || this.level.isClientSide) return;
        ISignalType newType = AllSignalTypes.get(typeId);
        if (newType == null) return;

        ISignalType oldType = this.signalType;
        List<BlockPos> controllersToReset = new ArrayList<>();
        Map<AttachmentSlot, SignalHead> oldHeads = new EnumMap<>(this.signalHeads);

        boolean isTypeChange = (oldType == null || !oldType.getId().equals(newType.getId()));
        
        if (isTypeChange) { // 種類が変更されたら全リセット
            oldHeads.values().stream()
                .filter(h -> h.getControllerPos() != null)
                .forEach(h -> controllersToReset.add(h.getControllerPos()));
            this.signalHeads.clear();
        }

        this.signalType = newType;
        
        // 新しいSignalHeadリストを作成
        Map<AttachmentSlot, SignalHead> newHeads = new EnumMap<>(AttachmentSlot.class);
        for (int i = 0; i < newHeadCount && i < newType.getMaxAttachmentsPerMast(); i++) {
            AttachmentSlot slot = i == 0 ? AttachmentSlot.PRIMARY : AttachmentSlot.SECONDARY;
            SignalHead existingHead = oldHeads.get(slot);

            if (!isTypeChange && existingHead != null) {
                // ★★★ ここからが修正ロジック (既存Headの再利用) ★★★
                // 既存のHeadをそのまま新しいリストに入れる
                newHeads.put(slot, existingHead);
                
                // 既存のAppearanceオブジェクトを取得して、中身だけを編集する
                ISignalAppearance appearanceToModify = existingHead.getAppearance();
                if (appearanceToModify instanceof ColorLightSignalAppearance clAppearance && newAppearanceData.contains("BackplateType")) {
                    try {
                        // GUIから送られてきたBackplateType名で、既存の設定を上書き
                        ColorLightSignalAppearance.BackplateType newBackplate = ColorLightSignalAppearance.BackplateType.valueOf(newAppearanceData.getString("BackplateType"));
                        clAppearance.setBackplateType(newBackplate);
                    } catch (IllegalArgumentException e) {
                        // 不正な名前が送られてきた場合は何もしない
                    }
                }
                // ★★★ ここまで ★★★
    
            } else {
                // ★★★ ここからが修正ロジック (新規Headの作成) ★★★
                // まずはデフォルトのAppearanceを生成
                ISignalAppearance appearance = newType.createDefaultAppearance();
                
                // 生成したデフォルトAppearanceを、GUIからの情報で部分的に更新
                if (appearance instanceof ColorLightSignalAppearance clAppearance && newAppearanceData.contains("BackplateType")) {
                     try {
                        ColorLightSignalAppearance.BackplateType newBackplate = ColorLightSignalAppearance.BackplateType.valueOf(newAppearanceData.getString("BackplateType"));
                        clAppearance.setBackplateType(newBackplate);
                    } catch (IllegalArgumentException e) {
                        // 不正な名前の場合はデフォルトのまま
                    }
                }
                
                // 更新されたAppearanceで新しいHeadを生成
                SignalHead newHead = new SignalHead(UUID.randomUUID(), appearance, null);
                newHeads.put(slot, newHead);
                // ★★★ ここまで ★★★
            }
        }

        this.signalHeads.clear();
        this.signalHeads.putAll(newHeads);
        
        // 古い設定で使われていて、新しい設定では使われなくなったリンクを探す
        oldHeads.forEach((slot, head) -> {
            if (head.getControllerPos() != null && !newHeads.containsKey(slot)) {
                controllersToReset.add(head.getControllerPos());
            }
        });

        // 孤児になった制御盤にリセットを通知
        for (BlockPos controllerPos : controllersToReset) {
            if (level.isLoaded(controllerPos) && level.getBlockEntity(controllerPos) instanceof ControlBoxBlockEntity controlBox) {
                controlBox.resetLink();
            }
        }

        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
    
    // --- 外部からの操作メソッド ---
    
    public boolean claimSignalHead(UUID headId, BlockPos controllerPos) {
        for (SignalHead head : this.signalHeads.values()) {
            if (head.getUniqueId().equals(headId)) {
                if (head.getControllerPos() != null) return false;
                head.setControllerPos(controllerPos);
                this.setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                return true;
            }
        }
        return false;
    }

    public void releaseSignalHead(UUID headId) { // 引数をBlockPosからUUIDに変更
        signalHeads.values().stream()
            .filter(head -> head.getUniqueId().equals(headId)) // IDでHeadを特定
            .findFirst()
            .ifPresent(head -> {
                head.setControllerPos(null); // 所有者をnullに戻す
                this.setChanged();
                if (this.level != null) {
                    this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                }
            });
    }

    public void updateAspect(UUID headId, SignalAspect.State newAspect, SignalAccessory.Route newRoute) {
        signalHeads.values().stream()
            .filter(head -> head.getUniqueId().equals(headId))
            .findFirst()
            .ifPresent(head -> {
                if (head.getCurrentAspect() != newAspect || head.getCurrentRoute() != newRoute) {
                    head.setCurrentAspect(newAspect);
                    head.setCurrentRoute(newRoute);
                    this.setChanged();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            });
    }

    

    /**
     * 指定されたIDを持つSignalHeadの「見た目」を更新する。
     * このメソッドは主にControlBoxから呼び出される。
     * @param headId 更新対象のSignalHeadのUUID
     * @param newAppearance 新しい見た目のデータ
     */
    public void updateSignalHeadAppearance(UUID headId, ISignalAppearance newAppearance) { //不要
        if (this.level == null || this.level.isClientSide) return;

        // マップから該当するHeadを探す
        this.signalHeads.values().stream()
            .filter(head -> head.getUniqueId().equals(headId))
            .findFirst()
            .ifPresent(head -> {
                // 見つかったHeadのAppearanceを更新
                head.setAppearance(newAppearance.copy());
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            });
    }

    // --- ゲッター ---
    public Map<AttachmentSlot, SignalHead> getSignalHeads() { return this.signalHeads; } //不要
    @Nullable public ISignalType getSignalType() { return this.signalType; }
    public boolean isHeadControlledBy(UUID headId, BlockPos controllerPos) {
        for (SignalHead head : this.signalHeads.values()) {
            if (head.getUniqueId().equals(headId)) {
                return controllerPos.equals(head.getControllerPos());
            }
        }
        return false;
    }

    @Nullable // このメソッドはnullを返す可能性があることを示す
    public SignalHead getSignalHead(UUID headId) {
        // Mapに保存されている全SignalHead（値）に対してループ
        for (SignalHead head : this.signalHeads.values()) {
            // もしHeadのUUIDが、引数で受け取ったheadIdと一致したら
            if (head.getUniqueId().equals(headId)) {
                // そのHeadを返して処理を終了する
                return head;
            }
        }
        // ループが最後まで終わっても見つからなかった場合は、nullを返す
        return null;
    }

    /**
     * 紐付け時にスロット選択GUIを開くための、特別なMenuProviderを返すメソッド
     * @param controlBoxPos 紐付け元の制御盤の座標
     */
    public MenuProvider getLinkMenuProvider(BlockPos controlBoxPos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("信号機スロット選択");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                // GUIに、自分自身の情報(this)と、紐付け元の制御盤の座標(controlBoxPos)の両方を渡す
                return new MastLinkMenu(pContainerId, pInventory, SignalMastBlockEntity.this, controlBoxPos);
            }
        };
    }




    @Override
    public Component getDisplayName() {
        return Component.literal("信号柱 設定");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new MastConfigMenu(pContainerId, pInventory, this);
    }

    // --- NBT & 同期 ---
    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.signalType != null) {
            tag.putString("SignalType", this.signalType.getId().toString());
        }
        
        ListTag headList = new ListTag();
        signalHeads.forEach((slot, head) -> {
            CompoundTag headTag = new CompoundTag();
            headTag.putString("Slot", slot.name());
            head.writeNbt(headTag);
            headList.add(headTag);
        });
        tag.put("SignalHeads", headList);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("SignalType")) {
            this.signalType = AllSignalTypes.get(new ResourceLocation(tag.getString("SignalType")));
        }
        
        this.signalHeads.clear();
        ListTag headList = tag.getList("SignalHeads", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < headList.size(); i++) {
            CompoundTag headTag = headList.getCompound(i);
            try {
                AttachmentSlot slot = AttachmentSlot.valueOf(headTag.getString("Slot"));
                SignalHead head = SignalHead.fromNbt(headTag);
                this.signalHeads.put(slot, head);
            } catch (IllegalArgumentException e) { /* 不正データは無視 */ }
        }
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag() { return this.saveWithoutMetadata(); }
}