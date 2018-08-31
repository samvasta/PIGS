package com.samvasta.imagegenerator.generatorpack1.tessellation;

import java.awt.geom.Point2D;

public class Tile
{
    private Point2D.Double[] verticies;

    private Point2D.Double[] boundingBox;

    public Tile(Point2D.Double...verticiesIn){

        verticies = new Point2D.Double[verticiesIn.length];
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for(int i = 0; i < verticiesIn.length; i++){
            double x = verticiesIn[i].x;
            double y = verticiesIn[i].y;
            verticies[i] = new Point2D.Double(x, y);

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

        boundingBox = new Point2D.Double[] {
                                            new Point2D.Double(minX, minY),
                                            new Point2D.Double(minX, maxY),
                                            new Point2D.Double(maxX, maxY),
                                            new Point2D.Double(maxX, minY)
                                            };
    }

    public Point2D.Double[] getVerticies(){
        return verticies;
    }

    public Point2D.Double[] getBoundingBox(){
        return boundingBox;
    }
}
