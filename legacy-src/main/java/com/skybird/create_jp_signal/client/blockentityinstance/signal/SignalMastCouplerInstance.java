package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SignalMastCouplerInstance {

    private final MaterialManager materialManager;
    private final List<ModelData> models = new ArrayList<>();

    public SignalMastCouplerInstance(MaterialManager materialManager) {
        this.materialManager = materialManager;
    }

    public void init(Vec3 position, PoseStack ms, Vec3 offset, Pair<Double, Double> rotation) {
        delete();
        TransformStack msr = TransformStack.cast(ms);

        ModelData mastCoupler = materialManager.defaultCutout()
            .material(Materials.TRANSFORMED)
            .getModel(PartialModelRegistry.MAST_COUPLER)
            .createInstance();
        models.add(mastCoupler);

        ModelData mastPipe = materialManager.defaultCutout()
            .material(Materials.TRANSFORMED)
            .getModel(PartialModelRegistry.MAST_PIPE)
            .createInstance();
        models.add(mastPipe);

        ModelData signalJoint = materialManager.defaultCutout()
            .material(Materials.TRANSFORMED)
            .getModel(PartialModelRegistry.SIGNAL_JOINT)
            .createInstance();
        models.add(signalJoint);
        
        {
            ms.pushPose();
            double x = position.x;
            double y = position.y;
            double z = position.z;
            msr.translate(0.5, 0, 0.5).rotateY(rotation.getFirst());
            msr.translate(0, y, 0);
            {
                ms.pushPose();
                msr.rotateY(Math.toDegrees(Math.atan2(x, z))).translate(-0.5, -0.5, 0).scale(1, 1, (float)Math.sqrt(x * x + z * z) * 16);
                mastPipe.setTransform(ms);
                ms.popPose();
            }
            msr.unCentre();
            mastCoupler.setTransform(ms);
            msr.translate(x, 0, z);
            signalJoint.setTransform(ms);

            ms.popPose();
        }
    }

    public void updateLight(Level level, BlockPos pos) {
        models.forEach(model -> model.updateLight(level, pos));
    }

    public void delete() {
        models.forEach(ModelData::delete);
        models.clear();
    }
}
