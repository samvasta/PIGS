package com.samvasta.imageGenerator.common.graphics.colors;

public enum ColorIlluminant
{
    /**
     * same as {@link #D65} (Daylight, sRGB, Adobe-RGB)
     */
    DEFAULT (95.047, 100.000, 108.883),
    /**
     * Incandescent/tungsten
     */
    A	(109.850, 100.000, 35.585),
    /**
     * Old direct sunlight at noon
     */
    B	(99.0927, 100.000, 85.313),
    /**
     * Old daylight
     */
    C	(98.074, 100.000, 118.232),
    /**
     * ICC profile PCS
     */
    D50	(96.422, 100.000, 82.521),
    /**
     * Mid-morning daylight
     */
    D55	(95.682, 100.000, 92.149),
    /**
     * Daylight, sRGB, Adobe-RGB
     */
    D65	(95.047, 100.000, 108.883),
    /**
     * North sky daylight
     */
    D75	(94.972, 100.000, 122.638),
    /**
     * Equal energy
     */
    E	(100.000, 100.000, 100.000),
    /**
     * Daylight Fluorescent
     */
    F1	(92.834, 100.000, 103.665),
    /**
     * Cool fluorescent
     */
    F2	(99.187, 100.000, 67.395),
    /**
     * White Fluorescent
     */
    F3	(103.754, 100.000, 49.861),
    /**
     * Warm White Fluorescent
     */
    F4	(109.147, 100.000, 38.813),
    /**
     * Daylight Fluorescent
     */
    F5	(90.872, 100.000, 98.723),
    /**
     * Lite White Fluorescent
     */
    F6	(97.309, 100.000, 60.191),
    /**
     * Daylight fluorescent, D65 simulator
     */
    F7	(95.044, 100.000, 108.755),
    /**
     * Sylvania F40, D50 simulator
     */
    F8	(96.413, 100.000, 82.333),
    /**
     * Cool White Fluorescent
     */
    F9	(100.365, 100.000, 67.868),
    /**
     * Ultralume 50, Philips TL85
     */
    F10	(96.174, 100.000, 81.712),
    /**
     * Ultralume 40, Philips TL84
     */
    F11	(100.966, 100.000, 64.370),
    /**
     * Ultralume 30, Philips TL83
     */
    F12	(108.046, 100.000, 39.228),
    ;

    public final double X;
    public final double Y;
    public final double Z;

    ColorIlluminant(double x, double y, double z){
        X = x;
        Y = y;
        Z = z;
    }
}
