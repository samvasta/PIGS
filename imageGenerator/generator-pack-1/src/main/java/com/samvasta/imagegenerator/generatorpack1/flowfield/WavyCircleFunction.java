package com.samvasta.imagegenerator.generatorpack1.flowfield;

import com.samvasta.imageGenerator.common.models.parametricfunctions.IParametricFunction;

import java.awt.geom.Point2D;

public class WavyCircleFunction implements IParametricFunction {

    private final double width;
    private final double height;
    private final double centerX;
    private final double centerY;
    private final double period;
    private final double waveAmplitude;

    public WavyCircleFunction(double widthPercent, double widthToHeightRatio, double centerXPercent, double centerYPercent, int numPeriodsPerUnitTime, double waveAmplitudePercent){
        width = widthPercent;
        height = width * widthToHeightRatio;
        centerX = centerXPercent;
        centerY = centerYPercent;
        period = (2.0 * Math.PI)*numPeriodsPerUnitTime;
        waveAmplitude = waveAmplitudePercent;
    }

    @Override
    public Point2D get(double time) {
        double percent = time * 2.0 * Math.PI;

        double radiusX = width + (waveAmplitude * Math.sin(period*time));
        double radiusY = height + (waveAmplitude * Math.sin(period*time));

        double x = centerX + radiusX*Math.cos(percent);
        double y = centerY + radiusY*Math.sin(percent);



        return new Point2D.Double(x, y);
    }
}
