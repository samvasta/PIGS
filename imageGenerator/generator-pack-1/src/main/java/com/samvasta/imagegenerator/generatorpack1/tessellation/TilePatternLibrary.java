package com.samvasta.imagegenerator.generatorpack1.tessellation;

import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.*;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class TilePatternLibrary
{
    private static final Logger logger = Logger.getLogger(TilePatternLibrary.class);
    public static final double UNIT_SIDE_LENGTH = 1.0;

    /**
     * Singleton instance
     */
    public static final TilePatternLibrary INSTANCE = new TilePatternLibrary();

    private ArrayList<IPatternGenerator> patterns;

    public TilePattern getRandomPattern(RandomGenerator rand){
        int patternIdx = rand.nextInt(patterns.size());
        logger.debug("Chose pattern #" + patternIdx);
        IPatternGenerator patternGenerator = patterns.get(patternIdx);

        PatternGeneratorParameter[] parameters = patternGenerator.getParameters();
        double[] paramValues = new double[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            paramValues[i] = parameters[i].getRandValue(rand);
//            logger.warn("Tessellations are all using default parameters!");
//            paramValues[i] = parameters[i].defaultValue;
        }

        logger.debug(Arrays.toString(paramValues));
        return  patternGenerator.generatePattern(paramValues);
    }

    private TilePatternLibrary()
    {
        patterns = new ArrayList<>();
        //todo: create tile patterns
//        patterns.add(new N3_01());
//        patterns.add(new N3_02a());
//        patterns.add(new N3_02b());
//        patterns.add(new N3_18());
//        patterns.add(new P5_06());
//        patterns.add(new P5_16());
//        patterns.add(new RegularTrianglePattern());
//        patterns.add(new RegularQuadPattern());
//        patterns.add(new RegularHexagonPattern());
        patterns.add(new TruncatedTrihexagonalPattern());
    }

    public void addPattern(IPatternGenerator patternGenerator){
        patterns.add(patternGenerator);
    }

    /****************************************************
     * Helper Functions
     ****************************************************/
    public static Point2D.Double[] getRegularPolygonSideLength(int numSides, double sideLen, double angleOffset){
        double apothemLen = getRegPolyApothemLength(sideLen, numSides);
        double radius = Math.sqrt((apothemLen * apothemLen) + (sideLen * sideLen * 0.25));

        Point2D.Double[] points = new Point2D.Double[numSides];

        double dTheta = Math.PI * 2.0 / (double)numSides;
        double angle = angleOffset;
        for(int i = 0; i < numSides; i++){
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            points[i] = new Point2D.Double(x, y);
            angle += dTheta;
        }
        return points;
    }

    public static Point2D.Double[] getRegularPolygonRadius(int numSides, double radius, double angleOffset){
        Point2D.Double[] points = new Point2D.Double[numSides];

        double dTheta = Math.PI * 2.0 / (double)numSides;
        double angle = angleOffset;
        for(int i = 0; i < numSides; i++){
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            points[i] = new Point2D.Double(x, y);
            angle += dTheta;
        }
        return points;
    }

    public static double getRegPolyApothemLength(double sideLength, int numSides){
        return sideLength / (2.0 * Math.tan(Math.PI / (double)numSides));
    }
}
