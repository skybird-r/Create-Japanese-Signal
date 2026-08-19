package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SignalAccessoryInstance {

    private final List<SignalModelData> staticParts = new ArrayList<>();
    private final List<SignalModelData> lightParts = new ArrayList<>();
    private final SignalInstanceManager materialManager;
    private SignalHead signalHead;

    private final List<Vec3> mastCouplerPositions = new ArrayList<>();

    public SignalAccessoryInstance(SignalInstanceManager materialManager, SignalHead signalHead) {
        this.materialManager = materialManager;
        this.signalHead = signalHead;
    }

    // offset済み
    public void init(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        this.signalHead = signalHead;
        delete();

        {
            ms.pushPose();
            ms.translate(0.5, 0, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
            ms.translate(offset.x, offset.y, offset.z);
            SignalAccessory.Type type = null;
            if (signalHead != null) {
                type = signalHead.getAppearance().getAccessory().getType();
            }

            switch (type) {
                case FORECAST -> {
                    SignalModelData forecast = materialManager.create(PartialModelRegistry.ROUTE_FORECAST_CASING);
                    staticParts.add(forecast);
                    {
                        ms.pushPose();
                        ms.translate(0, -8.0/16, 0);
                        ms.translate(-0.5, -0.5, -0.5);
                        forecast.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -10.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_HOME -> {
                    SignalModelData indicator = materialManager.create(PartialModelRegistry.ROUTE_INDICATOR_HOME_CASING);
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        ms.translate(0, -16.0/16, 0);
                        ms.translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -18.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_DEPARTURE -> {
                    SignalModelData indicator = materialManager.create(PartialModelRegistry.ROUTE_INDICATOR_DEPARTURE_CASING);
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        ms.translate(0, -11.0/16, 0);
                        ms.translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -13.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_SHUNT -> {
                    SignalModelData indicator = materialManager.create(PartialModelRegistry.ROUTE_INDICATOR_SHUNT_CASING);
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        ms.translate(0, -8.0/16, 0);
                        ms.translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -10.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
            }
            initLights(ms, type);
            ms.popPose();
        }
        beginFrame();
    }

    private void initLights(PoseStack ms, SignalAccessory.Type type) {
        switch (type) {
            case FORECAST -> {
                ms.pushPose();
                ms.translate(-7.0 / 16, (2.25 - 8.0) / 16, 1.75 / 16);
                ms.scale(3.5f, 3.5f, 3.5f);
                for (int i = 0; i < 2; i++) {
                    addLight(ms);
                    ms.translate(14.0 / 16 / 3.5, 0, 0);
                }
                ms.popPose();
            }
            case INDICATOR_HOME -> {
                ms.pushPose();
                ms.translate(-5.0 / 16, 0.75 / 16 - 1, 1.75 / 16);
                ms.scale(2.5f, 2.5f, 2.5f);
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < 3; column++) {
                        addLight(ms);
                        ms.translate(5.0 / 16 / 2.5, 0, 0);
                    }
                    ms.translate(-15.0 / 16 / 2.5, 5.0 / 16 / 2.5, 0);
                }
                ms.popPose();
            }
            case INDICATOR_DEPARTURE -> {
                ms.pushPose();
                ms.translate(0, (0.75 - 11.0) / 16, 1.75 / 16);
                ms.scale(2.5f, 2.5f, 2.5f);
                addLight(ms);
                ms.translate(-5.0 / 16 / 2.5, 5.0 / 16 / 2.5, 0);
                for (int i = 0; i < 3; i++) {
                    addLight(ms);
                    ms.translate(5.0 / 16 / 2.5, 0, 0);
                }
                ms.popPose();
            }
            case INDICATOR_SHUNT -> {
                ms.pushPose();
                ms.translate(0, (0.5 - 8.0) / 16, 1.75 / 16);
                ms.pushPose();
                ms.translate(-3.75 / 16, 0, 0);
                ms.scale(1.5f, 3.0f, 1.0f);
                for (int i = 0; i < 3; i++) {
                    addLight(ms);
                    ms.translate(3.75 / 16 / 1.5, 0, 0);
                }
                ms.popPose();

                ms.pushPose();
                ms.translate(0, 3.5 / 16, 0);
                ms.scale(9.0f, 1.5f, 1.0f);
                addLight(ms);
                ms.popPose();
                ms.popPose();
            }
            case NONE -> {
            }
        }
    }

    private void addLight(PoseStack ms) {
        SignalModelData light = materialManager.createFullBright(PartialModelRegistry.SIGNAL_LIGHT);
        light.setTransform(ms);
        lightParts.add(light);
    }

    public void setSignalHead(SignalHead signalHead) {
        this.signalHead = signalHead;
    }

    public void beginFrame() {
        List<LampColor> colors = SignalAccessory.getLampColors(
            signalHead.getAppearance().getAccessory().getType(), signalHead.getCurrentRoute());
        for (int i = 0; i < lightParts.size(); i++) {
            lightParts.get(i).setColor(i < colors.size() ? colors.get(i) : LampColor.OFF);
        }
    }

    public void updateLight(Level level, BlockPos pos) {
        staticParts.forEach(model -> model.updateLight(level, pos));
    }

    public void delete() {
        staticParts.forEach(SignalModelData::delete);
        staticParts.clear();
        lightParts.forEach(SignalModelData::delete);
        lightParts.clear();
        mastCouplerPositions.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        staticParts.forEach(model -> consumer.accept(model.instance()));
        lightParts.forEach(model -> consumer.accept(model.instance()));
    }

    public List<Vec3> getMastCouplerPositions() {
        return mastCouplerPositions;
    }
}
