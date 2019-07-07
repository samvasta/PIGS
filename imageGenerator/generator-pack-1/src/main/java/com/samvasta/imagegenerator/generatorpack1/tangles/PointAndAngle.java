package com.samvasta.imagegenerator.generatorpack1.tangles;

import java.awt.geom.Point2D;

public class PointAndAngle {

    public final Point2D point;
    public final double angle;

    public PointAndAngle(Point2D pointIn, double angleIn){
        point = pointIn;
        while(angleIn < 0) {
            angleIn += 2.0*Math.PI;
        }
        angle = angleIn;
    }

    public double getX(){return point.getX();}
    public double getY(){return point.getY();}
    public int getXInt(){return (int)point.getX();}
    public int getYInt(){return (int)point.getY();}
}
