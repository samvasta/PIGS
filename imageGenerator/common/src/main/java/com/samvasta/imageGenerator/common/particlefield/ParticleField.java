package com.samvasta.imageGenerator.common.particlefield;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.models.PolarVector;

public class ParticleField
{
    private PolarVector[] fieldValues;
    private final int width;
    private final int height;

    private boolean isEdgeWrap;

    public ParticleField(int width, int height){
        this.width = width;
        this.height = height;
        isEdgeWrap = true;
    }

    public ParticleField(int width, int height, PolarVector[] fieldValues){
        this(width, height);
        setFieldValues(fieldValues);
    }

    public void setEdgeWrap(boolean isEdgeWrap){
        this.isEdgeWrap = isEdgeWrap;
    }

    public void setFieldValues(PolarVector[] fieldValues){
        if(fieldValues.length != width * height){
            throw new IllegalArgumentException("fieldValues must have a length equal to width * height (" + (width * height) + ")");
        }
        this.fieldValues = fieldValues;
    }

    public ParticleFieldValue getValue(double x, double y){
        int xFloor, xCeil;
        int yFloor, yCeil;

        if(isEdgeWrap){
            while(x < 0){
                x += width;
            }
            while(x > width){
                x -= width;
            }
        }
        else{
            x = Math.min(width, Math.max(0, x));
        }

        xFloor = (int)Math.floor(x);
        xCeil = xFloor + 1;
        if(xCeil >= width){
            xCeil = 0;
        }

        if(isEdgeWrap){
            while(y < 0){
                y += height;
            }
            while(y > height){
                y -= height;
            }
        }
        else{
            y = Math.min(height, Math.max(0, y));
        }

        yFloor = (int)Math.floor(y);
        yCeil = yFloor + 1;
        if(yCeil >= height){
            yCeil = 0;
        }

        double xPercent = x - xFloor;
        double yPercent = y - yFloor;
        PolarVector tl = getFieldValue(xFloor, yFloor);

        PolarVector tr = getFieldValue(xCeil, yFloor);
        PolarVector bl = getFieldValue(xFloor, yCeil);
        PolarVector br = getFieldValue(xCeil, yCeil);

        double angle = MathHelper.lerp2d(tl.angle, tr.angle, bl.angle, br.angle, xPercent, yPercent);
        double magnitude = MathHelper.lerp2d(tl.magnitude, tr.magnitude, bl.magnitude, br.magnitude, xPercent, yPercent);

        return new ParticleFieldValue(x, y, angle, magnitude);
    }

    private PolarVector getFieldValue(int x, int y){
        return fieldValues[x + y * width];
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    public boolean isEdgeWrap(){
        return isEdgeWrap;
    }
}
