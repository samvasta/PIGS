package com.samvasta.imagegenerator.generatorpack1.landscape;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.noise.MidpointDisplacement;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LandscapeGenerator implements IGenerator
{
    @Override
    public boolean isOnByDefault()
    {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled()
    {
        return true;
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random)
    {
        Point2D.Double[] points = MidpointDisplacement.getMidpointDisplacement(new Point2D.Double(50, imageSize.height/2), new Point2D.Double(imageSize.width-50, imageSize.height/2), MidpointDisplacement.DEFLECTION_FACTOR_LOW, random, 8);

        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageSize.width, imageSize.height);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3f));

        Point2D.Double start = points[0];
        g.drawOval((int)start.x-5, (int)start.y-5, 10, 10);

        for(int i = 0; i < points.length-1; i++){
            Point2D.Double p1 = points[i];
            Point2D.Double p2 = points[i+1];

            g.drawOval((int)p2.x-5, (int)p2.y-5, 10, 10);
            g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }
    }
}
