package com.samvasta.imagegenerator.generatorpack1.samplegenerator;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.MonochromePalette;
import com.samvasta.imageGenerator.common.graphics.stamps.IStamp;
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

    public void generateImage(final Map<String, Object> settings, final Graphics2D g, final Dimension imageSize, final MersenneTwister random) {
        final ColorPalette palette = new MonochromePalette(random);

        IStamp circleStamp = new IStamp(){
            @Override
            public void stamp(Graphics2D g, int x, int y){
                double diameter = imageSize.width * (random.nextDouble() * 0.15 + 0.01);
                Color col = palette.getColor(random.nextDouble());
                g.setColor(col);
                g.fillOval(x, y, (int)diameter, (int)diameter);

                g.setColor(ColorUtil.shift(col, 0, 0, -0.2f));
                g.drawOval(x, y, (int)diameter, (int)diameter);
            }
        };

        g.setColor(palette.getBiggestColor());
        g.fillRect(0, 0, imageSize.width, imageSize.height);

        for(int i = 0; i < 400; i++){
            circleStamp.stamp(g, random.nextInt((int)(imageSize.width * 1.2)) - (int)(imageSize.width * 0.1), random.nextInt((int)(imageSize.height * 1.2)) - (int)(imageSize.height * 0.1));
        }
    }
}
