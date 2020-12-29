package com.samvasta.imageGenerator.common.models;

import com.samvasta.imageGenerator.common.helpers.MathHelper;

import javax.vecmath.Vector2d;
import java.awt.geom.Point2D;

public class PolarVector
{
    public double angle;
    public double magnitude;

    public PolarVector(){
        this(0, 0);
    }

    public PolarVector(double angle, double magnitude){
        this.angle = angle;
        this.magnitude = magnitude;
    }

    public Vector2d toCartesianVector(){
        return new Vector2d(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
    }

    public Point2D.Double toCartesianPoint() {
        return new Point2D.Double(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
    }

    public PolarVector add(PolarVector v2){
        Vector2d cartesian1 = toCartesianVector();
        Vector2d cartesian2 = v2.toCartesianVector();
        cartesian1.add(cartesian2);
        return new PolarVector(Math.atan2(cartesian1.y, cartesian1.x), cartesian1.length());
    }

    public PolarVector negative() {
        return new PolarVector(MathHelper.wrap(angle + Math.PI, 0, Math.PI*2), magnitude);
    }

    public static PolarVector fromCartesian(Point2D point) {
        double theta = Math.atan2(point.getY(), point.getX());
        double mag = Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
        return new PolarVector(theta, mag);
    }
}
