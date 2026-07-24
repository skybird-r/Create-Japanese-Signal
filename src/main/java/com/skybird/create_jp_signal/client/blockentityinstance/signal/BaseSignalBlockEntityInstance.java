package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
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

public class BaseSignalBlockEntityInstance extends AbstractBlockEntityVisual<BaseSignalBlockEntity>
        implements SimpleDynamicVisual {

    private final Map<AttachmentSlot, SignalHeadInstance> headInstances = new EnumMap<>(AttachmentSlot.class);
    private final SignalInstanceManager materialManager;
    private final SignalMastInstance mastInstance;

    public BaseSignalBlockEntityInstance(VisualizationContext context, BaseSignalBlockEntity blockEntity,
            float partialTick) {
        super(context, blockEntity, partialTick);
        materialManager = new SignalInstanceManager(context.instancerProvider());
        mastInstance = new SignalMastInstance(materialManager);
        rebuild();
    }

    @Override
    public void update(float partialTick) {
        rebuild();
    }

    private void rebuild() {
        deleteModels();
        blockEntity.clientVisualChanged = false;

        PoseStack ms = new PoseStack();
        ms.translate(getVisualPosition().getX(), getVisualPosition().getY(), getVisualPosition().getZ());
        


        if (blockEntity instanceof BaseSignalMastBlockEntity mast) {
            int rotation = mast.getRotation();
            int xPos = mast.getXPos();
            int zPos = mast.getZPos();
            float yRot = 180.0F - (rotation * 22.5F);
            
            Vec3 offset = new Vec3((double)xPos/16, 0, (double)zPos/16);

            mastInstance.init(blockEntity, ms, getVisualPosition(), offset, yRot);

            ms.translate((double)xPos/16, 0.5, (double)zPos/16);
            ms.mulPose(Axis.YP.rotationDegrees(yRot));
            ms.translate(-0.5, -0.5, -0.5);

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
                existingInstance.init(newHeadData, ms, getVisualPosition(), offset, rotation);
            } else {
                SignalHeadInstance newInstance = createHeadInstance(newHeadData);
                if (newInstance != null) {
                    newInstance.init(newHeadData, ms, getVisualPosition(), offset, rotation);
                    headInstances.put(slot, newInstance);
                }
            }
            ms.popPose();
        }
        updateLight(0);
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
    public void beginFrame(DynamicVisual.Context context) {
        if (blockEntity.clientVisualChanged) {
            rebuild();
        }
        BlockPos currentPos = getVisualPosition();
        for (SignalHeadInstance head : headInstances.values()) {
            head.beginFrame(currentPos);
        }
    }

    @Override
    public void updateLight(float partialTick) {
        // レベルがnullでないかチェック
        if (blockEntity.getLevel() != null && blockEntity.getBlockPos() != null) {
            mastInstance.updateLight(blockEntity.getLevel(), blockEntity.getBlockPos());
            
            // ヘッドの明るさ更新
            for (SignalHeadInstance head : headInstances.values()) {
                head.updateLight(blockEntity.getLevel(), blockEntity.getBlockPos());
            }
        }
    }

    @Override
    protected void _delete() {
        deleteModels();
    }

    private void deleteModels() {
        mastInstance.delete();
        headInstances.values().forEach(SignalHeadInstance::remove);
        headInstances.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        mastInstance.collectCrumblingInstances(consumer);
        headInstances.values().forEach(head -> head.collectCrumblingInstances(consumer));
    }
}
