package com.samvasta.imageGenerator.common.noise.pointsamplers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.geom.Point2D;

public class RandomPointSampler implements IPointSampler
{
    @Override
    public Point2D.Double[] sample(double width, double height, int numPoints, RandomGenerator random)
    {
        Point2D.Double[] points = new Point2D.Double[numPoints];

        for(int i = 0; i < numPoints; i++){
            points[i] = new Point2D.Double(random.nextDouble() * width, random.nextDouble() * height);
        }

        return points;
    }
}
