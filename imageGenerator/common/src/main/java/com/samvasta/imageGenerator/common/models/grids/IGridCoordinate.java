package com.samvasta.imageGenerator.common.models.grids;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;

import java.awt.geom.Point2D;

public interface IGridCoordinate {

    Point2D toScreenPoint(double sideLength, double angle, Point2D origin);
    Point2D[] getCellShapePoints(double sideLength, double angle, Point2D origin);

    double getAngleOffset();
    int getNumNeighbors();
    int getNumShapeSides();
    IGridCoordinate[] getNeighbors();
    Point2D getBoundingBoxMin(double sideLength, double angle, Point2D origin);
    Point2D getBoundingBoxMax(double sideLength, double angle, Point2D origin);

    boolean equals(IGridCoordinate other);
}
