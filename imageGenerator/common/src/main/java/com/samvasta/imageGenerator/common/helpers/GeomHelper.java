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

    public static Point2D.Double addPolar(Point2D p, double magnitude, double angle){
        double x = p.getX() + magnitude * Math.cos(angle);
        double y = p.getY() + magnitude * Math.sin(angle);
        return new Point2D.Double(x, y);
    }


    public static Point2D.Double[] addPolar(Point2D[] points, double magnitude, double angle){
        Point2D.Double[] results = new Point2D.Double[points.length];
        for(int i = 0; i < points.length; i++){
            results[i] = addPolar(points[i], magnitude, angle);
        }
        return results;
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

    public static Point2D.Double scale(Point2D.Double p, double scale) {
        return new Point2D.Double(p.x * scale, p.y * scale);
    }

    public static Point2D.Double rotate(Point2D.Double p, double angle){
        double newX = (p.x * Math.cos(angle)) - (p.y * Math.sin(angle));
        double newY = (p.x * Math.sin(angle)) + (p.y * Math.cos(angle));
        return new Point2D.Double(newX, newY);
    }

    public static void rotate(Point2D.Double[] points, double angle){
        for(int i = 0; i < points.length; i++){
            points[i] = rotate(points[i], angle);
        }
    }

    /*
    Centroid algorithm is based on an algorithm from
    http://www.faqs.org/faqs/graphics/algorithms-faq/
    (Relevant section copied below)
    ============================================================================
    Subject 2.02: How can the centroid of a polygon be computed?

    The centroid (a.k.a. the center of mass, or center of gravity)
    of a polygon can be computed as the weighted sum of the centroids
    of a partition of the polygon into triangles.  The centroid of a
    triangle is simply the average of its three vertices, i.e., it
    has coordinates (x1 + x2 + x3)/3 and (y1 + y2 + y3)/3.  This
    suggests first triangulating the polygon, then forming a sum
    of the centroids of each triangle, weighted by the area of
    each triangle, the whole sum normalized by the total polygon area.
    This indeed works, but there is a simpler method:  the triangulation
    need not be a partition, but rather can use positively and
    negatively oriented triangles (with positive and negative areas),
    as is used when computing the area of a polygon.  This leads to
    a very simple algorithm for computing the centroid, based on a
    sum of triangle centroids weighted with their signed area.
    The triangles can be taken to be those formed by any fixed point,
    e.g., the vertex v0 of the polygon, and the two endpoints of
    consecutive edges of the polygon: (v1,v2), (v2,v3), etc.  The area
    of a triangle with vertices a, b, c is half of this expression:
                (b[X] - a[X]) * (c[Y] - a[Y]) -
                (c[X] - a[X]) * (b[Y] - a[Y]);
     */
    /**
     * Computes the centroid of a convex polygon
     * @param points points of the polygon
     * @return a single point representing the weighted center of the polygon
     */
    public static Point2D.Double getCentroid(Point2D.Double[] points) {
        Point2D.Double a = points[0];

        double xSum = 0;
        double ySum = 0;
        double weightSum = 0;

        for(int i = 2; i < points.length; i++){
            Point2D.Double b = points[i-1];
            Point2D.Double c = points[i];
            double area = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
            area /= 2.0;

            xSum += area * (a.x + b.x + c.x) / 3.0;
            ySum += area * (a.y + b.y + c.y) / 3.0;

            weightSum += area;
        }

        return new Point2D.Double(xSum / weightSum, ySum / weightSum);
    }
}
