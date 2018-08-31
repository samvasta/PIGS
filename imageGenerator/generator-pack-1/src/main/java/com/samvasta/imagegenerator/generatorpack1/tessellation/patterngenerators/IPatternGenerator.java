package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;

public interface IPatternGenerator
{
    PatternGeneratorParameter[] getParameters();

    //Template for generatePattern()
    /*
        Point2D.Double[] points = new Point2D.Double[]{};
        Tile tile = new Tile(new Point2D.Double(), points);

        Point2D.Double[] neighborCenters = new Point2D.Double[]{};

        double[] neighborRotations = new double[] {};
        return new TilePattern(new Tile[] { tile }, neighborCenters, neighborRotations);
     */
    TilePattern generatePattern(double[] parameters);
}
