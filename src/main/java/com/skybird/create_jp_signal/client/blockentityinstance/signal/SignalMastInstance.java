package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class SignalMastInstance {
    private final SignalInstanceManager materialManager;
    
    private BlockPos pos;
    private Vec3 offset;
    private double rotation;

    private SignalModelData mastModel = null;

    public SignalMastInstance(SignalInstanceManager materialManager) {
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

            mastModel = materialManager.create(PartialModelRegistry.SIGNAL_MAST);
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

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        if (mastModel != null) {
            consumer.accept(mastModel.instance());
        }
    }
}
