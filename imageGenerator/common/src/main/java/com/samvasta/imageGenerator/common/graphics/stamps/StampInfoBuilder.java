package com.samvasta.imageGenerator.common.graphics.stamps;

import com.samvasta.imageGenerator.common.exceptions.IncompleteBuilderException;

public class StampInfoBuilder
{
    private double x;
    private double y;
    private double maxWidth;
    private double maxHeight;
    private double rotationAngle;


    public StampInfoBuilder x(double x)
    {
        this.x = x;
        return this;
    }

    public StampInfoBuilder y(double y)
    {
        this.y = y;
        return this;
    }

    public StampInfoBuilder width(double maxWidth)
    {
        this.maxWidth = maxWidth;
        return this;
    }

    public StampInfoBuilder height(double maxHeight)
    {
        this.maxHeight = maxHeight;
        return this;
    }

    public StampInfoBuilder rotationAngle(double rotationAngle)
    {
        this.rotationAngle = rotationAngle;
        return this;
    }

    public StampInfo build(){
        return new StampInfo(x, y, maxWidth, maxHeight, rotationAngle);
    }
}
