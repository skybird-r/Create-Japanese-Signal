package com.skybird.create_jp_signal.block.signal;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.SignalAspect.State;
import com.skybird.create_jp_signal.block.signal.debug.DebugInputBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.menu.ControlBoxMenu;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ControlBoxBlockEntity2 extends BlockEntity implements MenuProvider { // old

    // --- データ ---
    private ISignalAppearance appearance;
    private BlockPos linkedSignalPos;
    private UUID linkedHeadId;
    private final Map<BlockPos, AspectMapping> sourceMappings = new HashMap<>();

    public ControlBoxBlockEntity2(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.CONTROL_BOX_ENTITY.get(), pPos, pState);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, ControlBoxBlockEntity2 be) {
        if (level.isClientSide || !be.isLinked()) return;
        if (!level.isLoaded(be.linkedSignalPos)) return;
        if (!(level.getBlockEntity(be.linkedSignalPos) instanceof SignalMastBlockEntity mast) || !mast.isHeadControlledBy(be.linkedHeadId, pos)) {
            be.resetLink();
            return;
        }
        if (be.sourceMappings.isEmpty()) return;

        int highestAspectIndex = -1;
        SignalAspect.State finalAspect = null;
        SignalAccessory.Route finalRoute = SignalAccessory.Route.NONE;
        double finalReserverMaxSpeed = 0.0;
        boolean mappingsChanged = false;

        Iterator<Map.Entry<BlockPos, AspectMapping>> iterator = be.sourceMappings.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, AspectMapping> entry = iterator.next();
            BlockPos sourcePos = entry.getKey();
            if (!level.isLoaded(sourcePos)) continue;
            if (!(level.getBlockEntity(sourcePos) instanceof ISignalIndexSource source)) {
                iterator.remove();
                mappingsChanged = true;
                continue;
            }
            int currentIndex = source.getRedSignalIndex(entry.getValue().getMaxIndex()); // aspectの最大値を上限に開いている閉塞数を要求
            int currentAspectIndex = entry.getValue().getAspectFor(currentIndex).getAspectIndex();
            // 現示が一番高いものを採用
            if (currentAspectIndex > highestAspectIndex) {
                highestAspectIndex = currentAspectIndex;
                finalAspect = entry.getValue().getAspectFor(currentIndex);
                finalReserverMaxSpeed = source.getReserverMaxSpeed();
                // Route設定
                if (currentIndex <= 1 && be.appearance instanceof ColorLightSignalAppearance clAppearance && clAppearance.getAccessory().getType() == SignalAccessory.Type.FORECAST) {
                    // 入力が1以下かつ色灯かつ予告機なら、NONE
                } else if (currentIndex > 0) {
                    finalRoute = entry.getValue().getRoute();
                }
            }
        }
        if (finalReserverMaxSpeed <= 135.0 / 72) {
            if (finalAspect == State.GG_5B) {
                finalAspect = State.G_5B;
            } else if (finalAspect == State.GG_6) {
                finalAspect = State.G_6;
            }
        }

        if (finalAspect != null) mast.updateAspect(be.linkedHeadId, finalAspect, finalRoute);
        if (mappingsChanged) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public boolean linkToHead(SignalMastBlockEntity mast, UUID headId) {
        if (level == null || level.isClientSide) return false;
        if (this.isLinked()) this.resetLink();
        // リンク先のHeadから現在のAppearanceを取得する
        SignalHead targetHead = mast.getSignalHeads().values().stream()
            .filter(h -> h.getUniqueId().equals(headId))
            .findFirst().orElse(null);
        // Headが見つからなければリンク失敗
        if (targetHead == null) return false;
        boolean success = mast.claimSignalHead(headId, this.worldPosition);
        if (success) {
            this.linkedSignalPos = mast.getBlockPos();
            this.linkedHeadId = headId;
            //ISignalType signalType = mast.getSignalType();
            if (/*signalType*/targetHead.getAppearance() != null) {
                this.appearance = targetHead.getAppearance().copy();
            }
            this.sourceMappings.clear();
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            return true;
        }
        return false;
    }
    
    public void linkToIndexSource(BlockPos sourcePos) {
        if (this.level == null || this.level.isClientSide || sourceMappings.containsKey(sourcePos) || !this.isLinked()) return;
        ISignalType signalType = getSignalType();
        if (signalType != null && this.appearance != null) {
            AspectMapping defaultMapping = signalType.createDefaultMapping(this.appearance);
            sourceMappings.put(sourcePos, defaultMapping);
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    public void resetLink() {
        if (level != null && !level.isClientSide && isLinked() && level.isLoaded(linkedSignalPos)) {
            if (level.getBlockEntity(linkedSignalPos) instanceof SignalMastBlockEntity mast) {
                mast.releaseSignalHead(this.linkedHeadId);
            }
        }
        this.linkedSignalPos = null;
        this.linkedHeadId = null;
        this.appearance = null;
        this.sourceMappings.clear();
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void unlinkIndexSource(BlockPos sourcePos) {
        if (this.level != null && this.level.isClientSide) return;
        if (sourceMappings.remove(sourcePos) != null) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    public boolean isLinked() { return this.linkedSignalPos != null && this.linkedHeadId != null; }
    public boolean isLinkedTo(BlockPos mastPos, UUID headId) {
        return this.linkedSignalPos != null && this.linkedSignalPos.equals(mastPos) &&
               this.linkedHeadId != null && this.linkedHeadId.equals(headId);
    }
    @Nullable public ISignalAppearance getAppearance() { return this.appearance; }
    public Map<BlockPos, AspectMapping> getSourceMappings() { return this.sourceMappings; }

    /**
     * GUIから送られてきたAppearanceデータで、自身の状態を完全に上書きする
     */
    public void setAppearance(ISignalAppearance appearance) {
        this.appearance = appearance;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }

        if (this.level != null && !this.level.isClientSide && this.isLinked()) {
            if (this.level.isLoaded(this.linkedSignalPos)) {
                BlockEntity blockEntity = this.level.getBlockEntity(this.linkedSignalPos);
                if (blockEntity instanceof SignalMastBlockEntity mast) {
                    // Mastに「このHeadの見た目を更新して」とお願いする
                    mast.updateSignalHeadAppearance(this.linkedHeadId, this.appearance);
                }
            }
        }
    }

    /**
     * GUIから送られてきたMappingデータで、自身の状態を完全に上書きする
     */
    public void setSourceMappings(Map<BlockPos, AspectMapping> newMappings) {
        this.sourceMappings.clear();
        this.sourceMappings.putAll(newMappings);
        this.setChanged(); // 変更をマーク
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Nullable
    public ISignalType getSignalType() { 
        if (this.level != null && this.isLinked() && this.level.isLoaded(this.linkedSignalPos)) {
            if (this.level.getBlockEntity(this.linkedSignalPos) instanceof SignalMastBlockEntity mast) {
                return mast.getSignalType();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pId, Inventory pInv, Player pPlayer) {
        // GUIを開く直前にMastからデータを同期する処理は残す
        this.syncAppearanceFromMast();
        
        // Menuを普通にnewして返すだけの、シンプルな形に戻す
        return new ControlBoxMenu(pId, pInv, this);
    }

    // 同期用のヘルパーメソッドを新設
    public void syncAppearanceFromMast() {
        // 1. サーバー側で、かつMastにリンクされているかチェック
        if (!this.level.isClientSide() && this.isLinked()) {
            // 2. リンク先のMastとHeadを取得
            if (level.isLoaded(linkedSignalPos) && level.getBlockEntity(linkedSignalPos) instanceof SignalMastBlockEntity mast) {
                SignalHead head = mast.getSignalHead(this.linkedHeadId);
                if (head != null && head.getAppearance() != null) {
                    // 3. MastのHeadが持つAppearanceを自分自身にコピーする
                    this.appearance = head.getAppearance().copy();
                    this.setChanged(); // 念のため変更をマーク
                }
            }
        }
    }
    
    @Override public Component getDisplayName() { return Component.literal("信号制御盤"); }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        if (appearance != null) {
            tag.putString("AppearanceType", appearance.getTypeId());
            CompoundTag appearanceTag = new CompoundTag();
            appearance.writeNbt(appearanceTag);
            tag.put("AppearanceData", appearanceTag);
        }
        if (linkedSignalPos != null) {
            tag.put("linkedSignalPos", NbtUtils.writeBlockPos(linkedSignalPos));
            tag.putUUID("LinkedHeadId", linkedHeadId);
        }
        ListTag mappingList = new ListTag();
        sourceMappings.forEach((pos, mapping) -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.put("SourcePos", NbtUtils.writeBlockPos(pos));
            CompoundTag mappingTag = new CompoundTag();
            mapping.writeNbt(mappingTag);
            entryTag.put("Mapping", mappingTag);
            mappingList.add(entryTag);
        });
        tag.put("SourceMappings", mappingList);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("AppearanceType")) {
            String typeId = tag.getString("AppearanceType");
            this.appearance = AllSignalTypes.createAppearanceFromId(typeId, tag.getCompound("AppearanceData"));
        } else { this.appearance = null; }

        if (tag.contains("linkedSignalPos")) {
            this.linkedSignalPos = NbtUtils.readBlockPos(tag.getCompound("linkedSignalPos"));
            this.linkedHeadId = tag.getUUID("LinkedHeadId");
        } else {
            this.linkedSignalPos = null;
            this.linkedHeadId = null;
        }

        //移行
        if (tag.contains("linkedMastPos")) {
            this.linkedSignalPos = NbtUtils.readBlockPos(tag.getCompound("linkedMastPos"));
            this.linkedHeadId = tag.getUUID("LinkedHeadId");
        }

        this.sourceMappings.clear();
        ListTag mappingList = tag.getList("SourceMappings", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < mappingList.size(); i++) {
            CompoundTag entryTag = mappingList.getCompound(i);
            BlockPos sourcePos = NbtUtils.readBlockPos(entryTag.getCompound("SourcePos"));
            AspectMapping mapping = AspectMapping.fromNbt(entryTag.getCompound("Mapping"));
            this.sourceMappings.put(sourcePos, mapping);
        }
    }
    
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag() { return this.saveWithoutMetadata(); }
    public UUID getLinkedHeadId() { return linkedHeadId; }
    public BlockPos getLinkedSignalPos() { return linkedSignalPos; }
}