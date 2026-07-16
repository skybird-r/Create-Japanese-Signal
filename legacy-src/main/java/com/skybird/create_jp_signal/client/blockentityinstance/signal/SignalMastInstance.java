package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class SignalMastInstance {
    private final MaterialManager materialManager;
    
    private BlockPos pos;
    private Vec3 offset;
    private double rotation;

    private ModelData mastModel = null;

    public SignalMastInstance(MaterialManager materialManager) {
        this.materialManager = materialManager;
    }

    // 初期化
    public void init(BlockEntity be, PoseStack ms, BlockPos pos, Vec3 offset, double rotation) {
        this.pos = pos;
        if (offset.equals(this.offset) && rotation == this.rotation && mastModel != null) 
            return;

        delete();

        {
            ms.pushPose();
            ms.translate(offset.x, offset.y, offset.z);
            ms.mulPose(Axis.YP.rotationDegrees((float)rotation));
            ms.translate(-0.5, 0, -0.5);

            mastModel = materialManager.defaultCutout()
                .material(Materials.TRANSFORMED)
                .getModel(PartialModelRegistry.SIGNAL_MAST)
                .createInstance();
            mastModel.setTransform(ms);
            ms.popPose();
        }
        this.offset = offset;
        this.rotation = rotation;
    }

    public void beginFrame(BlockPos instancePos) {
    }

    public void updateLight(Level level, BlockPos pos) {
        if (mastModel != null) {
            mastModel.updateLight(level, pos);
        }
    }

    public void delete() {
        if (mastModel != null) {
            mastModel.delete();
            mastModel = null;
        }
    }
}