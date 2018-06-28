package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;

public class SingleColorPalette extends ColorPalette
{
    public SingleColorPalette(Color color){
        super(new Color[]{color}, new double[]{1.0});
    }

    @Override
    protected void initColorsAndWeights(MersenneTwister random)
    {
        //don't care about this
    }
}
