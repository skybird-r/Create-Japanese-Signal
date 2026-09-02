package com.skybird.create_jp_signal.block.track;

import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.create.train.track.AllEdgePointTypes;
import com.skybird.create_jp_signal.create.train.track.PenaltyBoundary;
import com.skybird.create_jp_signal.menu.PenaltyMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PenaltyBlockEntity extends SmartBlockEntity implements MenuProvider {

    public TrackTargetingBehaviour<PenaltyBoundary> edgePoint;

    private boolean shouldRenderOverlay;
    private int penalty = 100;

    public PenaltyBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.PENALTY_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide())
            return;

        PenaltyBoundary boundary = edgePoint.getEdgePoint();
        boolean newRenderState = boundary != null && boundary.isBoundTo(worldPosition);
        if (boundary != null && boundary.getPenalty() != penalty)
            boundary.setPenalty(penalty);

        if (shouldRenderOverlay != newRenderState) {
            shouldRenderOverlay = newRenderState;
            notifyUpdate();
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        edgePoint = new TrackTargetingBehaviour<>(this, AllEdgePointTypes.PENALTY);
        behaviours.add(edgePoint);
    }

    public boolean shouldRenderOverlay() {
        return shouldRenderOverlay;
    }

    public int getPenalty() {
        return penalty;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, edgePoint.getGlobalPosition()).inflate(2);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("RenderOverlay", shouldRenderOverlay);
        tag.putInt("Penalty", penalty);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        shouldRenderOverlay = tag.getBoolean("RenderOverlay");
        penalty = Mth.clamp(tag.getInt("Penalty"), 0, PenaltyBoundary.MAX_PENALTY);

        if (!clientPacket && edgePoint != null) {
            PenaltyBoundary boundary = edgePoint.getEdgePoint();
            if (boundary != null)
                boundary.setPenalty(penalty);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("create_jp_signal.gui.penalty.title");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PenaltyMenu(containerId, inventory, this);
    }

    public void setPenalty(int penalty) {
        this.penalty = Mth.clamp(penalty, 0, PenaltyBoundary.MAX_PENALTY);
        if (edgePoint != null) {
            PenaltyBoundary boundary = edgePoint.getEdgePoint();
            if (boundary != null)
                boundary.setPenalty(this.penalty);
        }
        setChanged();
        notifyUpdate();
    }
}
