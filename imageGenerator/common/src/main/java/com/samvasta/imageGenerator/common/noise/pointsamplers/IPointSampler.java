package com.samvasta.imageGenerator.common.noise.pointsamplers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.geom.Point2D;

public interface IPointSampler
{
    Point2D.Double[] sample(double width, double height, int numPoints, RandomGenerator random);
}
