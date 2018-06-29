package com.samvasta.imagegenerator.generatorpack1.landscape;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.noise.MidpointDisplacement;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class LandscapeGenerator implements IGenerator
{
    private Set<ISnapshotListener> snapshotListeners = new HashSet<>();
    private Dimension imageSize;

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


        double twoPercentHeight = (imageSize.height / 50.0);

        double startY = random.nextGaussian() * twoPercentHeight + 25.0*twoPercentHeight;
        double endY = startY - (random.nextDouble() + 1) * 10.0 * twoPercentHeight;

        drawMountainPeaks(g, startY, endY, palette.getColorByIndex(palette.getNumColors()-1), random);

        takeSnapshot();

        //decrement startY, endY

        for(int i = numColors-2; i >= 0; i--){
            startY += twoPercentHeight + random.nextGaussian() * twoPercentHeight + twoPercentHeight;
            endY = startY + (random.nextDouble() + 0.5) * 10.0 * twoPercentHeight;
            drawRidge(g, startY, endY, palette.getColorByIndex(i), random);
            takeSnapshot();
        }
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

        int[] xPoints;
        int[] yPoints;

        if(highlightEndX < imageSize.width / 2){
            //left
            xPoints = new int[highlightPoints.length + 1 + minYIdx];    //+1 for the bottom corner
            yPoints = new int[highlightPoints.length + 1 + minYIdx];    //+1 for the bottom corner

            for(int i = 0; i < highlightPoints.length; i++){
                xPoints[i] = (int)Math.round(highlightPoints[i].x);
                yPoints[i] = (int)Math.round(highlightPoints[i].y);
            }
            xPoints[highlightPoints.length] = 0;
            yPoints[highlightPoints.length] = imageSize.height;

            for(int i = 0; i < minYIdx; i++){
                xPoints[highlightPoints.length + 1 + i] = (int)Math.round(ridgeLinePoints[i].x);
                yPoints[highlightPoints.length + 1 + i] = (int)Math.round(ridgeLinePoints[i].y);
            }
        }
        else{
            //right
            xPoints = new int[highlightPoints.length + (ridgeLinePoints.length - minYIdx) - 1];
            yPoints = new int[highlightPoints.length + (ridgeLinePoints.length - minYIdx) - 1];

            for(int i = 0; i < highlightPoints.length; i++){
                xPoints[i] = (int)Math.round(highlightPoints[i].x);
                yPoints[i] = (int)Math.round(highlightPoints[i].y);
            }
            xPoints[highlightPoints.length] = imageSize.width;
            yPoints[highlightPoints.length] = imageSize.height;

            for(int i = ridgeLinePoints.length-1; i > minYIdx + 1; i--){
                xPoints[highlightPoints.length + (ridgeLinePoints.length - i)] = (int)Math.round(ridgeLinePoints[i].x);
                yPoints[highlightPoints.length + (ridgeLinePoints.length - i)] = (int)Math.round(ridgeLinePoints[i].y);
            }
        }

        Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);
        g.setColor(new Color(255, 255, 255, 64));
        g.fill(poly);
    }

    private void drawRidge(Graphics2D g, double startY, double endY, Color color, MersenneTwister random){
        Point2D.Double[] points = MidpointDisplacement.getMidpointDisplacement(MidpointDisplacement.DEFLECTION_FACTOR_MEDIUM, random, 4,
                new Point2D.Double(0, startY), new Point2D.Double(imageSize.width/2, endY), new Point2D.Double(imageSize.width, startY));

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
