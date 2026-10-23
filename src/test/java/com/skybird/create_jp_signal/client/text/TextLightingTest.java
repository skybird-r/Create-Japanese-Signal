package com.skybird.create_jp_signal.client.text;

import java.lang.reflect.Proxy;
import java.util.List;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.vertex.MutableVertexList;
import dev.engine_room.flywheel.lib.material.Materials;
import net.minecraft.resources.ResourceLocation;

/** Tests material settings and the actual mesh writer without requiring a font atlas or GPU. */
public final class TextLightingTest {
    public static int verify() throws Exception {
        int checks = 0;
        for (boolean intensity : new boolean[] {false, true}) {
            var material = FlywheelText.createMaterial(new ResourceLocation("test", "glyph"), intensity);
            require(material.useLight() == Materials.CUTOUT_BLOCK.useLight(), "Block light map"); checks++;
            require(material.light().equals(Materials.CUTOUT_BLOCK.light()), "Block light shader"); checks++;
            require(material.cardinalLightingMode() == Materials.CUTOUT_BLOCK.cardinalLightingMode(), "Block face shading"); checks++;
        }
        Class<?> vertexType = Class.forName(FlywheelText.class.getName() + "$Vertex");
        var constructor = vertexType.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object vertex = constructor.newInstance(0F, 0F, 0F, 1F, 1F, 1F, 1F, 0F, 0F);
        Class<?> meshType = Class.forName(FlywheelText.class.getName() + "$TextMesh");
        var meshConstructor = meshType.getDeclaredConstructor(List.class);
        meshConstructor.setAccessible(true);
        var mesh = (dev.engine_room.flywheel.api.model.Mesh) meshConstructor.newInstance(List.of(vertex));
        int[] light = {-1};
        float[] normal = new float[3];
        var output = (MutableVertexList) Proxy.newProxyInstance(MutableVertexList.class.getClassLoader(),
            new Class<?>[] {MutableVertexList.class}, (proxy, method, args) -> {
                if (method.getName().equals("light")) light[0] = (Integer) args[1];
                if (method.getName().equals("normalX")) normal[0] = (Float) args[1];
                if (method.getName().equals("normalY")) normal[1] = (Float) args[1];
                if (method.getName().equals("normalZ")) normal[2] = (Float) args[1];
                return null;
            });
        mesh.write(output);
        require(light[0] == 0, "Glyph mesh must not override world light with full brightness"); checks++;
        require(normal[0] == 0 && normal[1] == 0 && normal[2] == 1, "Glyph faces same direction as name board"); checks++;
        return checks;
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
