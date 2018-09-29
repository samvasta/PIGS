//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imagegenerator.procgenideas.maps.palettes.LandPalette
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imagegenerator.procgenideas.maps.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;

public class LandPalette {

    private final Color earthColor;
    private final Color grasslandColor;
    private final Color forestColor;
    private final Color hillColor;
    private final Color mountainColor;

    public LandPalette(RandomGenerator random){
        earthColor = new CeiLchColor(65, 5, 45).toColor();
        grasslandColor = new CeiLchColor(95, 15, 100).toColor();
        forestColor = new CeiLchColor(75, 35, 135).toColor();
        hillColor = new CeiLchColor(70, 20, 35).toColor();
        mountainColor = new CeiLchColor(77, 10, 40).toColor();
    }

    public Color getEarthColor(RandomGenerator random) {
        return ColorUtil.getClose(earthColor, random.nextDouble() * 0.1);
    }

    public Color getGrasslandColor(RandomGenerator random) {
        return ColorUtil.getClose(grasslandColor, random.nextDouble() * 0.1);
    }

    public Color getForestColor(RandomGenerator random) {
        return ColorUtil.getClose(forestColor, random.nextDouble() * 0.1);
    }

    public Color getHillColor(RandomGenerator random) {
        return ColorUtil.getClose(hillColor, random.nextDouble() * 0.1);
    }

    public Color getMountainColor(RandomGenerator random) {
        return ColorUtil.getClose(mountainColor, random.nextDouble() * 0.1);
    }
}
