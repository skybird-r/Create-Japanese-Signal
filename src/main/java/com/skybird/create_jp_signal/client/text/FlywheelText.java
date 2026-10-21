package com.skybird.create_jp_signal.client.text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.skybird.create_jp_signal.mixin.TextRenderTypeAccessor;
import com.skybird.create_jp_signal.mixin.FontTextureAccessor;
import com.skybird.create_jp_signal.JpSignals;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.engine_room.flywheel.lib.material.SimpleMaterialShaders;
import dev.engine_room.flywheel.lib.material.StandardMaterialShaders;
import com.skybird.create_jp_signal.mixin.TextCompositeStateAccessor;
import com.skybird.create_jp_signal.mixin.TextTextureStateAccessor;
import dev.engine_room.flywheel.api.material.CardinalLightingMode;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.vertex.MutableVertexList;
import dev.engine_room.flywheel.lib.material.CutoutShaders;
import dev.engine_room.flywheel.lib.material.LightShaders;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.model.QuadMesh;
import dev.engine_room.flywheel.lib.model.SimpleModel;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Captures the normal Minecraft font's vertices, without submitting any BER draw calls.
 * Coordinates remain in font pixels (X right, Y down). Callers supply placement and scale.
 * Models are shared by text/font and invalidated with Flywheel's renderer reload cache.
 * Call prepare() on the render thread before a worker-thread visual uses model(): lazy
 * Minecraft glyph baking uploads textures and cannot run on Flywheel's worker threads.
 */
public final class FlywheelText {
    private record Key(String text, SignText.FontMode font) { }
    public record TextModel(Model model, float minX, float minY, float maxX, float maxY, int advance) {
        public float centerX() { return (minX + maxX) / 2; }
        public float centerY() { return (minY + maxY) / 2; }
    }
    private static final Model EMPTY_MODEL = new Model() {
        private final Vector4fc bounds = new Vector4f();
        @Override public List<ConfiguredMesh> meshes() { return List.of(); }
        @Override public Vector4fc boundingSphere() { return bounds; }
    };
    private static final RendererReloadCache<Key, TextModel> MODELS = new RendererReloadCache<>(FlywheelText::build);
    private FlywheelText() { }

    public static TextModel model(String text, SignText.FontMode font) { return MODELS.get(new Key(text, font)); }

    public static void prepare(String text, SignText.FontMode font) {
        RenderSystem.assertOnRenderThread();
        model(text, font);
    }

    private static TextModel build(Key key) {
        RenderSystem.assertOnRenderThread();
        Map<ResourceLocation, Capture> captures = new LinkedHashMap<>();
        var sequence = SignText.sequence(key.text, key.font);
        int advance = SignText.font().width(sequence);
        SignText.font().drawInBatch(sequence, 0, 0, 0xFFFFFFFF, false,
            new Matrix4f(), type -> captures.computeIfAbsent(texture(type), ignored -> new Capture()),
            Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        List<Model.ConfiguredMesh> meshes = new ArrayList<>();
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        for (var entry : captures.entrySet()) {
            var vertices = entry.getValue().vertices;
            if (vertices.isEmpty()) continue;
            for (Vertex vertex : vertices) {
                minX = Math.min(minX, vertex.x); minY = Math.min(minY, vertex.y);
                maxX = Math.max(maxX, vertex.x); maxY = Math.max(maxY, vertex.y);
            }
            var texture = net.minecraft.client.Minecraft.getInstance().getTextureManager().getTexture(entry.getKey());
            boolean intensity = texture instanceof net.minecraft.client.gui.font.FontTexture
                && !((FontTextureAccessor) texture).jpSignal$isColored();
            var shaders = intensity ? new SimpleMaterialShaders(StandardMaterialShaders.DEFAULT.vertexSource(),
                JpSignals.asResource("material/text_intensity.frag")) : StandardMaterialShaders.DEFAULT;
            var material = SimpleMaterial.builder().shaders(shaders).texture(entry.getKey()).mipmap(false)
                .cutout(CutoutShaders.ONE_TENTH).backfaceCulling(false).useOverlay(false)
                .light(LightShaders.FLAT).cardinalLightingMode(CardinalLightingMode.OFF).build();
            meshes.add(new Model.ConfiguredMesh(material, new TextMesh(List.copyOf(vertices))));
        }
        if (meshes.isEmpty()) return new TextModel(EMPTY_MODEL, 0, 0, 0, 0, advance);
        return new TextModel(new SimpleModel(meshes), minX, minY, maxX, maxY, advance);
    }

    private static ResourceLocation texture(RenderType type) {
        var state = ((TextRenderTypeAccessor) type).jpSignal$state();
        var texture = ((TextCompositeStateAccessor) (Object) state).jpSignal$textureState();
        return ((TextTextureStateAccessor) texture).jpSignal$cutoutTexture()
            .orElseThrow(() -> new IllegalArgumentException("Font render type has no texture: " + type));
    }

    private record Vertex(float x, float y, float z, float r, float g, float b, float a, float u, float v) { }

    private static final class Capture implements VertexConsumer {
        private final List<Vertex> vertices = new ArrayList<>();
        private float x, y, z, r = 1, g = 1, b = 1, a = 1, u, v;
        @Override public VertexConsumer vertex(double x, double y, double z) { this.x = (float)x; this.y = (float)y; this.z = (float)z; return this; }
        @Override public VertexConsumer color(int r, int g, int b, int a) { this.r = r / 255F; this.g = g / 255F; this.b = b / 255F; this.a = a / 255F; return this; }
        @Override public VertexConsumer uv(float u, float v) { this.u = u; this.v = v; return this; }
        @Override public VertexConsumer overlayCoords(int u, int v) { return this; }
        @Override public VertexConsumer uv2(int u, int v) { return this; }
        @Override public VertexConsumer normal(float x, float y, float z) { return this; }
        @Override public void endVertex() { vertices.add(new Vertex(x, y, z, r, g, b, a, u, v)); }
        @Override public void defaultColor(int r, int g, int b, int a) { color(r, g, b, a); }
        @Override public void unsetDefaultColor() { r = g = b = a = 1; }
    }

    private static final class TextMesh implements QuadMesh {
        private final List<Vertex> vertices;
        private final Vector4f sphere;
        TextMesh(List<Vertex> vertices) {
            this.vertices = vertices;
            float radius = 0;
            for (Vertex v : vertices) radius = Math.max(radius, (float)Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z));
            sphere = new Vector4f(0, 0, 0, radius);
        }
        @Override public int vertexCount() { return vertices.size(); }
        @Override public Vector4fc boundingSphere() { return sphere; }
        @Override public void write(MutableVertexList out) {
            for (int i = 0; i < vertices.size(); i++) {
                Vertex v = vertices.get(i);
                out.x(i, v.x); out.y(i, v.y); out.z(i, v.z);
                out.r(i, v.r); out.g(i, v.g); out.b(i, v.b); out.a(i, v.a);
                out.u(i, v.u); out.v(i, v.v);
                out.overlay(i, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
                out.light(i, LightTexture.FULL_BRIGHT);
                out.normalX(i, 0); out.normalY(i, 0); out.normalZ(i, -1);
            }
        }
    }
}
