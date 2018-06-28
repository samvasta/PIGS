package com.samvasta.imageGenerator.common.models;

import javax.vecmath.Vector2d;

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

    public PolarVector add(PolarVector v2){
        Vector2d cartesian1 = toCartesianVector();
        Vector2d cartesian2 = v2.toCartesianVector();
        cartesian1.add(cartesian2);
        return new PolarVector(Math.atan2(cartesian1.y, cartesian1.x), cartesian1.length());
    }
}
