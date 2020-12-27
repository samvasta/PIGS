package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;

public class ComplementaryPalette extends ColorPalette {

    private final double angle;
    private final double startHue;

    public ComplementaryPalette(RandomGenerator random, String nameIn){
        this(random.nextGaussian() * 40 + 180, random.nextDouble() * 360, random, nameIn);
    }

    public ComplementaryPalette(double startHue, RandomGenerator random, String nameIn){
        this(random.nextGaussian() * 40 + 180, startHue, random, nameIn);
    }

    public ComplementaryPalette(double angleIn, double startHueIn, RandomGenerator random, String nameIn){
        super(random, nameIn);
        angle = angleIn;
        startHue = startHueIn;
        initColorsAndWeights(random);
    }

    @Override
    protected void initColorsAndWeights(RandomGenerator random) {
        double lum = random.nextDouble() * 25 + 65;
        double chroma = random.nextDouble() * 75 + 25;

        double deltaLum = random.nextGaussian() * 5;
        double deltaChroma = random.nextGaussian() * 5;

        double hue = startHue;
        Color col1 = new CeiLchColor(lum, chroma, hue).toColor();
        hue += angle;
        lum += deltaLum;
        chroma += deltaChroma;
        Color col2 = new CeiLchColor(lum, chroma, hue).toColor();
        hue += angle;
        lum += deltaLum;
        chroma += deltaChroma;
        Color col3 = new CeiLchColor(lum, chroma, hue).toColor();

        addColor(col1, 1);
        addColor(col2, 1);
    }
}
