package com.samvasta.imageGenerator.common.models.curves;

import com.samvasta.imageGenerator.common.helpers.MathHelper;

import java.awt.geom.Point2D;

public class HermiteSpline implements IParametricCurve{

    private final HermiteCurve[] curves;
    private final double percentPerCurve;


    public HermiteSpline(Point2D.Double[] points, double tangentStart, double tangentEnd, double tangentMagnitude){
        if(points.length < 2){
            throw new IllegalArgumentException("Hermite Spline requires at least 2 points");
        }

        curves = new HermiteCurve[points.length-1];

        if(curves.length == 1){
            initSingleSegmentCurve(points, tangentStart, tangentEnd, tangentMagnitude);
        }
        else{
            initMultiSegmentCurve(points, tangentStart, tangentEnd, tangentMagnitude);
        }

        percentPerCurve = 1.0 / (double)curves.length;
    }

    /**
     * Creates a hermite spline which connects the end to the start
     */
    public HermiteSpline(Point2D.Double[] points, double tangentMagnitude){
        if(points.length < 2){
            throw new IllegalArgumentException("Hermite Spline requires at least 2 points");
        }

        curves = new HermiteCurve[points.length-1];

        Point2D.Double start = points[0];
        Point2D.Double end = points[points.length-1];

        double dy = end.y - start.y;
        double dx = end.x - start.x;
        double tangent = Math.atan2(dy, dx);

        if(curves.length == 1){
            initSingleSegmentCurve(points, tangent, tangent, tangentMagnitude);
        }
        else{
            initMultiSegmentCurve(points, tangent, tangent, tangentMagnitude);
        }

        percentPerCurve = 1.0 / (double)curves.length;
    }

    private void initMultiSegmentCurve(Point2D.Double[] points, double tangentStart, double tangentEnd, double tangentMagnitude){
        double dy = points[2].y - points[0].y;
        double dx = points[2].x - points[0].x;
        double tangent = Math.atan2(dy, dx);
        curves[0] = new HermiteCurve(points[0], points[1], tangentStart, tangentMagnitude, tangent, tangentMagnitude);

        for(int i = 1; i < curves.length-1; i++){
            dy = points[i+2].y - points[i].y;
            dx = points[i+2].x - points[i].x;
            tangent = Math.atan2(dy, dx);

            curves[i] = new HermiteCurve(points[i], points[i+1], tangent, tangentMagnitude, tangent, tangentMagnitude);
        }

        curves[curves.length-1] = new HermiteCurve(points[points.length-2], points[points.length-1], tangent, tangentMagnitude, tangentEnd, tangentMagnitude);
    }

    private void initSingleSegmentCurve(Point2D.Double[] points, double tangentStart, double tangentEnd, double tangentMagnitude){
        curves[0] = new HermiteCurve(points[0], points[1], tangentStart, tangentMagnitude, tangentEnd, tangentMagnitude);
    }

    public Point2D.Double interpolate(double percent){
        percent = MathHelper.wrap(percent, 0, 1);
        if(percent == 1){
            return curves[curves.length-1].end;
        }

        int curveIdx = (int)(percent / percentPerCurve);
        double remainingPercent = percent % percentPerCurve;

        return curves[curveIdx].interpolate(remainingPercent / percentPerCurve);
    }

    public HermiteCurve getCurveAtPercent(double percent){
        int curveIdx = (int)(percent / percentPerCurve);
        return curves[curveIdx];
    }

    public Point2D.Double[] getInterpolatedPoints(int numSegments) {
        Point2D.Double[] points = new Point2D.Double[numSegments+1];
        for(int i = 0; i < points.length; i++){
            double percent = (double)i / (double)points.length;
            points[i] = interpolate(percent);
        }
        return points;
    }
}
