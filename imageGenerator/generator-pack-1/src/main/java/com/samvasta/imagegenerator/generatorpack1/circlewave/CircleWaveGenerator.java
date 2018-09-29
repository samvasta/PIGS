//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imagegenerator.generatorpack1.circlewave.CircleWaveGenerator
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imagegenerator.generatorpack1.circlewave;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.curves.HermiteSpline;
import com.samvasta.imageGenerator.common.noise.MidpointDisplacement;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CircleWaveGenerator implements IGenerator {
    private List<ISnapshotListener> snapshotListeners = new ArrayList<>();

    @Override
    public boolean isOnByDefault() {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        double baseHue = random.nextDouble()*360;
        CeiLchColor base = new CeiLchColor(90, 20, baseHue);
        CeiLchColor baseAlt = new CeiLchColor(80, 25, baseHue);

        double wavePrimaryHue = baseHue + (random.nextBoolean() ? 1 : -1) * (random.nextDouble() * 50 + 90);

        ColorPalette primaryRamp = new LinearLchPaletteBuilder(random)
                                        .startLum(25)
                                        .startChroma(85)
                                        .startHue(wavePrimaryHue)
                                        .deltaLum(15)
                                        .deltaChroma(-10)
                                        .deltaHue((random.nextBoolean() ? 1 : -1) * (baseHue - wavePrimaryHue) * 0.15)
                                        .numColors(4)
                                        .build();

        double waveSecondaryHue = ((baseHue - wavePrimaryHue) / 2) % 360;
        CeiLchColor waveSecondary = new CeiLchColor(50, 30, waveSecondaryHue);

        g.setColor(base.toColor());
        g.fillRect(0,0,imageSize.width,imageSize.height);

        takeSnapshot();

        g.setColor(baseAlt.toColor());
        int centerX = imageSize.width/2;
        int centerY = imageSize.height/2;
        int radius = (int)(Math.min(imageSize.width, imageSize.height) * 0.45);
        Dimension circleBound = new Dimension(radius*2, radius*2);
        Shape baseAltOval = new Ellipse2D.Double(centerX-radius, centerY-radius, radius*2, radius*2);
        g.fill(baseAltOval);

        takeSnapshot();

        double yStart = centerY + (random.nextGaussian() * radius / 2.0);
        double yEnd = (radius) - (yStart - centerY);

        Point2D.Double start = new Point2D.Double(0, yStart);
        Point2D.Double end = new Point2D.Double(imageSize.width, yEnd);
        Point2D.Double[] points = MidpointDisplacement.getMidpointDisplacement(start, end, MidpointDisplacement.DEFLECTION_FACTOR_MEDIUM, random, 3);

        Polygon p = getFillPoly(points, centerX, centerY, imageSize, random);

        if(random.nextDouble() < 0.3){
            g.setColor(waveSecondary.toColor(32 + random.nextInt(64)));
            g.fill(p);

            takeSnapshot();
        }

        g.setClip(baseAltOval);

        g.setColor(waveSecondary.toColor());
        g.fill(p);

        takeSnapshot();

        double deflectionFactor = MidpointDisplacement.DEFLECTION_FACTOR_MEDIUM;
        int alpha = 128;
        for(int i = 0; i < primaryRamp.getNumColors(); i++){
            yStart = centerY + random.nextGaussian() * radius / 4.0;
            yEnd = centerY + random.nextGaussian() * radius / 4.0;

            g.setColor(ColorUtil.getTransparent(primaryRamp.getColorByIndex(i), alpha));
            start = new Point2D.Double(centerX - radius, yStart);
            end = new Point2D.Double(centerX + radius, yEnd);
            points = MidpointDisplacement.getMidpointDisplacement(start, end, deflectionFactor, random, 2);

            p = getFillPoly(points, centerX, centerY, circleBound, random);

            g.fill(p);

            takeSnapshot();

            deflectionFactor *= 1.0 + random.nextDouble() * 0.5;
            alpha = (int)(alpha * 0.8);
        }
    }

    private Polygon getFillPoly(Point2D.Double[] points, int centerX, int centerY, Dimension boundSize, RandomGenerator random){
        Polygon p = new Polygon();

        double tangentOut = random.nextDouble() * Math.PI/3.0 - Math.PI/6.0;
        double tangentIn = random.nextDouble() * Math.PI/3.0 - Math.PI/6.0;
        HermiteSpline spline = new HermiteSpline(points, tangentOut, tangentIn, Math.max(boundSize.width/2, boundSize.height/2) / (random.nextDouble() + 1.5));

        //bottom left
        p.addPoint(centerX - boundSize.width/2, centerY + boundSize.height/2);

        for(double percent = 0; percent < 1.0; percent += 0.005){
            Point2D.Double point = spline.interpolate(percent);
            p.addPoint((int)point.x, (int)point.y);
        }
        Point2D.Double point = spline.interpolate(1.0);
        p.addPoint((int)point.x, (int)point.y);

        //bottom right
        p.addPoint(centerX + boundSize.width/2, centerY + boundSize.height/2);

        return p;
    }

    private void takeSnapshot(){
        for(ISnapshotListener listener : snapshotListeners){
            listener.takeSnapshot();
        }
    }
}
