package com.samvasta.imageGenerator.common.graphics.vertexplacers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * Created by Sam on 7/5/2017.
 */
public class PoissonVertexPlacer implements IVertexPlacer
{
    public static final int K = 30;

    public double minDistance;
    double cellSize;
    int gridWidth;
    int gridHeight;
    Map<Point, List<Point2D.Double>> grid;
    ArrayList<Point2D.Double> processingList;

    public PoissonVertexPlacer(){
        this(0.5);
    }

    public PoissonVertexPlacer(double minDistanceIn){
        setMinDistance(minDistanceIn);
    }

    public double getMinDistance()
    {
        return minDistance;
    }

    public void setMinDistance(double minDistanceIn)
    {
        minDistance = minDistanceIn;
    }

    public void placeVertices(List<Point2D.Double> pointList, Rectangle bounds, RandomGenerator random)
    {
        cellSize = minDistance / (2d * Math.PI);
        gridWidth = (int)Math.ceil(bounds.getWidth() / cellSize);
        gridHeight = (int)Math.ceil(bounds.getHeight() / cellSize);
        grid = new HashMap<>();
        processingList = new ArrayList<>();

        Point2D.Double initialSample = new Point2D.Double(random.nextInt((int)bounds.getWidth()) + bounds.getMinX(), random.nextInt((int)bounds.getHeight()) + bounds.getMinY());
        pointList.add(initialSample);
        processingList.add(initialSample);
        addPointToGrid(initialSample);

        while(!processingList.isEmpty()){
            Point2D.Double point = processingList.remove(random.nextInt(processingList.size()));

            for(int i = 0; i < K; i++){
                double angle = 2d * Math.PI * random.nextDouble();
                double distance = random.nextDouble() * minDistance + minDistance;
                Point2D.Double candidate = new Point2D.Double(distance * Math.cos(angle) + point.x, distance * Math.sin(angle) + point.y);

                if(isValidLocation(candidate, bounds, pointList)){
                    pointList.add(candidate);
                    processingList.add(candidate);
                    addPointToGrid(candidate);
                }
            }

        }
    }

    private boolean isValidLocation(Point2D.Double candidate, Rectangle bounds, List<Point2D.Double> points){
        if(!bounds.contains(candidate)){
            return false;
        }

        for(Point2D.Double point : points) {
            double distSq = candidate.distanceSq(point);
            if(distSq < minDistance * minDistance){
                return false;
            }
        }
        return true;
    }

    private void addPointToGrid(Point2D.Double point){
        int x = (int)(point.x / cellSize);
        int y = (int)(point.y / cellSize);
        Point cell = new Point(x, y);
        if(!grid.containsKey(cell)) {
            grid.put(cell, new ArrayList<>());
        }
        grid.get(cell).add(point);
    }

}
