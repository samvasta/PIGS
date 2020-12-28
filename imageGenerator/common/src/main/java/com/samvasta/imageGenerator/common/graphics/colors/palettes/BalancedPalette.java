package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.helpers.MathHelper;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;

public class BalancedPalette extends ColorPalette {

    private CeiLchColor bg;
    private CeiLchColor fg;
    private CeiLchColor[] colors;

    public BalancedPalette(RandomGenerator random, String nameIn) {
        super(random, nameIn);
        initColorsAndWeights(random);
    }

    @Override
    protected void initColorsAndWeights(RandomGenerator random) {
        boolean isDark = random.nextBoolean();

        double darkLum = random.nextDouble() * 20;
        double lighLum = 100 - random.nextDouble() * 20;
        double bgChroma = random.nextDouble() * 15;
        double bgHue = random.nextDouble() * 360;
        CeiLchColor dark = new CeiLchColor(darkLum, bgChroma, bgHue);
        CeiLchColor light = new CeiLchColor(lighLum, bgChroma, bgHue);
        if (isDark){
            addColor(dark.toColor(), 10);
            addColor(light.toColor(), 1);
            bg = dark;
            fg = light;
        }else{
            addColor(dark.toColor(), 1);
            addColor(light.toColor(), 10);
            bg = light;
            fg = dark;
        }

        int numColors = random.nextInt(4) + 3;

        colors = new CeiLchColor[numColors];

        double lum = 40 + random.nextDouble() * 20;
        double chroma = 100 + random.nextDouble() * 50;
        double hue = MathHelper.wrap(bgHue + 60, 0, 360);
        for(int i = 0; i < numColors; i++){
            CeiLchColor col = new CeiLchColor(lum, chroma, hue);
            addColor(col.toColor(), 2);
            colors[i] = col;

            hue = MathHelper.wrap(hue + 360*MathHelper.PHI, 0, 360);
            chroma = MathHelper.clamp(chroma + random.nextGaussian() * 20, 100, 150);
            lum = MathHelper.clamp(lum + random.nextGaussian() * 20, 35, 75);

        }
    }

    public CeiLchColor getBg() {
        return bg;
    }

    public CeiLchColor getFg() {
        return fg;
    }

    public CeiLchColor getRandPrimary(RandomGenerator random){
        return colors[random.nextInt(colors.length)];
    }
}
