package com.skybird.create_jp_signal.block.signal;

import java.util.function.LongPredicate;

public class SignalAspect {

    public enum LampColor {
        OFF(0, 0, 0),
        RED(1.0f, 0.25f, 0.35f),
        YELLOW(1.0f, 0.75f, 0.0f),
        GREEN(0.0f, 1.0f, 0.7f),
        PURPLE(0.8f, 0.2f, 1.0f),
        WHITE(1.0f, 1.0f, 0.8f);

        private final float r, g, b;
        LampColor(float r, float g, float b) { this.r = r; this.g = g; this.b = b; }
        public float getRed() { return r; }
        public float getGreen() { return g; }
        public float getBlue() { return b; }

        public byte getByteRed() { return (byte)(r * 255); }
        public byte getByteGreen() { return (byte)(g * 255); }
        public byte getByteBlue() { return (byte)(b * 255); }
    }

    public enum Aspect {

        // 共通
        ALL(-1, "common.all"),
        OFF(-1, "common.off"),

        // 色灯式
        R(0, "color.R"),
        YY(1, "color.YY"),
        Y(2, "color.Y"),
        YG(3, "color.YG"),
        YGF(4, "color.YGF"),
        G(5, "color.G"),
        GG(6, "color.GG"),

        // 灯列式入換信号
        S_STOP(0, "shunt.stop"),
        S_CAUTION(1, "shunt.caution"),
        S_PROCEED(2, "shunt.proceed"),

        // 灯列式中継信号
        R_STOP(0, "repeater.stop"),
        R_RESTRICTED(1, "repeater.restricted"),
        R_PROCEED(2, "repeater.proceed"),
        R_HIGH_SPEED_PROCEED(3, "repeater.high_speed_proceed");


        Aspect(int index, String key) {
            this.aspectIndex = index;
            this.translationKey = key;
        };

        private final int aspectIndex;
        private final String translationKey;

        public int getAspectIndex() {
            return aspectIndex;
        }

        public String getTranslationKey() {
            return "signal.signal_aspect.aspect." + translationKey;
        }

    } 

    public enum State {

        // 色灯式 lamp下から
        
        // 2灯式YR
        ALL_2YR(Aspect.ALL, gameTime -> true, LampColor.RED, LampColor.YELLOW),
        OFF_2YR(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF),
        Y_2YR(Aspect.Y, gameTime -> true, LampColor.OFF, LampColor.YELLOW),
        R_2YR(Aspect.R, gameTime -> true, LampColor.RED, LampColor.OFF),

        // 2灯式GR
        ALL_2GR(Aspect.ALL, gameTime -> true, LampColor.RED, LampColor.GREEN),
        OFF_2GR(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF),
        G_2GR(Aspect.G, gameTime -> true, LampColor.OFF, LampColor.GREEN),
        R_2GR(Aspect.R, gameTime -> true, LampColor.RED, LampColor.OFF),

        // 2灯式GY
        ALL_2GY(Aspect.ALL, gameTime -> true, LampColor.YELLOW, LampColor.GREEN),
        OFF_2GY(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF),
        G_2GY(Aspect.G, gameTime -> true, LampColor.OFF, LampColor.GREEN),
        Y_2GY(Aspect.Y, gameTime -> true, LampColor.YELLOW, LampColor.OFF),

