package com.samvasta.imageGenerator.common.models.parametricfunctions;

import java.awt.geom.Point2D;

public class LinearFunction implements IParametricFunction {

    private final double xIntercept;
    private final double xSlope;
    private final double yIntercept;
    private final double ySlope;

    public LinearFunction(double xIntercept, double xSlope, double yIntercept, double ySlope){
        this.xIntercept = xIntercept;
        this.xSlope = xSlope;
        this.yIntercept = yIntercept;
        this.ySlope = ySlope;
    }

    @Override
    public Point2D get(double time) {
        double x = xIntercept + time*xSlope;
        double y = yIntercept + time*ySlope;
        return new Point2D.Double(x, y);
    }
}
