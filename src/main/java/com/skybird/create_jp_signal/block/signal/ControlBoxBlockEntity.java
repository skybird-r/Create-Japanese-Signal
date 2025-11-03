package com.skybird.create_jp_signal.block.signal;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.SignalAspect.State;
import com.skybird.create_jp_signal.block.signal.debug.DebugInputBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;
import com.skybird.create_jp_signal.menu.ControlBoxMenu;
import com.skybird.create_jp_signal.util.Lang;

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

public class ControlBoxBlockEntity extends BlockEntity implements MenuProvider {

    private ISignalAppearance appearance;
    private BlockPos linkedSignalPos;
    private UUID linkedHeadId;
    private final Map<BlockPos, AspectMapping> sourceMappings = new HashMap<>();

    public ControlBoxBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.CONTROL_BOX_ENTITY.get(), pPos, pState);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, ControlBoxBlockEntity be) {
        if (level.isClientSide || !be.isLinked()) return;
        if (!level.isLoaded(be.linkedSignalPos)) return;
        if (!(level.getBlockEntity(be.linkedSignalPos) instanceof BaseSignalBlockEntity signal) || !signal.isHeadControlledBy(be.linkedHeadId, pos)) {
            be.resetLink();
            return;
        }
        if (be.sourceMappings.isEmpty()) return;

        int highestAspectIndex = -1;
        SignalAspect.State finalAspect = null;
        SignalAccessory.Route finalRoute = SignalAccessory.Route.NONE;
        double finalReserverMaxSpeed = 0.0;
        boolean mappingsChanged = false;

        boolean forShunt = (be.appearance instanceof PositionLightShuntSignalAppearance);

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
            int currentIndex;
            OperationType currentOperationType = source.getReserverOperationType();
            if (forShunt == (currentOperationType == OperationType.SHUNT)) {
                currentIndex = source.getRedSignalIndex(entry.getValue().getMaxIndex()); // aspectの最大値を上限に開いている閉塞数を要求
            } else { //operationTypeとappearanceが一致しないとき
                currentIndex = 0;
            }
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
        if (finalReserverMaxSpeed <= 135.0 /* km/h */ / 72) {
            if (finalAspect == State.GG_5B) {
                finalAspect = State.G_5B;
            } else if (finalAspect == State.GG_6) {
                finalAspect = State.G_6;
            } else if (finalAspect == State.HIGH_SPEED_R2) {
                finalAspect = State.PROCEED_R2;
            }
        }

        if (finalAspect != null) signal.updateAspect(be.linkedHeadId, finalAspect, finalRoute);
        if (mappingsChanged) {
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public boolean linkToHead(BaseSignalBlockEntity signal, UUID headId) {
        if (level == null || level.isClientSide) return false;
        if (this.isLinked()) this.resetLink();
        // リンク先のHeadから現在のAppearanceを取得する
        SignalHead targetHead = signal.getSignalHeads().values().stream()
            .filter(h -> h.getUniqueId().equals(headId))
            .findFirst().orElse(null);
        if (targetHead == null) return false;
        boolean success = signal.claimSignalHead(headId, this.worldPosition);
        if (success) {
            this.linkedSignalPos = signal.getBlockPos();
            this.linkedHeadId = headId;
            //ISignalType signalType = signal.getSignalType();
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
            if (level.getBlockEntity(linkedSignalPos) instanceof BaseSignalBlockEntity signal) {
                signal.releaseSignalHead(this.linkedHeadId);
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
    public boolean isLinkedTo(BlockPos signalPos, UUID headId) {
        return this.linkedSignalPos != null && this.linkedSignalPos.equals(signalPos) &&
               this.linkedHeadId != null && this.linkedHeadId.equals(headId);
    }
    @Nullable public ISignalAppearance getAppearance() { return this.appearance; }
    public Map<BlockPos, AspectMapping> getSourceMappings() { return this.sourceMappings; }

    public void setAppearance(ISignalAppearance appearance) {
        this.appearance = appearance;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }

        if (this.level != null && !this.level.isClientSide && this.isLinked()) {
            if (this.level.isLoaded(this.linkedSignalPos)) {
                BlockEntity blockEntity = this.level.getBlockEntity(this.linkedSignalPos);
                if (blockEntity instanceof BaseSignalBlockEntity signal) {
                    // signalに「このHeadの見た目を更新して」とお願いする
                    signal.updateSignalHeadAppearance(this.linkedHeadId, this.appearance);
                }
            }
        }
    }

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
            if (this.level.getBlockEntity(this.linkedSignalPos) instanceof BaseSignalBlockEntity signal) {
                return signal.getSignalType();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pId, Inventory pInv, Player pPlayer) {
        // GUIを開く直前にsignalからデータを同期
        this.syncAppearanceFromSignal();
        
        return new ControlBoxMenu(pId, pInv, this);
    }

    public void syncAppearanceFromSignal() {
        if (!this.level.isClientSide() && this.isLinked()) {
            if (level.isLoaded(linkedSignalPos) && level.getBlockEntity(linkedSignalPos) instanceof BaseSignalBlockEntity signal) {
                SignalHead head = signal.getSignalHead(this.linkedHeadId);
                if (head != null && head.getAppearance() != null) {
                    this.appearance = head.getAppearance().copy();
                    this.setChanged();
                }
            }
        }
    }
    
    @Override public Component getDisplayName() { return Lang.translatable("block_entity.control_box.name"); }

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


