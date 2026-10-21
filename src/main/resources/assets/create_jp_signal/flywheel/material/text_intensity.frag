// Unifont/TrueType glyph atlases store coverage in RED rather than RGBA.
void flw_materialFragment() {
    flw_fragColor = vec4(flw_vertexColor.rgb, flw_vertexColor.a * flw_sampleColor.r);
}
