package com.samvasta.imageGenerator.common.helpers;

import java.awt.*;
import java.awt.geom.Point2D;

public class GeomHelper {

    public static double rad2Deg(double radians){
        return radians * 180.0 / Math.PI;
    }

    public static double deg2Rad(double degrees){
        return degrees * Math.PI / 180.0;
    }

    public static double getPositiveAngle(double radians){
        while(radians < 0){
            radians += Math.PI * 2.0;
        }
        while(radians > Math.PI*2.0){
            radians -= Math.PI * 2.0;
        }
        return radians;
    }

    public static double getAngleTo(Point2D p1, Point2D p2){
        double dy = p2.getY() - p1.getY();
        double dx = p2.getX() - p1.getX();
        return GeomHelper.getPositiveAngle(Math.atan2(dy, dx));
    }

    public static Point2D getMidpPoint(Point2D p1, Point2D p2){
        double midX = p2.getX() + (p1.getX() - p2.getX()) / 2.0;
        double midY = p2.getY() + (p1.getY() - p2.getY()) / 2.0;
        return new Point2D.Double(midX, midY);
    }

    public static Point2D addPolar(Point2D p, double magnitude, double angle){
        double x = p.getX() + magnitude * Math.cos(angle);
        double y = p.getY() + magnitude * Math.sin(angle);
        return new Point2D.Double(x, y);
    }

    public static Point2D getIntersection(Point2D p1, double angle1, Point2D p2, double angle2){
        //Trying to find y = slope1 . X + b1 and y = slope2 . X + b2

        //Slopes
        double slope1 = Math.tan(angle1);
        double slope2 = Math.tan(angle2);

        if(Math.abs(slope1 - slope2) < 1e-5){
            return null;
        }

        //Intercepts
        double b1 = slope1 * -1 * p1.getX() + p1.getY();
        double b2 = slope2 * -1 * p2.getX() + p2.getY();

        //set equal to each other and solve:
        // => slope1 . X + b1       = slope2 . X + b2
        // => slope1 . X - slope2.X = b2 - b1
        // =>                     X = (b2 - b1) / (slope1 - slope2)
        double x = (b2-b1) / (slope1 - slope2);

        double y = slope1 * x + b1;

        return new Point.Double(x, y);
    }
}
