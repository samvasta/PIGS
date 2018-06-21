//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.graphics.colors.MonochromePalette
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
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

        colors = new Color[5];
        colors[0] =
    }
}
