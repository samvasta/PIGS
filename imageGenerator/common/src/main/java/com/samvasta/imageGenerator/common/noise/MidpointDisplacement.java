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

    public static Point2D.Double[] getMidpointDisplacement(double maxDeflectionFactor, RandomGenerator random, int numSteps, Point2D.Double...anchorPoints){
        if(numSteps < 0){
            throw new IllegalArgumentException("numSteps cannot be negative");
        }
        if(numSteps > 10){
            logger.warn("WARNING: Midpoint displacement with more than 10 steps is not recommended. The extra resolution is usually not detectable");
        }

        if(maxDeflectionFactor <= 0){
            throw new IllegalArgumentException("maxDeflectionFactor must be positive");
        }
        if(maxDeflectionFactor > 0.5){
            logger.warn("WARNING: Midpoint displacement with a maximum deflection factor greater than 0.5 will not give smooth results.");
        }

        if(numSteps == 0){
            //0 steps returns a line directly between start and end
            return anchorPoints;
        }

        int numPoints = getMidpointDisplacementLength(numSteps) * (anchorPoints.length-1);
        Point2D.Double[] points = new Point2D.Double[numPoints];

        int pointsIdx = 0;
        for(int i = 0; i < anchorPoints.length-1; i++){
            Point2D.Double start = anchorPoints[i];
            Point2D.Double end = anchorPoints[i+1];
            Point2D.Double[] segment = getMidpointDisplacement(start, end, maxDeflectionFactor, random, numSteps);
            for(int j = 0; j < segment.length; j++){
                points[pointsIdx + j] = segment[j];
            }
            pointsIdx += segment.length;
        }

        return points;
    }

    /**
     * Recursively applies a 1-D midpoint displacement algorithm between {@code start} and {@code end} points.
     * @param start Point to start at
     * @param end Point to end at
     * @param maxDeflectionFactor magnitude of deflection as a percent of the distance between the two points being deflected
     * @param random Random generator used to for midpoint deflection
     * @param numSteps number of recursive steps
     * @return An array of points in order between {@code start} and {@code end}, including the {@code start} and {@code end}
     * points at indexes 0 and {@code array.length-1} respectively
     */
    public static Point2D.Double[] getMidpointDisplacement(Point2D.Double start, Point2D.Double end, double maxDeflectionFactor, RandomGenerator random, int numSteps){
        if(numSteps < 0){
            throw new IllegalArgumentException("numSteps cannot be negative");
        }
        if(numSteps > 10){
            logger.warn("WARNING: Midpoint displacement with more than 10 steps is not recommended. The extra resolution is usually not detectable");
        }

        if(maxDeflectionFactor <= 0){
            throw new IllegalArgumentException("maxDeflectionFactor must be positive");
        }
        if(maxDeflectionFactor > 0.5){
            logger.warn("WARNING: Midpoint displacement with a maximum deflection factor greater than 0.5 will not give smooth results.");
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

    /**
     * Finds the midpoint between {@code p1} and {@code p2}, then deflects at a right angle to the line between {@code p1}
     * and {@code p2}.
     * @param p1
     * @param p2
     * @param deflectionFactor Amount to deflect the midpoint as a percent of the distance between {@code p1} and {@code p2}
     * @return A point equidistant from {@code p1} and {@code p2}
     */
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
