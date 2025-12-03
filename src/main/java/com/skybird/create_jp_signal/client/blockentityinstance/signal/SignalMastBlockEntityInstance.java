package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.SignalMastBlockEntity;

import net.minecraft.world.phys.Vec3;

public class SignalMastBlockEntityInstance extends BlockEntityInstance<SignalMastBlockEntity> {
    
    private SignalMastInstance mastInstance;

    public SignalMastBlockEntityInstance(MaterialManager materialManager, SignalMastBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        mastInstance = new SignalMastInstance(materialManager);
        update(); 
    }

    @Override
    public void update() {
        super.update();
        remove();

        int rotation = blockEntity.getRotation();
        int xPos = blockEntity.getXPos();
        int zPos = blockEntity.getZPos();
        float yRot = 180.0F - (rotation * 22.5F);
        
        Vec3 offset = new Vec3((double)xPos/16, 0, (double)zPos/16);

        PoseStack ms = new PoseStack();
        TransformStack msr = TransformStack.cast(ms);
        msr.translate(getInstancePosition());

        mastInstance.init(blockEntity, ms, getInstancePosition(), offset, yRot);

        updateLight();
    }

    @Override
    public void updateLight() {
        if (blockEntity.getLevel() != null && blockEntity.getBlockPos() != null)
            mastInstance.updateLight(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    @Override
    protected void remove() {
        mastInstance.delete();
    }
}
