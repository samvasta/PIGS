package com.samvasta.imageGenerator.common.models;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

public class PrecisePoint2D extends Point2D
{
    private BigDecimal x;
    private BigDecimal y;

    public PrecisePoint2D(){
        x = new BigDecimal(0);
        y = new BigDecimal(0);
    }

    public PrecisePoint2D(double xIn, double yIn){
        x = new BigDecimal(xIn);
        y = new BigDecimal(yIn);
    }

    public PrecisePoint2D(BigDecimal xIn, BigDecimal yIn){
        x = xIn;
        y = yIn;
    }

    public PrecisePoint2D add(double xIn, double yIn){
        return new PrecisePoint2D(x.add(new BigDecimal(xIn)), y.add(new BigDecimal(yIn)));
    }

    public PrecisePoint2D subtract(double xIn, double yIn){
        return new PrecisePoint2D(x.subtract(new BigDecimal(xIn)), y.subtract(new BigDecimal(yIn)));
    }

    public double getX(){
        return x.doubleValue();
    }
    public double getY(){
        return y.doubleValue();
    }

    public BigDecimal getPreciseX(){
        return x;
    }
    public BigDecimal getPreciseY(){
        return y;
    }

    @Override
    public void setLocation(double xIn, double yIn) {
        x = new BigDecimal(xIn);
        y = new BigDecimal(yIn);
    }
}
