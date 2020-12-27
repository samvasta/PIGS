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
    Map<Point, Point2D.Double> grid;
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

    public void placeVerticies(List<Point2D.Double> pointList, Rectangle bounds, RandomGenerator random)
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

                if(isValidLocation(candidate, bounds)){
                    pointList.add(candidate);
                    processingList.add(candidate);
                    addPointToGrid(candidate);
                }
            }

        }
    }

    private boolean isValidLocation(Point2D.Double candidate, Rectangle bounds){
        if(!bounds.contains(candidate)){
            return false;
        }

        int x = (int)(candidate.x / cellSize);
        int y = (int)(candidate.y / cellSize);

        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                try{
                    Point2D.Double cellPoint = grid.get(new Point(x+i, y+j));
                    if(cellPoint == null){
                        continue;
                    }
                    else if(cellPoint.distanceSq(candidate) < minDistance * minDistance){
                        return false;
                    }
                }
                catch(ArrayIndexOutOfBoundsException ex){
                    //do nothing, too lazy to properly avoid this. And besides,
                    //we only tried to access a cell that isn't there so no worries
                }
            }
        }

        return true;
    }

    private void addPointToGrid(Point2D.Double point){
        int x = (int)(point.x / cellSize);
        int y = (int)(point.y / cellSize);
        grid.put(new Point(x, y), point);
    }

}
