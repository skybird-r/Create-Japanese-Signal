package com.skybird.create_jp_signal.block.sign;

/** Registry names follow the supplied model names. Dimensions are in model pixels. */
public enum RailwaySignType {
    SPEED_LIMIT("speed_limit_sign", 12, 9),
    SPEED_LIMIT_END("speed_limit_end_sign", 12, 9),
    TRAIN_STOP_LIMIT("train_stop_limit_sign", 5, 7),
    SHUNT_LIMIT("shunt_limit_sign", 5, 7),
    TRACK_END("track_end_sign", 6, 6),
    BLOCK_SIGNAL_NO_PLATE("block_signal_no_plate", 5, 5);

    private final String id;
    private final int width;
    private final int height;
    RailwaySignType(String id, int width, int height) { this.id = id; this.width = width; this.height = height; }
    public String id() { return id; }
    public int width() { return width; }
    public int height() { return height; }
    public boolean numberedMast() { return this == BLOCK_SIGNAL_NO_PLATE; }
    public double boardBottom(RailwaySignSettings settings) {
        return numberedMast() ? settings.heightStep() * .25 : settings.bottomHeight();
    }
}
