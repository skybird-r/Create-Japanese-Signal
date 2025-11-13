package com.skybird.create_jp_signal.block.track;

import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.train.track.AllEdgePointTypes;
import com.skybird.create_jp_signal.create.train.track.SpeedLimitBoundary;
import com.skybird.create_jp_signal.menu.SpeedLimitMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SpeedLimitBlockEntity extends SmartBlockEntity implements MenuProvider {

    public TrackTargetingBehaviour<SpeedLimitBoundary> edgePoint;

    private boolean shouldRenderOverlay = false;

    private double speedLimit = 100.0;
    private double limitDistance = 0.0;

    public SpeedLimitBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.SPEED_LIMIT_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick(); // これが BlockEntityBehaviour (TrackTargetingBehaviour) のtickを呼び出す
        if (level == null || level.isClientSide()) {
            return;
        }
        SpeedLimitBoundary boundary = edgePoint.getEdgePoint();
        boolean newRenderState;

        if (boundary != null) {
            // JpSignals.LOGGER.info("BlockEntity tick read boundary:" + Double.toString(boundary.getSpeedLimit()) + " " + Double.toString(boundary.getLimitDistance()));
            newRenderState = boundary.isBoundTo(worldPosition);
            // JpSignals.LOGGER.info("boundary exists");
            if (boundary.getSpeedLimit() != speedLimit || boundary.getLimitDistance() != limitDistance) {
                boundary.setSpeedLimit(speedLimit);
                boundary.setLimitDistance(limitDistance);
            }
        } else {
            newRenderState = false;
            // JpSignals.LOGGER.info("boundary == null");

        }

        if (shouldRenderOverlay != newRenderState) {
            shouldRenderOverlay = newRenderState;
            notifyUpdate();
        }
    }

    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        edgePoint = new TrackTargetingBehaviour<>(this, AllEdgePointTypes.SPEED_LIMIT);
        behaviours.add(edgePoint);
    }

    public boolean shouldRenderOverlay() {
        return this.shouldRenderOverlay;
    }

    public double getSpeedLimit() { return this.speedLimit; }
    public double getLimitDistance() { return this.limitDistance; }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, edgePoint.getGlobalPosition()).inflate(2);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("RenderOverlay", shouldRenderOverlay);
        tag.putDouble("SpeedLimit", speedLimit);
        tag.putDouble("LimitDistance", limitDistance);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        shouldRenderOverlay = tag.getBoolean("RenderOverlay");
        speedLimit = tag.getDouble("SpeedLimit");
        limitDistance = tag.getDouble("LimitDistance");
        
        // 念のため
        if (!clientPacket && edgePoint != null) {
            SpeedLimitBoundary boundary = edgePoint.getEdgePoint();
            if (boundary != null) {
                 boundary.setSpeedLimit(speedLimit);
                 boundary.setLimitDistance(limitDistance);
            }
        }
    }


    // menu
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Speed Limit Configuration");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new SpeedLimitMenu(pContainerId, pInventory, this);
    }
    
    public void setSpeedLimitAndDistance(double speed, double distance) {
        if (this.edgePoint == null) return;
        
        this.speedLimit = speed;
        this.limitDistance = distance;

        SpeedLimitBoundary boundary = this.edgePoint.getEdgePoint();
        if (boundary != null) {
            boundary.setSpeedLimit(speed);
            boundary.setLimitDistance(distance);
        }
        
        setChanged();
        notifyUpdate();
    }
}
