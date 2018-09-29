//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imageGenerator.common.models.curves.HermiteCurve
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imageGenerator.common.models.curves;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.geom.Point2D;

public class HermiteCurve implements IParametricCurve{

    public static final RealMatrix MATRIX_H = MatrixUtils.createRealMatrix(new double[][] {
            { 2, -2,  1,  1},
            {-3,  3, -2, -1},
            { 0,  0,  1,  0},
            { 1,  0,  0,  0}
    });

    public final Point2D.Double start;
    public final Point2D.Double end;
    public final Point2D.Double tangentStart;
    public final Point2D.Double tangentEnd;

    private RealMatrix matrixHC;

    public HermiteCurve(Point2D.Double startIn, Point2D.Double endIn, Point2D.Double tangentStartIn, Point2D.Double tangentEndIn){
        start = startIn;
        end = endIn;
        tangentStart = tangentStartIn;
        tangentEnd = tangentEndIn;

        matrixHC = MatrixUtils.createRealMatrix(new double[][]{
                        {start.x, start.y},
                        {end.x, end.y},
                        {tangentStart.x, tangentStart.y},
                        {tangentEnd.x, tangentEnd.y}
        });
    }

    public HermiteCurve(Point2D.Double startIn, Point2D.Double endIn, double tangentDirStart, double tangentMagnitudeStart, double tangentDirEnd, double tangentMagnitudeEnd){
        start = startIn;
        end = endIn;
        tangentStart = new Point2D.Double(Math.abs(tangentMagnitudeStart) * Math.cos(tangentDirStart), Math.abs(tangentMagnitudeStart) * Math.sin(tangentDirStart));
        tangentEnd = new Point2D.Double(Math.abs(tangentMagnitudeEnd) * Math.cos(tangentDirEnd), Math.abs(tangentMagnitudeEnd) * Math.sin(tangentDirEnd));

        matrixHC = MatrixUtils.createRealMatrix(new double[][]{
                {start.x, start.y},
                {end.x, end.y},
                {tangentStart.x, tangentStart.y},
                {tangentEnd.x, tangentEnd.y}
        });
    }

    public Point2D.Double interpolate(double percent){
        RealMatrix matrixS = MatrixUtils.createRealMatrix(new double[][]{
                {Math.pow(percent, 3),
                 percent * percent,
                 percent,
                 1.0}
        });

        RealMatrix point = matrixS.multiply(MATRIX_H).multiply(matrixHC);//matrixS.multiply(MATRIX_H).multiply(matrixC);

        return new Point2D.Double(point.getEntry(0,0), point.getEntry(0, 1));
    }
}
