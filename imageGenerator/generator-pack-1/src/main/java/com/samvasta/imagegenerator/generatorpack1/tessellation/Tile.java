package com.samvasta.imagegenerator.generatorpack1.tessellation;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;

public class Tile
{
    private PrecisePoint2D[] verticies;

    private PrecisePoint2D[] boundingBox;

    public Tile(PrecisePoint2D...verticiesIn){

        verticies = new PrecisePoint2D[verticiesIn.length];
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for(int i = 0; i < verticiesIn.length; i++){
            double x = verticiesIn[i].getX();
            double y = verticiesIn[i].getY();
            verticies[i] = new PrecisePoint2D(x, y);

            if(x < minX){
                minX = x;
            }
            if(x > maxX){
                maxX = x;
            }

            if(y < minY){
                minY = y;
            }
            if(y > maxY){
                maxY = y;
            }
        }

        boundingBox = new PrecisePoint2D[] {
                                            new PrecisePoint2D(minX, minY),
                                            new PrecisePoint2D(minX, maxY),
                                            new PrecisePoint2D(maxX, maxY),
                                            new PrecisePoint2D(maxX, minY)
                                            };
    }

    public PrecisePoint2D[] getVerticies(){
        return verticies;
    }

    public PrecisePoint2D[] getBoundingBox(){
        return boundingBox;
    }
}
