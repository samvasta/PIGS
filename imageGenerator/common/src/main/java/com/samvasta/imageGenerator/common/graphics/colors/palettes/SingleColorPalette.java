package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;

public class SingleColorPalette extends ColorPalette
{
    public SingleColorPalette(Color color){
        super(new Color[]{color}, new double[]{1.0}, "Single");
    }

    @Override
    protected void initColorsAndWeights(RandomGenerator random)
    {
        //don't care about this
    }
}
