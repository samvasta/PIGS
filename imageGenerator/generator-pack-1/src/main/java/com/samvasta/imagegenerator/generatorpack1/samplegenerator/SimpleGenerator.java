package com.samvasta.imagegenerator.generatorpack1.samplegenerator;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.MonochromePalette;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleGenerator implements IGenerator
{
    public boolean isOnByDefault()
    {
        return true;
    }

    public List<IniSchemaOption<?>> getIniSettings()
    {
        //I need to make a change so I can test if my commit signing worked
        List<IniSchemaOption<?>> schemaOptions = new ArrayList<>();
        schemaOptions.add(new IniSchemaOption<>("text", "Hello World!", String.class));

        return schemaOptions;
    }

    public boolean isMultiThreadEnabled()
    {
        return true;
    }

    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        ColorPalette palette = new MonochromePalette(random);

        double x = 0;
        for(int i = 0; i < palette.getNumColors(); i++){
            g.setColor(palette.getColorByIndex(i));
            double weight = palette.getNormalizedWeightByIndex(i);
            double delta = imageSize.width * weight;
            g.fillRect((int)x, 0, (int)(x + delta), imageSize.height);
            x += delta;
        }


//        g.setColor(ColorUtil.getRandomColor(random, 128));
//        g.fillRect(0, 0, imageSize.width, imageSize.height);
//
//        g.setColor(ColorUtil.getRandomColor(random, 128));
//        g.fillRect(0, 0, imageSize.width, imageSize.height);

        String str = (String)settings.get("text");
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(45f));
        g.drawString(str, (int)(imageSize.width * random.nextDouble()), (int)(imageSize.height * random.nextDouble()));
    }
}
