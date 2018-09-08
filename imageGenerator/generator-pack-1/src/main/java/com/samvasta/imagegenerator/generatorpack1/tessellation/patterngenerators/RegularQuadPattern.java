package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;

import java.awt.geom.Point2D;


public class RegularQuadPattern implements IPatternGenerator
{
    private static final double QUAD_SIDE_LENGTH = TilePatternLibrary.UNIT_SIDE_LENGTH / 2.0;
    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return new PatternGeneratorParameter[0];
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        Point2D.Double[] squarePoints = TilePatternLibrary.getRegularPolygonSideLength(4, QUAD_SIDE_LENGTH, 0);
        Tile square = new Tile(squarePoints);

        double halfAngle = Math.PI / 4.0;
        double apothemLen = TilePatternLibrary.getRegPolyApothemLength(QUAD_SIDE_LENGTH, 4);
        Point2D.Double[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(4, 2 * apothemLen, halfAngle);

        double[] neighborRotations = new double[] { 0,0,0,0 };
        return new TilePattern(new Tile[] { square }, neighborCenters, neighborRotations);
    }
}
