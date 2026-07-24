package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SignalMastCouplerInstance {

    private final SignalInstanceManager materialManager;
    private final List<SignalModelData> models = new ArrayList<>();

    public SignalMastCouplerInstance(SignalInstanceManager materialManager) {
        this.materialManager = materialManager;
    }

    public void init(Vec3 position, PoseStack ms, Vec3 offset, Pair<Double, Double> rotation) {
        delete();
        SignalModelData mastCoupler = materialManager.create(PartialModelRegistry.MAST_COUPLER);
        models.add(mastCoupler);

        SignalModelData mastPipe = materialManager.create(PartialModelRegistry.MAST_PIPE);
        models.add(mastPipe);

        SignalModelData signalJoint = materialManager.create(PartialModelRegistry.SIGNAL_JOINT);
        models.add(signalJoint);
        
        {
            ms.pushPose();
            double x = position.x;
            double y = position.y;
            double z = position.z;
            ms.translate(0.5, 0, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
            ms.translate(0, y, 0);
            {
                ms.pushPose();
                ms.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(x, z))));
                ms.translate(-0.5, -0.5, 0);
                ms.scale(1, 1, (float)Math.sqrt(x * x + z * z) * 16);
                mastPipe.setTransform(ms);
                ms.popPose();
            }
            ms.translate(-0.5, -0.5, -0.5);
            mastCoupler.setTransform(ms);
            ms.translate(x, 0, z);
            signalJoint.setTransform(ms);

            ms.popPose();
        }
    }

    public void updateLight(Level level, BlockPos pos) {
        models.forEach(model -> model.updateLight(level, pos));
    }

    public void delete() {
        models.forEach(SignalModelData::delete);
        models.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        models.forEach(model -> consumer.accept(model.instance()));
    }
}
