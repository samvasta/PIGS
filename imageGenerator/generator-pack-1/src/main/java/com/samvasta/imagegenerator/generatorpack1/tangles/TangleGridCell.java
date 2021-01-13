package com.samvasta.imagegenerator.generatorpack1.tangles;

import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imageGenerator.common.helpers.InterpHelper;
import com.samvasta.imageGenerator.common.models.grids.IGridCoordinate;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TangleGridCell {

    private final IGridCoordinate coordinate;

    public TangleGridCell(IGridCoordinate coordinateIn){
        coordinate = coordinateIn;
    }

    public IGridCoordinate getCoordinate() {
        return coordinate;
    }

    public Point2D[] getCellShapePoints(double sideLength, double angle, Point2D origin){
        return coordinate.getCellShapePoints(sideLength, angle, origin);
    }

    public IGridCoordinate[] getNeighborCoords(){
        return coordinate.getNeighbors();
    }

    public Point2D getScreenPoint(double sideLength, double angle, Point2D origin){
        return coordinate.toScreenPoint(sideLength, angle, origin);
    }

    public List<TubeSide> getConnectedPointsList(double sideLength, double angle, Point2D origin, double tubeWidthPercent){
        List<TubeSide> connectedPoints = new ArrayList<>(coordinate.getNumShapeSides());

        Point2D[] corners = coordinate.getCellShapePoints(sideLength, angle, origin);

        for(int i = 0; i < corners.length; i++){
            Point2D first = corners[i%corners.length];
            Point2D second = corners[(i+1)%corners.length];

            Point2D p1 = InterpHelper.lerp(first, second, (1.0-tubeWidthPercent)/2.0);
            Point2D p2 = InterpHelper.lerp(first, second, (1.0+tubeWidthPercent)/2.0);

            double tangent = GeomHelper.getAngleTo(first, second) - Math.PI/2.0;
            TubeSide side = new TubeSide(p1, p2, tangent);
            connectedPoints.add(side);
        }

        Collections.shuffle(connectedPoints);

        return connectedPoints;
    }
}
