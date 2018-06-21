package com.samvasta.generatorpack1.samplegenerator;

import com.samvasta.common.interfaces.IGenerator;
import com.samvasta.common.models.IniSchemaOption;
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
        List<IniSchemaOption<?>> schemaOptions = new ArrayList<>();
        schemaOptions.add(new IniSchemaOption<>("text", "Hello World!", String.class));

        return schemaOptions;
    }

    public boolean isMultiThreadEnabled()
    {
        return true;
    }

    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, imageSize.width, imageSize.height);

        String str = (String)settings.get("text");
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(45f));
        g.drawString(str, (int)(imageSize.width * random.nextDouble()), (int)(imageSize.height * random.nextDouble()));
    }
}
