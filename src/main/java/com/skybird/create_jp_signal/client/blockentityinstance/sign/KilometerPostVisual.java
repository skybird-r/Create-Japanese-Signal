package com.skybird.create_jp_signal.client.blockentityinstance.sign;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.sign.KilometerPostBlockEntity;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.blockentityrenderer.sign.KilometerPostLayout;
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

public class KilometerPostVisual extends AbstractBlockEntityVisual<KilometerPostBlockEntity> implements SimpleDynamicVisual {
    private static final RendererReloadCache<PartialModel, Model> MODELS = new RendererReloadCache<>(partial ->
        BakedModelBuilder.create(partial.get()).materialFunc((type, shaded) -> Materials.CUTOUT_BLOCK).build());
    private final List<TransformedInstance> instances = new ArrayList<>();

    public KilometerPostVisual(VisualizationContext context, KilometerPostBlockEntity marker, float partialTick) {
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
        KilometerPostLayout.placement(pose, blockEntity);
        pose.pushPose();
        pose.translate(-0.5, 0, -0.5);
        create(MODELS.get(PartialModelRegistry.KILOMETER_POST), pose);
        pose.popPose();
        String text = Integer.toString(blockEntity.getDistance());
        if (blockEntity.useLetterO()) text = text.replace('0', 'O');
        for (int side = 0; side < 2; side++) {
            pose.pushPose();
            KilometerPostLayout.face(pose, side);
            for (int i = 0; i < text.length(); i++) {
                var glyph = FlywheelText.model(text.substring(i, i + 1), SignText.FontMode.MINECRAFT_ENGLISH);
                pose.pushPose();
                KilometerPostLayout.digit(pose, i, text.length(),
                    KilometerPostLayout.digitCenterX(glyph.advance()), glyph.centerY());
                create(glyph.model(), pose).color(0, 0, 0).setChanged();
                pose.popPose();
            }
            pose.popPose();
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
