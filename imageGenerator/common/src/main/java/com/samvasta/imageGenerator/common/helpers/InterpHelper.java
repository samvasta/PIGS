package com.samvasta.imageGenerator.common.helpers;

import java.awt.geom.Point2D;

public class InterpHelper {

    public static double lerp(double v1, double v2, double percent){
        return (percent * v1) + ((1-percent) * v2);
    }

    public static float lerp(float v1, float v2, float percent){
        return (percent * v1) + ((1-percent) * v2);
    }

    public static double lerp2d(double tl, double tr, double bl, double br, double xPercent, double yPercent){
        double top = lerp(tl, tr, xPercent);
        double bottom = lerp(bl, br, xPercent);
        return lerp(top, bottom, yPercent);
    }

    public static Point2D lerp(Point2D p1, Point2D p2, double percent){
        double x = lerp(p1.getX(), p2.getX(), percent);
        double y = lerp(p1.getY(), p2.getY(), percent);
        return new Point2D.Double(x, y);
    }

}
