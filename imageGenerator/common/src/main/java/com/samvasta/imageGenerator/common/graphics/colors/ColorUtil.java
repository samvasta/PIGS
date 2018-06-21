//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.graphics.colors.ColorUtil
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imageGenerator.common.graphics.colors;

import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.color.ColorSpace;

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

    /**
     * Creates a new color by shifting the HSV components of a color without wrapping the Saturation and Value components. (The Hue component can wrap)
     */
    public static Color shift(Color c, float deltaHue, float deltaSaturation, float deltaBrightness){
        return shift(c, deltaHue, deltaSaturation, deltaBrightness, false, false);
    }

    /**
     * Creates a new color by shifting the HSV components of a color
     */
    public static Color shift(Color c, float deltaHue, float deltaSaturation, float deltaBrightness, boolean wrapSaturation, boolean wrapBrightness){
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);

        hsb[0] += deltaHue;
        hsb[1] += deltaSaturation;
        hsb[2] += deltaBrightness;

        if(hsb[0] < 0){
            hsb[0] += 1f;
        }
        else if(hsb[0] > 1){
            hsb[0] += -1f;
        }

        if(wrapSaturation){
            if(hsb[1] < 0){
                hsb[1] += 1f;
            }
            else if(hsb[1] > 1){
                hsb[1] += -1f;
            }
        }

        if(wrapBrightness){
            if(hsb[2] < 0){
                hsb[2] += 1f;
            }
            else if(hsb[2] > 1){
                hsb[2] += -1f;
            }
        }

        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
}
