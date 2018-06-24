package com.samvasta.imageGenerator.common.noise;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

public class MidpointDisplacement
{
    private static final Logger logger = Logger.getLogger(MidpointDisplacement.class);

    private static final double NINETY_DEGREES = Math.PI / 2d;

    public static final double DEFLECTION_FACTOR_VERY_HIGH = 0.25;
    public static final double DEFLECTION_FACTOR_HIGH = 0.125;
    public static final double DEFLECTION_FACTOR_MEDIUM = 0.0625;
    public static final double DEFLECTION_FACTOR_LOW = 0.03125;

    public static Point2D.Double[] getMidpointDisplacement(Point2D.Double start, Point2D.Double end, double maxDeflectionFactor, RandomGenerator random, int numSteps){
        if(numSteps < 0){
            throw new IllegalArgumentException("numSteps cannot be negative");
        }
        if(numSteps > 10){
            logger.warn("Midpoint displacement with more than 10 steps is not recommended. The extra resolution is usually not detectable");
        }

        if(numSteps == 0){
            //0 steps returns a line directly between start and end
            return new Point2D.Double[] { start, end };
        }

        int numPoints = getMidpointDisplacementLength(numSteps);
        Point2D.Double[] points = new Point2D.Double[numPoints];
        points[0] = start;
        points[points.length-1] = end;

        populateMidpointDisplacement_recursive(points, maxDeflectionFactor, random, numSteps);

        return points;
    }

    private static void populateMidpointDisplacement_recursive(Point2D.Double[] arr, double maxDeflectionFactor, RandomGenerator random, int stepNum){
        if(stepNum == 0){
            return;
        }
        else{
            //skip to every 2^(stepNum-1) index
            int arrStepSize = (int)Math.pow(2 , stepNum);
            int startIdx = (int)Math.pow(2 , stepNum-1);

            if(arr.length == getMidpointDisplacementLength(stepNum)){
                arrStepSize = (int)Math.pow(2 , stepNum-1);
                startIdx = arrStepSize;
            }
            //happens to always be the same
            int lookupDelta = startIdx;


            for(int idx = startIdx; idx < arr.length-1; idx += arrStepSize){
                arr[idx] = deflect(arr[idx - lookupDelta], arr[idx+lookupDelta], maxDeflectionFactor * random.nextGaussian());
            }

            populateMidpointDisplacement_recursive(arr, maxDeflectionFactor, random, stepNum-1);
        }

    }

    public static Point2D.Double deflect(Point2D.Double p1, Point2D.Double p2, double deflectionFactor){
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.atan2(dy, dx);
        angle += NINETY_DEGREES;

        double midX = p2.x + (dx / 2d);
        double midY = p2.y + (dy / 2d);

        midX += distance * deflectionFactor * Math.cos(angle);
        midY += distance * deflectionFactor * Math.sin(angle);
        return new Point2D.Double(midX, midY);
    }

    private static int getMidpointDisplacementLength(int numRecursionSteps){
        if(numRecursionSteps == 1){
            //special case. After step 1, it follows the pattern 2^n + 1
            return 3;
        }
        return (2 << (numRecursionSteps-1)) + 1;
    }
}
