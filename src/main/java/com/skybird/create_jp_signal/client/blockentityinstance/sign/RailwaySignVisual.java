package com.skybird.create_jp_signal.client.blockentityinstance.sign;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockEntity;
import com.skybird.create_jp_signal.block.sign.RailwaySignType;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.blockentityrenderer.sign.RailwaySignLayout;
import com.skybird.create_jp_signal.client.text.FlywheelText;
import com.skybird.create_jp_signal.client.text.SignText;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.client.renderer.LevelRenderer;

public class RailwaySignVisual extends AbstractBlockEntityVisual<RailwaySignBlockEntity> implements SimpleDynamicVisual {
    private static final RendererReloadCache<PartialModel, Model> MODELS = new RendererReloadCache<>(partial ->
        BakedModelBuilder.create(partial.get()).materialFunc((type, shaded) -> Materials.CUTOUT_BLOCK).build());
    private final List<TransformedInstance> instances = new ArrayList<>();

    public RailwaySignVisual(VisualizationContext context, RailwaySignBlockEntity marker, float partialTick) {
        super(context, marker, partialTick);
        rebuild();
    }

    private TransformedInstance create(Model model, PoseStack pose) {
        var instance = instancerProvider().instancer(InstanceTypes.TRANSFORMED, model).createInstance();
        instance.setTransform(pose).setChanged();
        instances.add(instance);
        return instance;
    }

    private void rebuild() {
        _delete();
        PoseStack pose = new PoseStack();
        var pos = getVisualPosition();
        pose.translate(pos.getX(), pos.getY(), pos.getZ());
        var settings = blockEntity.settings();
        RailwaySignLayout.placement(pose, settings);
        pose.pushPose(); RailwaySignLayout.pole(pose, settings, blockEntity.signType());
        create(MODELS.get(RailwaySignLayout.poleModel(blockEntity.signType())), pose); pose.popPose();
        pose.pushPose(); RailwaySignLayout.board(pose, settings, blockEntity.signType());
        create(MODELS.get(RailwaySignLayout.model(blockEntity.signType(), settings.variant())), pose); pose.popPose();
        if (blockEntity.signType() == RailwaySignType.SPEED_LIMIT || blockEntity.signType().numberedMast()) {
            RailwaySignLayout.numberFace(pose, settings, blockEntity.signType());
            java.util.function.ToIntFunction<String> advance =
                text -> FlywheelText.model(text, SignText.FontMode.MINECRAFT_ENGLISH).advance();
            var digits = blockEntity.signType().numberedMast()
                ? RailwaySignLayout.numberPlateDigits(settings.number(), settings.useLetterO(), advance)
                : RailwaySignLayout.digits(settings.speed(), settings.useLetterO(), advance);
            int color = blockEntity.signType().numberedMast() ? 255 : 0;
            for (var digit : digits) {
                pose.pushPose(); RailwaySignLayout.digit(pose, digit);
                create(FlywheelText.model(digit.text(), SignText.FontMode.MINECRAFT_ENGLISH).model(), pose)
                    .color(color, color, color).setChanged();
                pose.popPose();
            }
        }
        updateLight(0);
        blockEntity.clientVisualChanged = false;
    }

    @Override public void update(float partialTick) { rebuild(); }
    @Override public void beginFrame(DynamicVisual.Context context) {
        if (blockEntity.clientVisualChanged) rebuild();
    }
    @Override public void updateLight(float partialTick) {
        int light = LevelRenderer.getLightColor(level, blockEntity.getBlockPos());
        instances.forEach(instance -> instance.light(light).setChanged());
    }
    @Override protected void _delete() {
        instances.forEach(Instance::delete);
        instances.clear();
    }
    @Override public void collectCrumblingInstances(Consumer<Instance> consumer) { instances.forEach(consumer); }
}
