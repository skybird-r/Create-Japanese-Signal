package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.BasicData;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance.BackplateType;
import com.skybird.create_jp_signal.client.CustomRenderTypes;
import com.skybird.create_jp_signal.client.ModelRegistry;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ColorLightSignalInstance extends SignalHeadInstance {

    private final List<ModelData> lamps = new ArrayList<>();
    private final List<ModelData> staticParts = new ArrayList<>();

    private Vec3 currentOffset = Vec3.ZERO;
    private Pair<Double, Double> currentRotation = Pair.of(0.0, 0.0);

    public ColorLightSignalInstance(MaterialManager materialManager, SignalHead headData) {
        super(materialManager, headData);
    }

    @Override
    public void init(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        // JpSignals.LOGGER.info("initializing color signal instance");
        super.init(signalHead, ms, pos, offset, rotation);
        if (!(this.signalHead.getAppearance() instanceof ColorLightSignalAppearance appearance)) 
            return;
        if (!(signalHead.getAppearance() instanceof ColorLightSignalAppearance newAppearance))
            return;
        if (appearance.hasSameStaticParts(newAppearance) && this.currentOffset.equals(offset) && this.currentRotation.equals(rotation)) {
            this.signalHead = signalHead;
            return;
        }
        this.remove();

        // JpSignals.LOGGER.info("processing");

        this.currentOffset = offset;
        this.currentRotation = rotation;
        this.signalHead = signalHead;

        TransformStack msr = TransformStack.cast(ms);
        {
            ms.pushPose();
            msr.translate(0.5, 0, 0.5).rotateY(rotation.getFirst()).translate(offset).rotateX(rotation.getSecond());

            PartialModel backplateBottomModel = switch (appearance.getBackplateType()) {
                case ROUND -> PartialModelRegistry.BACKPLATE_BOTTOM_ROUND;
                case SQUARE -> PartialModelRegistry.BACKPLATE_BOTTOM_SQUARE;
                case NONE -> null;
            };

            PartialModel lampBox = switch (appearance.getSignalSize()) {
                case NORMAL -> PartialModelRegistry.LAMPBOX_5x5;
                case TUNNEL -> PartialModelRegistry.LAMPBOX_4x4;
            };

            float lampHeight = switch (appearance.getSignalSize()) {
                case NORMAL -> 5;
                case TUNNEL -> 4;
            };

            int totalLampCount = this.signalHead.getCurrentAspect().getLampCount() + (appearance.isRepeater() ? 1 : 0);

            if (backplateBottomModel != null) {
                ModelData backplateBottom = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(backplateBottomModel)
                    .createInstance();
                staticParts.add(backplateBottom);
                allModels.add(backplateBottom);

                ModelData backplateTop = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(backplateBottomModel)
                    .createInstance();
                staticParts.add(backplateTop);
                allModels.add(backplateTop);

                ModelData backplateMiddle = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(PartialModelRegistry.BACKPLATE_MIDDLE)
                    .createInstance();
                staticParts.add(backplateMiddle);
                allModels.add(backplateMiddle);

                // 4*4は多分バグる
                {
                    ms.pushPose();
                    msr.unCentre();
                    backplateBottom.setTransform(ms);
                    msr.centre().translate(0, (5.0 * totalLampCount + 7.0)/16.0, 0).rotateZ(180).unCentre();
                    backplateTop.setTransform(ms);
                    ms.popPose();
                }
                {
                    ms.pushPose();
                    float yScale = 1.0f/5 + totalLampCount;
                    msr.unCentre().translate(0, 11.0/16 - 0.5 * yScale, 0).scale(1, yScale, 1);
                    backplateMiddle.setTransform(ms);
                    ms.popPose();
                }
            }

            // 2. ランプ
            {
                ms.pushPose();
                msr.translate(0, 3.5/16, 0);
                for (int i = 0; i < totalLampCount; i++) {
                    ModelData box = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(PartialModelRegistry.LAMPBOX_5x5)
                        .createInstance();
                    staticParts.add(box);
                    allModels.add(box);

                    // ModelData light = materialManager.defaultCutout()
                    //     .material(Materials.TRANSFORMED)
                    //     .getModel(PartialModelRegistry.SIGNAL_LIGHT)
                    //     .createInstance();
                    // lamps.add(light);
                    // allModels.add(light);

                    msr.unCentre();
                    box.setTransform(ms);
                    msr.centre();
                    // {
                    //     ms.pushPose();
                    //     msr.translate(0, 0.25/16, 1.75/16).scale((float)(lampHeight - 0.5));
                    //     light.setTransform(ms);
                    //     ms.popPose();
                    // }
                    msr.translate(0, 5.0/16, 0);
                }

                ms.popPose();
            }
            
            ms.popPose();
        }
        
    }

    @Override
    public void updateTransform(BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        // 親から渡される pos は getInstancePosition() (相対座標) である前提
        this.pos = pos;
        this.currentOffset = offset;
        this.currentRotation = rotation;
    }

    // 
    @Override
    public void beginFrame(BlockPos instancePos) {
        // SignalAspect.State aspect = this.signalHead.getCurrentAspect();
        // long gameTime = net.minecraft.client.Minecraft.getInstance().level.getGameTime();
        // float lampSpacing = 4.0f / 16.0f;
        // float yRot = currentRotation.getFirst().floatValue();

        // // SchematicannonInstanceのようにPoseStackを使うと計算が安全です
        // // PoseStack ms = new PoseStack();
        // // TransformStack msr = TransformStack.cast(ms);

        // for (int i = 0; i < lamps.size(); i++) {
        //     ModelData lamp = lamps.get(i);
        //     float yShift = i * lampSpacing;

        //     // ★ここ重要: PoseStackの原点を getInstancePosition (this.pos) に合わせる
        //     // ms.pushPose();
        //     // msr.translate(instancePos); 
            
        //     // // オフセットと回転の適用
        //     // msr.translate(currentOffset.x, currentOffset.y + yShift, currentOffset.z);
        //     // msr.rotateCentered(Direction.UP, (float)Math.toRadians(yRot));
        //     // msr.translate(0, 0, 0.1);

        //     // 色とライト設定
        //     SignalAspect.LampColor color = aspect.getLampColor(i, gameTime);

        //     // ms.scale(4.5f, 4.5f, 4.5f);
        //     lamp.setColor(color.getByteRed(), color.getByteGreen(), color.getByteBlue(), (byte)255)
        //         .setBlockLight(15).setSkyLight(15);

        //     // ★計算した行列を適用
        //     // lamp.setTransform(ms);
        //     // ms.popPose();
        // }
    }

    @Override
    public void updateLight(Level level, BlockPos pos) {
        this.pos = pos; // 念の為更新
        staticParts.forEach(model -> model.updateLight(level, pos));
    }

    @Override
    public void remove() {
        super.remove();
        staticParts.forEach(ModelData::delete);
        staticParts.clear();
        lamps.forEach(ModelData::delete);
        lamps.clear();
    }

    @Override
    public List<Vec3> getMastCouplerPosition() {
        // TODO Auto-generated method stub
        return super.getMastCouplerPosition();
    }
}