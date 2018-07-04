package com.samvasta.imageGenerator.common.graphics.stamps;

import java.awt.*;

public class StampInfo
{
    private double x;
    private double y;
    private double width;
    private double height;
    private double rotationAngle;

    StampInfo(double xIn, double yIn, double maxWidthIn, double maxHeightIn, double rotationAngleIn){
        x = xIn;
        y = yIn;
        width = maxWidthIn;
        height = maxHeightIn;
        rotationAngle = rotationAngleIn;
    }

    public double getX()
    {
        return x;
    }

    /**
     * Because nobody likes having casts everywhere
     * @return {@code (int)x}
     */
    public int getXInt(){
        return (int)x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    /**
     * Because nobody likes having casts everywhere
     * @return {@code (int)y}
     */
    public int getYInt(){
        return (int)y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getWidth()
    {
        return width;
    }

    /**
     * Because nobody likes having casts everywhere
     * @return {@code (int)width}
     */
    public int getWidthInt(){
        return (int)width;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    /**
     * Because nobody likes having casts everywhere
     * @return {@code (int)height}
     */
    public int getHeightInt(){
        return (int)height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public double getRotationAngle()
    {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle)
    {
        this.rotationAngle = rotationAngle;
    }

    public Dimension getDimension(){
        return new Dimension((int)width, (int)height);
    }
}
