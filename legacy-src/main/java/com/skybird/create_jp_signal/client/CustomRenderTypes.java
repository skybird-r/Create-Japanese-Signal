package com.skybird.create_jp_signal.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting; // ★これを使います
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.RenderType;
import com.skybird.create_jp_signal.JpSignals;

public class CustomRenderTypes extends RenderType {

    // コンストラクタ（継承用ダミー）
    public CustomRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    // ★カスタムRenderType
    public static final RenderType GLOWING_CUTOUT = new RenderType(
        JpSignals.MODID + ":glowing_cutout",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        256,
        true,
        false,
        // --- setupState (描画開始前) ---
        () -> {
            // 1. 基本的なCutoutの設定を適用
            RenderType.cutout().setupRenderState();
            
            // 2. ★照明ベクトルを全て「0」にして、陰影計算を無効化する
            // これにより dot(Normal, Light) が常に 0 になり、方向による影がつかなくなる
            // ただし setBlockLight(15) が効いているので、全体が明るく光る
            RenderSystem.setupLevelDiffuseLighting(
                new Vector3f(0f, 0f, 0f), // Light 0
                new Vector3f(0f, 0f, 0f), // Light 1
                new Matrix4f()            // Identity Matrix
            );
        },
        // --- clearState (描画終了後) ---
        () -> {
            // 3. 標準のCutoutの後始末
            RenderType.cutout().clearRenderState();
            
            // 4. ★重要：照明設定をワールド標準に戻す
            // これを忘れると、これ以降に描画される他のブロックが暗黒になります
            net.minecraft.client.Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        }
    ) {};
}