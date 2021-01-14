package com.samvasta.imagegenerator.generatorpack1.radialpolygons;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class RadialPolygonsGenerator extends SimpleGenerator
{
    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        double variation = random.nextDouble();
        BufferedImage heightmap = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);

        Graphics hmg = heightmap.getGraphics();

        final Point[] points = getPolygon((int)(Math.random() * 11 + 4), variation, imageSize);

        hmg.setColor(Color.BLACK);
        hmg.fillRect(0, 0, heightmap.getWidth(), heightmap.getHeight());

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)imageSize.getWidth(), (int)imageSize.getHeight());

        int numRotations = (int)(variation * 20 + 4);
        double theta = 0;
        double dtheta = Math.PI * 2d / (double)(numRotations);

        Color[] colors = new Color[numRotations];

        ColorPalette palette = new LinearLchPaletteBuilder(random, "linear")
                .numColors(colors.length)
                .startChroma(200 * (random.nextFloat() * 0.55f + 0.45f))
                .startLum(100 * (random.nextFloat() * 0.4f + 0.6f))
                .deltaLum(random.nextGaussian() * 3)
                .deltaChroma(random.nextGaussian() * 5)
                .build();//, Color.getHSBColor(random.nextFloat(), random.nextFloat() * 0.55f + 0.45f, random.nextFloat() * 0.3f + 0.7f));
        colors = palette.getAllColors();

        for(int i = 0; i < numRotations; i++){
            Point[] poly = rotatePolygon(points, theta);

            double maxtheta = (Math.atan2((double)(poly[1].x), (double)(poly[1].y))) + Math.PI/2 - theta,
                    mintheta = (Math.atan2((double)(poly[poly.length-1].x), (double)(poly[poly.length-1].y))) + Math.PI/2 - theta;
            if(maxtheta < mintheta) maxtheta += Math.PI * 2;
            int[] xp = new int[poly.length];
            int[] yp = new int[poly.length];
            for(int j = 0; j < poly.length; j++){
                xp[j] = poly[j].x + (int)imageSize.getWidth()/2;
                yp[j] = poly[j].y + (int)imageSize.getHeight()/2;

            }
            Polygon p = new Polygon(xp, yp, poly.length);
            g.setColor(colors[i]);

            for(int x = p.getBounds().x; x < p.getBounds().x + p.getBounds().getWidth(); x++){
                if(x >= imageSize.getWidth() || x < 0) continue;
                for(int y = p.getBounds().y; y < p.getBounds().y + p.getBounds().getHeight(); y++){
                    if(y >= imageSize.getHeight() || y < 0) continue;

                    if(p.contains(x, y)){
                        if(x == imageSize.getWidth()/2 && y == imageSize.getHeight()/2)continue;

                        double currenttheta = Math.atan2((double)((double)x - imageSize.getWidth()/2), (double)((double)y - (imageSize.getHeight()/2))) + Math.PI/2 - theta;
                        if(currenttheta < mintheta) currenttheta += Math.PI * 2;

                        int currentvalue = (int)(200d * ((currenttheta - mintheta) / (maxtheta - mintheta)));

                        if(currenttheta < mintheta || currenttheta > maxtheta){
                            heightmap.setRGB(x, y, Color.WHITE.getRGB());
                            continue;

                        }
                        Color testCol = new Color(50 + currentvalue, 0, 0);

                        if(heightmap.getRGB(x, y) <= testCol.getRGB()){
                            g.setColor(new Color(colors[i].getRGB()));
                            g.drawLine(x, y, x, y);
                            heightmap.setRGB(x, y, testCol.getRGB());
                        }
                    }

                }
            }

            theta += dtheta;
        }

        g.dispose();
        hmg.dispose();
    }

    public Point[] rotatePolygon(Point[] p, double theta){
        Point newPoly[] = new Point[p.length];
        for(int i = 0; i < p.length; i++){
            newPoly[i] = new Point((int)(((double)p[i].x * Math.cos(theta)) - ((double)p[i].y * Math.sin(theta))), (int)(((double)p[i].x * Math.sin(theta)) + ((double)p[i].y * Math.cos(theta))));
        }

        return newPoly;
    }

    public Point[] getPolygon(int numPoints, double variation, Dimension imageSize){
        double scaleFactor = variation * Math.random() / Math.random();
        int minDimension = Math.min(imageSize.width, imageSize.height);
        int maxSize = (int)(minDimension * scaleFactor);


        ArrayList<Point> points = new ArrayList<Point>(numPoints);


        points.add(0, new Point(0, 0));

        for(int i = 1; i < numPoints; i++){
            double x, y;
            x = Math.random() * maxSize;

            y = -(Math.random() * maxSize);

            points.add(new Point((int)x, (int)y));

        }

        Collections.sort(points, pointComparator);

        return points.toArray(new Point[points.size()]);

    }

    private Comparator<Point> pointComparator = new Comparator<Point>(){
        public int compare(Point arg0, Point arg1) {
            if(arg0.x == 0 && arg0.y == 0){
                return -1;
            }
            if(arg1.x == 0 && arg1.y == 0){
                return 1;
            }
            double theta1 = Math.atan((float)arg0.y / (float)arg0.x);

            double theta2 = Math.atan((float)arg1.y / (float)arg1.x);

            double result = (theta1 - theta2);
            if(result < 0) return -1;
            if(result == 0) return 0;
            return 1;
        }
    };
}
