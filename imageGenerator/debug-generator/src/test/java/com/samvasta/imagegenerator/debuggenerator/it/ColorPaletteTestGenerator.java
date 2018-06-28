package com.samvasta.imagegenerator.debuggenerator.it;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorIlluminant;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.MonochromePalette;
import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random)
    {
        this.palette = new LinearLchPaletteBuilder(random)
                .startChroma(10)
                .startLum(50)
                .deltaHue(10 * random.nextGaussian())
                .deltaLum(4 + 4 * random.nextGaussian())
                .build();

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
