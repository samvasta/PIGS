package com.samvasta.imageGenerator.common.graphics.colors.palettes;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

public class MonochromePalette extends ColorPalette
{
    private static final int MAX_COLORS = 10;
    private static final int MIN_COLORS = 5;

    private final int numColors;
    private final double startHue;

    public MonochromePalette(RandomGenerator random, String nameIn){
        this(random.nextInt((MAX_COLORS - MIN_COLORS)) + MIN_COLORS, random.nextInt(360), random, nameIn);
    }

    public MonochromePalette(int numColors, RandomGenerator random, String nameIn){
        this(numColors, random.nextInt(360), random, nameIn);
    }

    public MonochromePalette(double startHue, RandomGenerator random, String nameIn){
        this(random.nextInt((MAX_COLORS - MIN_COLORS)) + MIN_COLORS, startHue, random, nameIn);
    }

    public MonochromePalette(int numColors, double startHue, RandomGenerator random, String nameIn){
        super(random, nameIn);
        this.startHue = startHue;
        this.numColors = numColors;
        initColorsAndWeights(random);
    }

    @Override
    protected void initColorsAndWeights(RandomGenerator random)
    {
        double bigLumChange = 30 + random.nextGaussian() * 10;
        double smallLumChange = bigLumChange * 0.6;

        double bigChromaChange = 30 + random.nextGaussian() * 10;
        double smallChromaChange = bigChromaChange * 0.6;

        //MINIMUM COLORS:
        //primary
        CeiLchColor primary = new CeiLchColor(50, 50, startHue);

        //secondary
        CeiLchColor secondary = primary.add(smallLumChange, bigChromaChange, 0);

        //primary + tint (+ white)
        CeiLchColor tint = primary.add(bigLumChange, -smallChromaChange, 0);

        //primary + shade (+ black)
        CeiLchColor shade = primary.add(-bigLumChange, -smallChromaChange, 0);

        //primary + tone (+ gray)
        CeiLchColor tone = primary.add(0, -bigChromaChange, 0);

        CeiLchColor[] basicColors = new CeiLchColor[] {primary, secondary, tint, shade, tone};

        addColor(primary.toColor(), 0.4);
        addColor(secondary.toColor(), 0.1);
        addColor(tint.toColor(), 0.2);
        addColor(shade.toColor(), 0.2);
        addColor(tone.toColor(), 0.2);

        //add the rest of the colors based on one of the existing colors
        for(int i = 5; i < numColors; i++){
            CeiLchColor base = basicColors[random.nextInt(5)];

            double lumChange = smallLumChange * random.nextGaussian() + smallLumChange;
            double chromaChange = smallChromaChange * random.nextGaussian() + smallChromaChange;

            addColor(base.add(lumChange, chromaChange, 0).toColor(), 0.05);

        }
    }
}
