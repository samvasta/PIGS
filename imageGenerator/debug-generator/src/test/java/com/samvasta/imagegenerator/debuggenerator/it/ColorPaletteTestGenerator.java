package com.samvasta.imagegenerator.debuggenerator.it;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.BalancedPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.TriadPalette;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColorPaletteTestGenerator implements IGenerator
{
    private static final Logger logger = Logger.getLogger(ColorPaletteTestGenerator.class);
    private ColorPalette palette;

    public ColorPaletteTestGenerator(){
    }

    @Override
    public boolean isOnByDefault()
    {
        return false;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled()
    {
        return false;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener)
    {
        //don't take snapshots. it's just a test
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener)
    {
        //dont' take snapshots. it's just a test
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random)
    {
        double hue = random.nextDouble() * 180;
        double dHue = random.nextGaussian() * 5 + (random.nextBoolean() ? -5 : 5);
        int numColors = random.nextInt(5) + 5;
        double startLum = 50.0;
        this.palette = new LinearLchPaletteBuilder(random, "test")
                .numColors(numColors)
                .startHue(hue)
                .startLum(startLum)
                .startChroma(50)
                .deltaLum((90 - startLum) / numColors)
                .deltaChroma(-5)
                .deltaHue(dHue)
                .build();

        palette = new BalancedPalette(random, "balanced");

        double startX = 0;
        for(int i = 0; i < palette.getNumColors(); i++){
            g.setColor(palette.getColorByIndex(i));
            double normalizedWegiht = palette.getNormalizedWeightByIndex(i);
            double width = imageSize.width * normalizedWegiht;

            g.fillRect((int)startX, 0, (int)(startX + width), imageSize.height);

            startX += width;
        }

    }
}
