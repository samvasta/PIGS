package com.samvasta.imageGenerator.common.graphics.colors;

import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;

public class MonochromePalette extends ColorPalette
{
    public MonochromePalette(MersenneTwister random){
        super(random);
    }

    @Override
    protected void initColorsAndWeights(MersenneTwister random)
    {
        float hue = random.nextFloat();
        float sat = 0.8f;
        float val = 1.0f;


        addColor(Color.getHSBColor(hue, 0.25f, 1f), random.nextDouble() + 0.75);
        addColor(Color.getHSBColor(hue, 0.1f, 1f), random.nextDouble() + 0.25);
        addColor(Color.getHSBColor(hue, sat, val), random.nextDouble() + 0.2);
        addColor(Color.getHSBColor(hue, 0.1f, 0.3f), random.nextDouble() + 0.05);
    }
}
