package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;

public class LinearLchPaletteBuilder implements IColorPaletteBuilder
{
    private static final double MAX_LUMINANCE_CHANGE = 5;
    private static final double MIN_LUMINANCE_CHANGE = -5;

    private static final double MAX_CHROMA_CHANGE = 5;
    private static final double MIN_CHROMA_CHANGE = -5;

    private static final double MAX_HUE_CHANGE = 36;
    private static final double MIN_HUE_CHANGE = -36;

    private static final int MAX_COLORS = 10;
    private static final int MIN_COLORS = 5;

    private MersenneTwister random;

    private int numColors;
    private double startLum;
    private double startChroma;
    private double startHue;

    private double deltaLum;
    private double deltaChroma;
    private double deltaHue;

    public LinearLchPaletteBuilder(MersenneTwister random){
        this.random = random;
        numColors = MIN_COLORS + random.nextInt(MAX_COLORS - MIN_COLORS);
        startLum = 50 + 50 * random.nextGaussian();
        startChroma = 50 + 50 * random.nextGaussian();
        startHue = random.nextInt(360);
        deltaLum = random.nextDouble() * (MAX_LUMINANCE_CHANGE - MIN_LUMINANCE_CHANGE) + MIN_LUMINANCE_CHANGE;
        deltaChroma = random.nextDouble() * (MAX_CHROMA_CHANGE - MIN_CHROMA_CHANGE) + MIN_CHROMA_CHANGE;
        deltaHue = random.nextDouble() * (MAX_HUE_CHANGE - MIN_HUE_CHANGE) + MIN_HUE_CHANGE;
    }

    public LinearLchPaletteBuilder numColors(int numColors)
    {
        this.numColors = numColors;
        return this;
    }

    public LinearLchPaletteBuilder startLum(double startLum)
    {
        this.startLum = startLum;
        return this;
    }

    public LinearLchPaletteBuilder startChroma(double startChroma)
    {
        this.startChroma = startChroma;
        return this;
    }

    public LinearLchPaletteBuilder startHue(double startHue)
    {
        this.startHue = startHue;
        return this;
    }

    public LinearLchPaletteBuilder deltaLum(double deltaLum)
    {
        this.deltaLum = deltaLum;
        return this;
    }

    public LinearLchPaletteBuilder deltaChroma(double deltaChroma)
    {
        this.deltaChroma = deltaChroma;
        return this;
    }

    public LinearLchPaletteBuilder deltaHue(double deltaHue)
    {
        this.deltaHue = deltaHue;
        return this;
    }

    @Override
    public ColorPalette build()
    {
        return new LinearLchPalette(numColors,
                startLum,
                startChroma,
                startHue,
                deltaLum,
                deltaChroma,
                deltaHue,
                random);
    }
}
