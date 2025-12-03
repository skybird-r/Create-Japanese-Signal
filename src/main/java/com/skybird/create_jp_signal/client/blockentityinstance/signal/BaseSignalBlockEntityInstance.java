package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity.AttachmentSlot;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class BaseSignalBlockEntityInstance extends BlockEntityInstance<BaseSignalBlockEntity> implements DynamicInstance {

    private final Map<AttachmentSlot, SignalHeadInstance> headInstances = new EnumMap<>(AttachmentSlot.class);
    
    // 使ってない
    private final List<ModelData> mastModels = new ArrayList<>();

    private SignalMastInstance mastInstance;

    public BaseSignalBlockEntityInstance(MaterialManager materialManager, BaseSignalBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        mastInstance = new SignalMastInstance(materialManager);
        update(); 
    }

    @Override
    public void update() {
        remove();
        blockEntity.clientVisualChanged = false;

        PoseStack ms = new PoseStack();
        TransformStack msr = TransformStack.cast(ms);
        msr.translate(getInstancePosition());
        


        if (blockEntity instanceof BaseSignalMastBlockEntity mast) {
            int rotation = mast.getRotation();
            int xPos = mast.getXPos();
            int zPos = mast.getZPos();
            float yRot = 180.0F - (rotation * 22.5F);
            
            Vec3 offset = new Vec3((double)xPos/16, 0, (double)zPos/16);

            mastInstance.init(blockEntity, ms, getInstancePosition(), offset, yRot);

            msr.translate((double)xPos/16, 0.5, (double)zPos/16);
            msr.rotateY(yRot).unCentre();

        }
        

        Map<AttachmentSlot, SignalHead> currentHeads = blockEntity.getSignalHeads();

        headInstances.keySet().removeIf(slot -> {
            if (!currentHeads.containsKey(slot)) {
                headInstances.get(slot).remove();
                return true;
            }
            return false;
        });

        for (Map.Entry<AttachmentSlot, SignalHead> entry : currentHeads.entrySet()) {
            ms.pushPose();
            AttachmentSlot slot = entry.getKey();
            SignalHead newHeadData = entry.getValue();
            SignalHeadInstance existingInstance = headInstances.get(slot);

        

            Vec3 offset = blockEntity.getHeadOffset(slot);
            Pair<Double, Double> rotation = blockEntity.getHeadRotation(slot);
            
            if (existingInstance != null) {
                existingInstance.init(newHeadData, ms, getInstancePosition(), offset, rotation);
            } else {
                SignalHeadInstance newInstance = createHeadInstance(newHeadData);
                if (newInstance != null) {
                    newInstance.init(newHeadData, ms, getInstancePosition(), offset, rotation);
                    headInstances.put(slot, newInstance);
                }
            }
            ms.popPose();
        }
        updateLight();
    }
    
    @Nullable
    private SignalHeadInstance createHeadInstance(SignalHead head) {
        if (head.getAppearance() instanceof ColorLightSignalAppearance) {
            return new ColorLightSignalInstance(materialManager, head, blockEntity);
        } else if (head.getAppearance() instanceof PositionLightRepeaterSignalAppearance) {
            return new PositionLightRepeaterSignalInstance(materialManager, head, blockEntity);
        } else if (head.getAppearance() instanceof PositionLightShuntSignalAppearance) {
            return new PositionLightShuntSignalInstance(materialManager, head, blockEntity);
        }
        return null;
    }

    @Override
    public void beginFrame() {
        if (blockEntity.clientVisualChanged) {
            update();
        }
        BlockPos currentPos = getInstancePosition();
        for (SignalHeadInstance head : headInstances.values()) {
            head.beginFrame(currentPos);
        }
    }

    @Override
    public void updateLight() {
        // レベルがnullでないかチェック
        if (blockEntity.getLevel() != null && blockEntity.getBlockPos() != null) {
            // マストの明るさ更新
            relight(blockEntity.getBlockPos(), mastModels.stream());

            mastInstance.updateLight(blockEntity.getLevel(), blockEntity.getBlockPos());
            
            // ヘッドの明るさ更新
            for (SignalHeadInstance head : headInstances.values()) {
                head.updateLight(blockEntity.getLevel(), blockEntity.getBlockPos());
            }
        }
    }

    @Override
    public void remove() {
        // マストの削除
        mastModels.forEach(ModelData::delete);
        mastModels.clear();

        mastInstance.delete();
        // ヘッドの削除
        headInstances.values().forEach(SignalHeadInstance::remove);
        headInstances.clear();
    }
}
