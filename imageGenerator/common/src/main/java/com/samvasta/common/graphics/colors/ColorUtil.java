//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.graphics.colors.ColorUtil
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.common.graphics.colors;

import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;

public class ColorUtil
{

    public static Color getRandomColor(MersenneTwister random){
        int colorInt = random.nextInt(0xffffff);
        return new Color(colorInt);
    }

    public static Color getRandomColor(MersenneTwister random, int alpha){
        if(alpha > 255 || alpha < 0){
            throw new IllegalArgumentException("alpha must be in range [0 - 255]");
        }
        int colorInt = alpha << 24 | random.nextInt(0xffffff);
        return new Color(colorInt, true);
    }
}
