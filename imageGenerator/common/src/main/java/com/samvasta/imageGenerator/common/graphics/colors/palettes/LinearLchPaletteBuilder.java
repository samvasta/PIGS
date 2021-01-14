package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

public class LinearLchPaletteBuilder implements IColorPaletteBuilder
{
    private static final int MAX_COLORS = 10;
    private static final int MIN_COLORS = 5;

    private RandomGenerator random;

    private int numColors;
    private double startLum;
    private double startChroma;
    private double startHue;

    private double deltaLum;
    private double deltaChroma;
    private double deltaHue;

    private String name;

    public LinearLchPaletteBuilder(RandomGenerator random, String nameIn){
        this.random = random;
        numColors = MIN_COLORS + random.nextInt(MAX_COLORS - MIN_COLORS);
        startLum = 50 + 50 * random.nextGaussian();
        startChroma = 50 + 50 * random.nextGaussian();
        startHue = random.nextInt(360);

        double endLum = 45;
        double endChroma = 85;
        double endHue = (startHue + random.nextInt(270) + 90) % 360;

        deltaLum = (endLum - startLum) / numColors;
        deltaChroma = (endChroma - startChroma) / numColors;
        deltaHue = (endHue - startHue) / numColors;

        name = nameIn;
    }

    public LinearLchPaletteBuilder numColors(int numColors)
    {
        this.numColors = numColors;
        return this;
    }

    /**
     *
     * @param startLum in range [0,100]
     * @return
     */
    public LinearLchPaletteBuilder startLum(double startLum)
    {
        this.startLum = startLum;
        return this;
    }

    /**
     *
     * @param startChroma in range [0,200]
     * @return
     */
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
                random,
                name);
    }
}
