package com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge;

import java.awt.geom.Point2D;

/**
 * Created by Sam on 7/6/2017.
 */
public class Vertex{
    private Point2D.Double pos;

    public Point2D.Double getPos(){
        return pos;
    }

    public void setPos(Point2D.Double pos){
        if(pos == null){
            throw new IllegalArgumentException("Point cannot be null");
        }
        this.pos = pos;
    }

    @Override
    public int hashCode(){
        return pos.hashCode();
    }
}
