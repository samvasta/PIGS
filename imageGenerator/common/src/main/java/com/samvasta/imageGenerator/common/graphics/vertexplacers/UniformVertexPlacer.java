package com.samvasta.imageGenerator.common.graphics.vertexplacers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created by Sam on 7/5/2017.
 */
public class UniformVertexPlacer implements IVertexPlacer
{
    private int numVerticies;

    public UniformVertexPlacer(){
        this(10);
    }

    public UniformVertexPlacer(int numVerticiesIn){
        setNumVerticies(numVerticiesIn);
    }

    public void setNumVerticies(int numVerticiesIn){
        numVerticies = numVerticiesIn;
    }

    public int getNumVerticies(){
        return numVerticies;
    }

    public void placeVerticies(List<Point2D.Double> pointList, Rectangle bounds, RandomGenerator random)
    {
        for(int i = 0; i < numVerticies; i++){
            pointList.add(new Point2D.Double(random.nextInt((int)bounds.getWidth()) + bounds.getMinX(), random.nextInt((int)bounds.getHeight()) + bounds.getMinY()));
        }
    }
}
