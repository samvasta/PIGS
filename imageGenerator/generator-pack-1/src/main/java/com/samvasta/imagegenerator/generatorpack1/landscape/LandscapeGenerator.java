package com.samvasta.imagegenerator.generatorpack1.landscape;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.noise.MidpointDisplacement;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class LandscapeGenerator implements IGenerator
{
    private Set<ISnapshotListener> snapshotListeners = new HashSet<>();
    private RandomGenerator random;
    private Dimension imageSize;
    private double twoPercentHeight;
    private int numLayers;

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
    public void addSnapshotListener(ISnapshotListener listener)
    {
        if(!snapshotListeners.contains(listener)){
            snapshotListeners.add(listener);
        }
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener)
    {
        if(snapshotListeners.contains(listener)){
            snapshotListeners.remove(listener);
        }
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random)
    {
        this.random = random;
        this.imageSize = imageSize;

        double hue = random.nextDouble() * 180;
        double dHue = random.nextGaussian() * 5 + (random.nextBoolean() ? -5 : 5);
        int numColors = random.nextInt(5) + 5;
        double startLum = 50.0;
        ColorPalette palette = new LinearLchPaletteBuilder(random)
                .numColors(numColors)
                .startHue(hue)
                .startLum(startLum)
                .startChroma(50)
                .deltaLum((90 - startLum) / numColors)
                .deltaChroma(-5)
                .deltaHue(dHue)
                .build();

        Color background = new CeiLchColor(90, 10, hue + 180).toColor();
        g.setColor(background);
        g.fillRect(0,0,imageSize.width, imageSize.height);


        twoPercentHeight = (imageSize.height / 50.0);

        double startY = random.nextGaussian() * twoPercentHeight + 25.0*twoPercentHeight;
        double endY = startY - (random.nextDouble() + 1) * 10.0 * twoPercentHeight;

        drawMountainPeaks(g, startY, endY, palette.getColorByIndex(palette.getNumColors()-1), random);

        takeSnapshot();
        numLayers = numColors - 1;

        int waterEndWidth = random.nextInt(imageSize.width/4) + imageSize.width*3/4;
        int waterStartWidth = random.nextInt(waterEndWidth/2) + waterEndWidth/4;
        int waterStartY = imageSize.height/4 + (random.nextInt(imageSize.height/4) + imageSize.height / 4);
        int[] waterX = new int[] { imageSize.width/2 - waterStartWidth/2, imageSize.width/2 + waterStartWidth/2, imageSize.width/2 + waterEndWidth/2, imageSize.width/2 - waterEndWidth/2 };
        int[] waterY = new int[] { waterStartY, waterStartY, imageSize.height, imageSize.height};
        Polygon water = new Polygon(waterX, waterY, 4);

        FastNoise dipXGenerator = new FastNoise(random.nextInt());

        for(int i = numColors-2; i >= 0; i--)
        {
            startY += 0.1 * (twoPercentHeight + random.nextGaussian() * twoPercentHeight + twoPercentHeight);
            endY = startY + (random.nextDouble() + 0.5) * 20.0 * twoPercentHeight;
            int dipX = imageSize.width/ 2 + (int)(imageSize.width/2.0 * dipXGenerator.GetSimplex(0, i));
            drawRidge(g, startY, endY, dipX, palette.getColorByIndex(i), random, i);
            System.out.println(dipX);

            if (i < numLayers - 1)
            {
                g.setClip(0, (int) (endY  - twoPercentHeight), imageSize.width, imageSize.height);
                g.setColor(new Color(255, 255, 255, 64));
                g.fill(water);
                g.setClip(null);
            }


            takeSnapshot();
        }

        TreeStamp stamp = new TreeStamp();
        g.setColor(Color.RED);
        stamp.stamp(g, imageSize.width/2, imageSize.height/2, 60, 100, 0);
    }

    private void drawMountainPeaks(Graphics2D g, double startY, double endY, Color color, MersenneTwister random){
        Point2D.Double[] ridgeLinePoints = MidpointDisplacement.getMidpointDisplacement(MidpointDisplacement.DEFLECTION_FACTOR_MEDIUM, random, 4,
                new Point2D.Double(0, startY), new Point2D.Double(imageSize.width/2, endY), new Point2D.Double(imageSize.width, startY));

        fillDown(ridgeLinePoints, color, g);

        //find local maxima
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        double minY = Integer.MAX_VALUE;
        int minYIdx = -1;
        for(int i = 1; i < ridgeLinePoints.length-1; i++){
            if(ridgeLinePoints[i].y <= ridgeLinePoints[i+1].y && ridgeLinePoints[i].y <= ridgeLinePoints[i-1].y){
                g.drawOval((int)(ridgeLinePoints[i].x-5), (int)(ridgeLinePoints[i].y-5), 11, 11);
            }
            if(ridgeLinePoints[i].y < minY){
                minY = ridgeLinePoints[i].y;
                minYIdx = i;
            }
        }

        g.fillOval((int)(ridgeLinePoints[minYIdx].x-5), (int)(ridgeLinePoints[minYIdx].y-5), 11, 11);

        double highlightEndX = random.nextDouble() * imageSize.width;
        Point2D.Double[] highlightPoints = MidpointDisplacement.getMidpointDisplacement(new Point2D.Double(ridgeLinePoints[minYIdx].x, ridgeLinePoints[minYIdx].y), new Point2D.Double(highlightEndX, imageSize.height), MidpointDisplacement.DEFLECTION_FACTOR_MEDIUM, random, 4);

        if(highlightEndX < imageSize.width / 2){
            //left
            drawMountainHighlightLeft(ridgeLinePoints, minYIdx, highlightPoints, g);
        }
        else{
            //right
            drawMountainHighlightRight(ridgeLinePoints, minYIdx, highlightPoints, g);
        }
    }

    private void drawMountainHighlightLeft(Point2D.Double[] ridgeLinePoints, int peakIdx, Point2D.Double[] highlightPoints, Graphics2D g){
        List<Double> xPointsList = new ArrayList<>();
        List<Double> yPointsList = new ArrayList<>();

        for(int i = 0; i < highlightPoints.length; i++){
            xPointsList.add(highlightPoints[i].x);
            yPointsList.add(highlightPoints[i].y);
        }
        xPointsList.add(0.0);
        yPointsList.add((double)imageSize.height);

        double[] dy = new double[peakIdx+1];
        for(int i = 0; i < dy.length; i++){
            dy[i] = ridgeLinePoints[i].y - ridgeLinePoints[i+1].y;
        }

        for(int i = 0; i < peakIdx; i++){
            xPointsList.add(ridgeLinePoints[i].x);
            if(i > 0 && dy[i] < imageSize.height * 0.005){
                yPointsList.add(ridgeLinePoints[i].y);
                while(i+1 < dy.length && dy[i+1] < imageSize.height * 0.005){
                    i++;
                }
                yPointsList.add(ridgeLinePoints[i].y + twoPercentHeight + 0.5 * twoPercentHeight * random.nextGaussian());
                xPointsList.add(ridgeLinePoints[i].x);
            }
            else{
                yPointsList.add(ridgeLinePoints[i].y);
            }
        }

        int[] xPoints = new int[xPointsList.size()];    //+1 for the bottom corner
        int[] yPoints = new int[yPointsList.size()];    //+1 for the bottom corner

        for(int i = 0; i < xPointsList.size(); i++){
            xPoints[i] = (int)Math.round(xPointsList.get(i));
            yPoints[i] = (int)Math.round(yPointsList.get(i));
        }

        Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);
        g.setColor(new Color(255, 255, 255, 64));
        g.fill(poly);
    }

    private void drawMountainHighlightRight(Point2D.Double[] ridgeLinePoints, int peakIdx, Point2D.Double[] highlightPoints, Graphics2D g){
        List<Double> xPointsList = new ArrayList<>();
        List<Double> yPointsList = new ArrayList<>();

        for(int i = 0; i < highlightPoints.length; i++){
            xPointsList.add(highlightPoints[i].x);
            yPointsList.add(highlightPoints[i].y);
        }
        xPointsList.add((double)imageSize.width);
        yPointsList.add((double)imageSize.height);

        double[] dy = new double[ridgeLinePoints.length - peakIdx];
        for(int i = ridgeLinePoints.length-1; i > peakIdx; i--){
            dy[ridgeLinePoints.length - i - 1] = ridgeLinePoints[i].y - ridgeLinePoints[i-1].y;
        }

        for(int i = ridgeLinePoints.length-1; i > peakIdx + 1; i--){
            xPointsList.add(ridgeLinePoints[i].x);
            if(ridgeLinePoints.length - i - 1 > 0 && dy[ridgeLinePoints.length - i - 1] < imageSize.height * 0.005){
                yPointsList.add(ridgeLinePoints[i].y);
                while(ridgeLinePoints.length - i - 1 < dy.length && dy[ridgeLinePoints.length - i - 1] < imageSize.height * 0.005){
                    i--;
                }

                xPointsList.add(ridgeLinePoints[i].x);
                yPointsList.add(ridgeLinePoints[i].y + twoPercentHeight + 0.5 * twoPercentHeight * random.nextGaussian());
            }
            else{
                yPointsList.add(ridgeLinePoints[i].y);
            }
        }


        int[] xPoints = new int[xPointsList.size()];    //+1 for the bottom corner
        int[] yPoints = new int[yPointsList.size()];    //+1 for the bottom corner

        for(int i = 0; i < xPointsList.size(); i++){
            xPoints[i] = (int)Math.round(xPointsList.get(i));
            yPoints[i] = (int)Math.round(yPointsList.get(i));
        }

        Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);
        g.setColor(new Color(255, 255, 255, 64));
        g.fill(poly);
    }

    private void drawRidge(Graphics2D g, double startY, double endY, int dipX, Color color, MersenneTwister random, int layer){
        double percent = (double)layer / (double)numLayers;
        double deflectionFactor = (percent * percent) * MidpointDisplacement.DEFLECTION_FACTOR_LOW + (1.0 - (percent * percent)) * MidpointDisplacement.DEFLECTION_FACTOR_MEDIUM;
        int numSteps = (int)Math.round((percent) * 3 + (1.0 - percent) * 10);

        Point2D.Double[] points = MidpointDisplacement.getMidpointDisplacement(deflectionFactor, random, numSteps,
                new Point2D.Double(0, startY), new Point2D.Double(dipX, endY), new Point2D.Double(imageSize.width, startY));

        fillDown(points, color, g);
    }

    private void fillDown(Point2D.Double[] points, Color color, Graphics2D g){
        int[] xPoints = new int[points.length + 2]; //+2 for the bottom corners
        int[] yPoints = new int[points.length + 2]; //+2 for the bottom corners

        for(int i = 0; i < points.length; i++){
            xPoints[i] = (int)Math.round(points[i].x);
            yPoints[i] = (int)Math.round(points[i].y);
        }

        xPoints[xPoints.length-2] = (int)Math.round(points[points.length-1].x);
        yPoints[yPoints.length-2] = imageSize.height;

        xPoints[xPoints.length-1] = (int)Math.round(points[0].x);
        yPoints[yPoints.length-1] = imageSize.height;

        Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);
        g.setColor(color);
        g.fill(poly);
    }

    private void takeSnapshot(){
        for(ISnapshotListener snapshotListener : snapshotListeners){
            snapshotListener.takeSnapshot();
        }
    }
}