        // 3灯式
        ALL_3(Aspect.ALL, gameTime -> true, LampColor.RED, LampColor.YELLOW, LampColor.GREEN),
        OFF_3(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        G_3(Aspect.G, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.GREEN),
        Y_3(Aspect.Y, gameTime -> true, LampColor.OFF, LampColor.YELLOW, LampColor.OFF),
        R_3(Aspect.R, gameTime -> true, LampColor.RED, LampColor.OFF, LampColor.OFF),

        // 4灯式A
        ALL_4A(Aspect.ALL, gameTime -> true, LampColor.YELLOW, LampColor.GREEN, LampColor.RED, LampColor.YELLOW),
        OFF_4A(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        G_4A(Aspect.G, gameTime -> true, LampColor.OFF, LampColor.GREEN, LampColor.OFF, LampColor.OFF),
        Y_4A(Aspect.Y, gameTime -> true, LampColor.YELLOW, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        YY_4A(Aspect.YY, gameTime -> true, LampColor.YELLOW, LampColor.OFF, LampColor.OFF, LampColor.YELLOW),
        R_4A(Aspect.R, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.RED, LampColor.OFF),

        // 4灯式B
        ALL_4B(Aspect.ALL, gameTime -> true, LampColor.GREEN, LampColor.YELLOW, LampColor.RED, LampColor.YELLOW),
        OFF_4B(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        G_4B(Aspect.G, gameTime -> true, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        YGF_4B(Aspect.YGF, gameTime -> gameTime % 15 < 8, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.YELLOW),
        YG_4B(Aspect.YG, gameTime -> true, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.YELLOW),
        Y_4B(Aspect.Y, gameTime -> true, LampColor.OFF, LampColor.YELLOW, LampColor.OFF, LampColor.OFF),
        R_4B(Aspect.R, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.RED, LampColor.OFF),

        // 5灯式A
        ALL_5A(Aspect.ALL, gameTime -> true, LampColor.GREEN, LampColor.YELLOW, LampColor.RED, LampColor.YELLOW, LampColor.YELLOW),
        OFF_5A(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        G_5A(Aspect.G, gameTime -> true, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        YGF_5A(Aspect.YGF, gameTime -> gameTime % 15 < 8, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.YELLOW, LampColor.OFF),
        YG_5A(Aspect.YG, gameTime -> true, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.YELLOW, LampColor.OFF),
        Y_5A(Aspect.Y, gameTime -> true, LampColor.OFF, LampColor.YELLOW, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        YY_5A(Aspect.YY, gameTime -> true, LampColor.OFF, LampColor.YELLOW, LampColor.OFF, LampColor.OFF, LampColor.YELLOW),
        R_5A(Aspect.R, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.RED, LampColor.OFF, LampColor.OFF),

        // 5灯式B
        ALL_5B(Aspect.ALL, gameTime -> true, LampColor.GREEN, LampColor.RED, LampColor.YELLOW, LampColor.GREEN, LampColor.GREEN),
        OFF_5B(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        GG_5B(Aspect.GG, gameTime -> true, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.GREEN),
        G_5B(Aspect.G, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.GREEN, LampColor.OFF),
        Y_5B(Aspect.Y, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.YELLOW, LampColor.OFF, LampColor.OFF),
        R_5B(Aspect.R, gameTime -> true, LampColor.OFF, LampColor.RED, LampColor.OFF, LampColor.OFF, LampColor.OFF),

        // 6灯式
        ALL_6(Aspect.ALL, gameTime -> true, LampColor.GREEN, LampColor.YELLOW, LampColor.GREEN, LampColor.RED, LampColor.GREEN, LampColor.YELLOW),
        OFF_6(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        GG_6(Aspect.GG, gameTime -> true, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.GREEN, LampColor.OFF),
        G_6(Aspect.G, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        YGF_6(Aspect.YGF, gameTime -> gameTime % 15 < 8, LampColor.OFF, LampColor.OFF, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.YELLOW),
        YG_6(Aspect.YG, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.GREEN, LampColor.OFF, LampColor.OFF, LampColor.YELLOW),
        Y_6(Aspect.Y, gameTime -> true, LampColor.OFF, LampColor.YELLOW, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        R_6(Aspect.R, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.RED, LampColor.OFF, LampColor.OFF),


        // 灯列式入換信号

        // 通常　白
        PROCEED_S2(Aspect.S_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.WHITE),
        STOP_S2(Aspect.S_STOP, gameTime -> true, LampColor.WHITE, LampColor.WHITE, LampColor.OFF),

        // 通常 赤
        PROCEED_S2R(Aspect.S_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.WHITE),
        STOP_S2R(Aspect.S_STOP, gameTime -> true, LampColor.WHITE, LampColor.RED, LampColor.OFF),

        // 3位式 白
        PROCEED_S3(Aspect.S_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.WHITE),
        CAUTION_S3(Aspect.S_CAUTION, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.WHITE, LampColor.OFF),
        STOP_S3(Aspect.S_STOP, gameTime -> true, LampColor.WHITE, LampColor.WHITE, LampColor.OFF, LampColor.OFF),

        // 通常 赤
        PROCEED_S3R(Aspect.S_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.WHITE),
        CAUTION_S3R(Aspect.S_CAUTION, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.WHITE, LampColor.OFF),
        STOP_S3R(Aspect.S_STOP, gameTime -> true, LampColor.WHITE, LampColor.RED, LampColor.OFF, LampColor.OFF),


        // 灯列式中継信号

        // 通常 横、斜め、縦
        ALL_R(Aspect.ALL, gameTime -> true, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE),
        OFF_R(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        PROCEED_R(Aspect.R_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.WHITE, LampColor.WHITE),
        RESTRICTED_R(Aspect.R_RESTRICTED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.WHITE, LampColor.WHITE, LampColor.OFF, LampColor.OFF),
        STOP_R(Aspect.R_STOP, gameTime -> true, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),

        // 高速進行対応 横、斜め、縦、高速進行用縦
        ALL_R2(Aspect.ALL, gameTime -> true, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE),
        OFF_R2(Aspect.OFF, gameTime -> true, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        HIGH_SPEED_R2(Aspect.R_HIGH_SPEED_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE),
        PROCEED_R2(Aspect.R_PROCEED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.WHITE, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        RESTRICTED_R2(Aspect.R_RESTRICTED, gameTime -> true, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.WHITE, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF),
        STOP_R2(Aspect.R_STOP, gameTime -> true, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF, LampColor.OFF);
        

        private final Aspect aspect;
        private final LongPredicate blinkingLogic;
        private final LampColor[] lampColors;

        State(Aspect aspect, LongPredicate logic, LampColor... colors) {
            this.aspect = aspect;
            this.blinkingLogic = logic;
            this.lampColors = colors;
        }

        public LampColor getLampColor(int index) {
            if (index < 0 || index >= this.lampColors.length) {
                return LampColor.OFF;
            }
            return this.lampColors[index];
        }

        public LampColor getLampColor(int index, long gameTime) {
            if (index < 0 || index >= this.lampColors.length || !isLit(gameTime)) {
                return LampColor.OFF;
            }
            return this.lampColors[index];
        }

        public boolean isLit(long gameTime) {
            return this.blinkingLogic.test(gameTime);
        }

        public int getLampCount() {
            return this.lampColors.length;
        }

        public Aspect getAspect() {
            return aspect;
        }

        public String getTranslationKey() {
            return this.aspect.getTranslationKey();
        }

        public int getAspectIndex() {
            return this.aspect.getAspectIndex();
        }

    }
}
