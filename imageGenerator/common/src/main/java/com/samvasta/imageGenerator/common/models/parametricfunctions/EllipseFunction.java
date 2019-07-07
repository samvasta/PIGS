package com.samvasta.imageGenerator.common.models.parametricfunctions;

import java.awt.geom.Point2D;

public class EllipseFunction implements IParametricFunction {

    private final double width;
    private final double height;
    private final double centerX;
    private final double centerY;

    public EllipseFunction(double widthPercent, double heightPercent, double centerXPercent, double centerYPercent){
        width = widthPercent;
        height = heightPercent;
        centerX = centerXPercent;
        centerY = centerYPercent;
    }

    @Override
    public Point2D get(double time) {
        double percent = time * 2.0 * Math.PI;
        double x = centerX + width*Math.cos(percent);
        double y = centerY + height*Math.sin(percent);
        return new Point2D.Double(x, y);
    }
}