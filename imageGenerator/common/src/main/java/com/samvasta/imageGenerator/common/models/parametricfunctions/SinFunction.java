package com.samvasta.imageGenerator.common.models.parametricfunctions;

import java.awt.geom.Point2D;

public class SinFunction implements IParametricFunction {

    private final double amplitude;
    private final double period;
    private final double periodOffset;
    private final double yOffset;

    public SinFunction(double amplitudePercent, double numPeriodsPerUnitTime, double periodOffsetIn, double yOffset){
        amplitude = amplitudePercent;
        period = (2.0 * Math.PI)*numPeriodsPerUnitTime;
        periodOffset = periodOffsetIn;
        this.yOffset = yOffset;
    }

    @Override
    public Point2D get(double time) {
        return new Point2D.Double(time, yOffset + amplitude * Math.sin(time*period + periodOffset));
    }
}
