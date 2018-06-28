package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import org.apache.commons.math3.random.MersenneTwister;

public class LinearLchPalette extends ColorPalette
{
    private final int numColors;
    private final double startLum;
    private final double startChroma;
    private final double startHue;

    private final double deltaLum;
    private final double deltaChroma;
    private final double deltaHue;

    public LinearLchPalette(int numColors, double startLum, double startChroma, double startHue, double deltaLum, double deltaChroma, double deltaHue, MersenneTwister random)
    {
        super(random);
        this.numColors = numColors;
        this.startLum = startLum;
        this.startChroma = startChroma;
        this.startHue = startHue;
        this.deltaLum = deltaLum;
        this.deltaChroma = deltaChroma;
        this.deltaHue = deltaHue;
        initColorsAndWeights(random);
    }

    @Override
    protected void initColorsAndWeights(MersenneTwister random)
    {
        CeiLchColor col = new CeiLchColor(startLum, startChroma, startHue);

        for(int i = 0; i < numColors; i++){
            addColor(col.toColor(), 1);
            col = col.add(deltaLum, deltaChroma, deltaHue);
        }
    }
}
