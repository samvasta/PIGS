//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imageGenerator.common.graphics.colors.SingleColorPalette
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imageGenerator.common.graphics.colors;

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
