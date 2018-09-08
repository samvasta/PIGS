package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;

import java.awt.geom.Point2D;

public class RegularHexagonPattern implements IPatternGenerator
{
    private static final double HEX_SIDE_LENGTH = TilePatternLibrary.UNIT_SIDE_LENGTH / 4.0;
    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return new PatternGeneratorParameter[0];
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        Point2D.Double[] hexPoints = TilePatternLibrary.getRegularPolygonSideLength(6, HEX_SIDE_LENGTH, 0);
        Tile hexagon = new Tile(hexPoints);

        double halfAngle = Math.PI / 6.0;
        double apothemLen = TilePatternLibrary.getRegPolyApothemLength(HEX_SIDE_LENGTH, 6);
        Point2D.Double[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(6, 2 * apothemLen, halfAngle);

        double[] neighborRotations = new double[] { 0,0,0,0,0,0 };
        return new TilePattern(new Tile[] { hexagon }, neighborCenters, neighborRotations);
    }
}
