package com.samvasta.imagegenerator.generatorpack1.tangles;

import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imageGenerator.common.helpers.InterpHelper;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TubeSide {
    private final Point2D firstClockwiseCorner;
    private final Point2D lastClockwiseCorner;
    private final double angle;

    public TubeSide(Point2D firstClockwiseCornerIn, Point2D lastClockwiseCornerIn, double angleIn){
        firstClockwiseCorner = firstClockwiseCornerIn;
        lastClockwiseCorner = lastClockwiseCornerIn;
        angle = angleIn;
    }

    public List<PointAndAngle> getTubePoints(double tubeWidthPercent){
        List<PointAndAngle> tubePoints = new ArrayList<>(2);

        Point2D p1 = InterpHelper.lerp(firstClockwiseCorner, lastClockwiseCorner, (1.0+tubeWidthPercent)/2.0);
        Point2D p2 = InterpHelper.lerp(firstClockwiseCorner, lastClockwiseCorner, (1.0-tubeWidthPercent)/2.0);

        double tangent = GeomHelper.getAngleTo(firstClockwiseCorner, lastClockwiseCorner) - Math.PI/2.0;
        tubePoints.add(new PointAndAngle(p1, tangent));
        tubePoints.add(new PointAndAngle(p2, tangent));
        return tubePoints;
    }

    public Point2D getMidPoint(){
        return InterpHelper.lerp(firstClockwiseCorner, lastClockwiseCorner, 0.5);
    }

    public double getAngle(){
        return angle;
    }

    public Point2D getFirstClockwiseCorner(){
        return firstClockwiseCorner;
    }
    public Point2D getLastClockwiseCorner(){
        return lastClockwiseCorner;
    }
}
