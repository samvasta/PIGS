package com.samvasta.imagegenerator.generatorpack1.fog;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.PaletteFactory;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import com.samvasta.imageGenerator.common.shapes.deformable.DeformablePolygon;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Random;

/**
 * Created by Sam on 7/8/2017.
 */
public class FogGenerator extends SimpleGenerator
{

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        ColorPalette palette = PaletteFactory.getRandomPalette(random);

        if(useDarkBackground(palette)){
            g.setColor(new Color(17,17,17));
        }
        else{
            g.setColor(new Color(255-17,255-17,255-17));
        }

        g.fillRect(0, 0, imageSize.width, imageSize.height);

        List<Point2D.Double> centers = new ArrayList<>();

        double distance = Math.min(imageSize.width, imageSize.height) * 0.4d;

        double x;
        double y;

        if(imageSize.width > imageSize.height){
            x = imageSize.width/3d + random.nextGaussian() * imageSize.width/10d;
            y = imageSize.height/2d + random.nextGaussian() * imageSize.height/10d;
            y = imageSize.height/2d + random.nextGaussian() * imageSize.height/10d;
            if(random.nextBoolean()){
                x += imageSize.width/3d;
            }
        }
        else{
            x = imageSize.width/2d + random.nextGaussian() * imageSize.width/10d;
            y = imageSize.height/3d + random.nextGaussian() * imageSize.height/10d;
            if(random.nextBoolean()){
                y += imageSize.height/3d;
            }
        }

        for(int i = 0; i < 10; i++){
            double origX = x;
            double origY = y;
            centers.add(new Point2D.Double(x, y));
            do{
                double angle = random.nextDouble() * 2d * Math.PI;
                x = origX + distance * Math.cos(angle);
                y = origY + distance * Math.sin(angle);
            } while(x > imageSize.width - imageSize.width/6d ||
                    x < imageSize.width/6d ||
                    y > imageSize.height - imageSize.height/6d ||
                    y < imageSize.height/6d);
        }

        for(Point2D.Double p : centers){
            paintSplotch(random, g, p, palette.getAllColors()[random.nextInt(palette.getNumColors())], distance);
        }

    }

    private void paintSplotch(RandomGenerator random, Graphics2D g, Point2D center, Color color, double distance){
        Color c = ColorUtil.getTransparent(color, 0.05f);

        DeformablePolygon polygon = new DeformablePolygon(3 + random.nextInt(3), distance/10d * random.nextInt(10) + distance/2d);
        polygon.deform(random, 2);
        polygon.setTranslation(center.getX(), center.getY());

        g.setColor(c);
        for(int i = 0; i < 15; i++){
            DeformablePolygon p = polygon.clone();
            p.deform(random, 5);
            g.fill(p.getPolygon());
        }
    }

    private boolean useDarkBackground(ColorPalette pal){
        double avgScore = 0;
        for(Color c : pal.getAllColors()){
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), new float[3]);
            avgScore += hsb[1] * 0.3 + hsb[2] * 0.7;
        }
        avgScore /= pal.getNumColors();
        return avgScore >= 0.4;
    }
}
