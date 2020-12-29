package com.samvasta.imageGenerator.common.models.curves;

import java.awt.geom.Point2D;

public class HermiteCurve implements IParametricCurve{

    public final Point2D.Double start;
    public final Point2D.Double end;
    public final Point2D.Double tangentStart;
    public final Point2D.Double tangentEnd;

    private final double[] hC;

    public HermiteCurve(Point2D.Double startIn, Point2D.Double endIn, Point2D.Double tangentStartIn, Point2D.Double tangentEndIn){
        start = startIn;
        end = endIn;
        tangentStart = tangentStartIn;
        tangentEnd = tangentEndIn;

        hC = computeHC(start, end, tangentStart, tangentEnd);
    }

    public HermiteCurve(Point2D.Double startIn, Point2D.Double endIn, double tangentDirStart, double tangentMagnitudeStart, double tangentDirEnd, double tangentMagnitudeEnd){
        start = startIn;
        end = endIn;
        tangentStart = new Point2D.Double(Math.abs(tangentMagnitudeStart) * Math.cos(tangentDirStart), Math.abs(tangentMagnitudeStart) * Math.sin(tangentDirStart));
        tangentEnd = new Point2D.Double(Math.abs(tangentMagnitudeEnd) * Math.cos(tangentDirEnd), Math.abs(tangentMagnitudeEnd) * Math.sin(tangentDirEnd));

        hC = computeHC(start, end, tangentStart, tangentEnd);
    }

    private double[] computeHC(Point2D.Double start, Point2D.Double end, Point2D.Double tangentStart, Point2D.Double tangentEnd) {
        /* If you're reading this, the knowledge of how it works has probably been lost to time and it's is probably magic now. But maybe this will help:
        "h times C" is referencing the multiplication of two matrices called "h" and "C"
        h and C are names specific to the domain of hermite curves. look it up.
        https://www.cubic.org/docs/hermite.htm

        Multiply

                    H                     C
            { 2, -2,  1,  1}     {start.x , start.y }
            {-3,  3, -2, -1}  X  {end.x   , end.y   }
            { 0,  0,  1,  0}     {tStart.x, tStart.y}
            { 1,  0,  0,  0}     {tEnd.x  , tEnd.y  }

        I'm intentionally not using matrix libraries because they are super slow
         */

        double[] matrix = new double[8];

        //Row 1
        matrix[0] = start.x * 2 + end.x * -2 + tangentStart.x * 1 + tangentEnd.x * 1;
        matrix[4] = start.y * 2 + end.y * -2 + tangentStart.y * 1 + tangentEnd.y * 1;

        //Row 2
        matrix[1] = start.x * -3 + end.x * 3 + tangentStart.x * -2 + tangentEnd.x * -1;
        matrix[5] = start.y * -3 + end.y * 3 + tangentStart.y * -2 + tangentEnd.y * -1;

        //Row 3
        matrix[2] = tangentStart.x;
        matrix[6] = tangentStart.y;

        //Row 4
        matrix[3] = start.x;
        matrix[7] = start.y;

        return matrix;
    }

    public Point2D.Double interpolate(double percent){
        // Multiply the 4row x 2col "hC" matrix with a 4x1 vector ("S") to get the 1x2 (x,y) vector

        /* If you're reading this, the knowledge of how it works has probably been lost to time and it's is probably magic now. But maybe this will help:
        "ShC" is referencing the multiplication of two matrices called "S" and "hC"
        S, h, and C are names specific to the domain of hermite curves. look it up.
        https://www.cubic.org/docs/hermite.htm

        Multiply

                  S              hC
            { percent^3 }     { ?, ? }
            { percent^2 }  X  { ?, ? }
            { percent^1 }     { ?, ? }
            { percent^0 }     { ?, ? }

        I'm intentionally not using matrix libraries because they are super slow
         */
        double v0 = Math.pow(percent, 3);
        double v1 = percent * percent;
        double v2 = percent;
        double v3 = 1.0;

        double x = hC[0] * v0 +
                hC[1] * v1 +
                hC[2] * v2 +
                hC[3] * v3;

        double y = hC[4] * v0 +
                hC[5] * v1 +
                hC[6] * v2 +
                hC[7] * v3;
        return new Point2D.Double(x, y);
    }
}
