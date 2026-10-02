package com.skybird.create_jp_signal.block.track;

import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.create.train.track.AllEdgePointTypes;
import com.skybird.create_jp_signal.create.train.track.ReservationLimitBoundary;
import com.skybird.create_jp_signal.menu.ReservationLimitMenu;

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

public class ReservationLimitBlockEntity extends SmartBlockEntity implements MenuProvider {

    public TrackTargetingBehaviour<ReservationLimitBoundary> edgePoint;

    private boolean shouldRenderOverlay;
    private int reservationLimit = ReservationLimitBoundary.DEFAULT_LIMIT;

    public ReservationLimitBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.RESERVATION_LIMIT_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide())
            return;

        ReservationLimitBoundary boundary = edgePoint.getEdgePoint();
        boolean newRenderState = boundary != null && boundary.isBoundTo(worldPosition);
        if (boundary != null && boundary.getReservationLimit() != reservationLimit)
            boundary.setReservationLimit(reservationLimit);

        if (shouldRenderOverlay != newRenderState) {
            shouldRenderOverlay = newRenderState;
            notifyUpdate();
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        edgePoint = new TrackTargetingBehaviour<>(this, AllEdgePointTypes.RESERVATION_LIMIT);
        behaviours.add(edgePoint);
    }

    public boolean shouldRenderOverlay() {
        return shouldRenderOverlay;
    }

    public int getReservationLimit() {
        return reservationLimit;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, edgePoint.getGlobalPosition()).inflate(2);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("RenderOverlay", shouldRenderOverlay);
        tag.putInt("ReservationLimit", reservationLimit);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        shouldRenderOverlay = tag.getBoolean("RenderOverlay");
        reservationLimit = tag.contains("ReservationLimit")
            ? Mth.clamp(tag.getInt("ReservationLimit"), 0, ReservationLimitBoundary.MAX_LIMIT)
            : ReservationLimitBoundary.DEFAULT_LIMIT;

        if (!clientPacket && edgePoint != null) {
            ReservationLimitBoundary boundary = edgePoint.getEdgePoint();
            if (boundary != null)
                boundary.setReservationLimit(reservationLimit);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("create_jp_signal.gui.reservation_limit.title");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ReservationLimitMenu(containerId, inventory, this);
    }

    public void setReservationLimit(int reservationLimit) {
        this.reservationLimit = Mth.clamp(reservationLimit, 0, ReservationLimitBoundary.MAX_LIMIT);
        if (edgePoint != null) {
            ReservationLimitBoundary boundary = edgePoint.getEdgePoint();
            if (boundary != null)
                boundary.setReservationLimit(this.reservationLimit);
        }
        setChanged();
        notifyUpdate();
    }
}
