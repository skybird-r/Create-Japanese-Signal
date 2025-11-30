package com.skybird.create_jp_signal.block.signal;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.menu.SignalLinkMenu;
import com.skybird.create_jp_signal.util.Lang;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class BaseSignalBlockEntity extends BlockEntity {

    public enum AttachmentSlot {
        PRIMARY("Primary"), SECONDARY("Secondary");
        
        final String displayName;

        AttachmentSlot (String name) {
            this.displayName = name;
        }
    }

    public boolean clientVisualChanged = true;

    protected SignalLayout layout = new SignalLayout();
    protected final Map<AttachmentSlot, SignalHead> signalHeads = new EnumMap<>(AttachmentSlot.class);
    protected final ISignalType signalType;

    public BaseSignalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ISignalType signalType) {
        super(type, pos, state);
        this.signalType = signalType;
    }

    @Override
    public AABB getRenderBoundingBox() {
        // 描画範囲拡張
        return new AABB(this.worldPosition).inflate(3.0);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (!(be instanceof BaseSignalBlockEntity baseBE)) return;
        if (level.isClientSide || baseBE.signalHeads.isEmpty() || level.getGameTime() % 100 != 0) return;

        boolean hasChanged = false;
        for (SignalHead head : baseBE.signalHeads.values()) {
            BlockPos controllerPos = head.getControllerPos();
            if (controllerPos == null) continue;

            if (level.isLoaded(controllerPos)) {
                if (level.getBlockEntity(controllerPos) instanceof ControlBoxBlockEntity controlBox) {
                    if (controlBox.isLinkedTo(baseBE.getBlockPos(), head.getUniqueId())) {
                        continue; // 正常
                    }
                }
                head.setControllerPos(null);
                hasChanged = true;
            }
        }
        if (hasChanged) {
            baseBE.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public boolean claimSignalHead(UUID headId, BlockPos controllerPos) {
        if (level == null || level.isClientSide) return false;
        for (SignalHead head : this.signalHeads.values()) {
            if (head.getUniqueId().equals(headId)) {
                if (head.getControllerPos() != null) return false; // 既に使用中
                head.setControllerPos(controllerPos);
                this.setChanged();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                return true;
            }
        }
        return false;
    }

    public void releaseSignalHead(UUID headId) {
        if (level == null || level.isClientSide) return;
        signalHeads.values().stream()
            .filter(head -> head.getUniqueId().equals(headId))
            .findFirst()
            .ifPresent(head -> {
                head.setControllerPos(null);
                this.setChanged();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            });
    }

    public void updateAspect(UUID headId, SignalAspect.State newAspect, SignalAccessory.Route newRoute) {
        // boolean colorChangeOnly = true;
        for (SignalHead head : signalHeads.values()) {
            if (head.getUniqueId().equals(headId)) {
                if (head.getCurrentAspect() != newAspect || head.getCurrentRoute() != newRoute) {
                    // if (head.getCurrentAspect().getLampCount() != newAspect.getLampCount())
                    //     colorChangeOnly = false;
                    head.setCurrentAspect(newAspect);
                    head.setCurrentRoute(newRoute);
                    this.setChanged();
                    if (level != null) {
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }
                return;
            }
        }
    }

    public void updateSignalHeadAppearance(UUID headId, ISignalAppearance newAppearance) {
        if (this.level == null || this.level.isClientSide) return;
        this.signalHeads.values().stream()
            .filter(head -> head.getUniqueId().equals(headId))
            .findFirst()
            .ifPresent(head -> {
                head.setAppearance(newAppearance.copy());
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            });
    }

    public boolean isHeadControlledBy(UUID headId, BlockPos controllerPos) {
        for (SignalHead head : this.signalHeads.values()) {
            if (head.getUniqueId().equals(headId)) {
                return controllerPos.equals(head.getControllerPos());
            }
        }
        return false;
    }

    @Nullable
    public SignalHead getSignalHead(UUID headId) {
        for (SignalHead head : this.signalHeads.values()) {
            if (head.getUniqueId().equals(headId)) {
                return head;
            }
        }
        return null;
    }

    public MenuProvider getLinkMenuProvider(BlockPos controlBoxPos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Lang.translatable("block_entity.base_signal.menu");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return new SignalLinkMenu(pContainerId, pInventory, BaseSignalBlockEntity.this, controlBoxPos);
            }
        };
    }

    public Map<AttachmentSlot, SignalHead> getSignalHeads() { return this.signalHeads; }

    public ISignalType getSignalType() { return this.signalType; }

    
    public abstract void cycleLayout();
    public abstract Vec3 getHeadOffset(AttachmentSlot slot);
    public abstract Pair<Double, Double> getHeadRotation(AttachmentSlot slot);
    public SignalLayout getLayout() { return this.layout; }
 

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag headList = new ListTag();
        signalHeads.forEach((slot, head) -> {
            CompoundTag headTag = new CompoundTag();
            headTag.putString("Slot", slot.name());
            head.writeNbt(headTag);
            headList.add(headTag);
        });
        tag.put("SignalHeads", headList);

        CompoundTag layoutTag = new CompoundTag();
        this.layout.writeNbt(layoutTag);
        tag.put("Layout", layoutTag);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.signalHeads.clear();
        ListTag headList = tag.getList("SignalHeads", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < headList.size(); i++) {
            CompoundTag headTag = headList.getCompound(i);
            try {
                AttachmentSlot slot = AttachmentSlot.valueOf(headTag.getString("Slot"));
                SignalHead head = SignalHead.fromNbt(headTag);
                this.signalHeads.put(slot, head);
            } catch (IllegalArgumentException e) {}
        }

        if (tag.contains("Layout", CompoundTag.TAG_COMPOUND)) {
            this.layout = SignalLayout.fromNbt(tag.getCompound("Layout"));
        } else {
            this.layout = new SignalLayout();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (this.level != null && this.level.isClientSide) {
            this.clientVisualChanged = true;
        }
    }

}
