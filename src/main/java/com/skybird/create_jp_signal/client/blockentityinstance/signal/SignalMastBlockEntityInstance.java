package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.SignalMastBlockEntity;

import net.minecraft.world.phys.Vec3;

public class SignalMastBlockEntityInstance extends AbstractBlockEntityVisual<SignalMastBlockEntity> {
    
    private final SignalMastInstance mastInstance;

    public SignalMastBlockEntityInstance(VisualizationContext context, SignalMastBlockEntity blockEntity,
            float partialTick) {
        super(context, blockEntity, partialTick);
        mastInstance = new SignalMastInstance(new SignalInstanceManager(context.instancerProvider()));
        rebuild();
    }

    @Override
    public void update(float partialTick) {
        rebuild();
    }

    private void rebuild() {
        mastInstance.delete();

        int rotation = blockEntity.getRotation();
        int xPos = blockEntity.getXPos();
        int zPos = blockEntity.getZPos();
        float yRot = 180.0F - (rotation * 22.5F);
        
        Vec3 offset = new Vec3((double)xPos/16, 0, (double)zPos/16);

        PoseStack ms = new PoseStack();
        ms.translate(getVisualPosition().getX(), getVisualPosition().getY(), getVisualPosition().getZ());

        mastInstance.init(blockEntity, ms, getVisualPosition(), offset, yRot);

        updateLight(0);
    }

    @Override
    public void updateLight(float partialTick) {
        if (blockEntity.getLevel() != null && blockEntity.getBlockPos() != null)
            mastInstance.updateLight(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    @Override
    protected void _delete() {
        mastInstance.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        mastInstance.collectCrumblingInstances(consumer);
    }
}
