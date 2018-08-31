package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;


public class RegularQuadPattern implements IPatternGenerator
{
    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return new PatternGeneratorParameter[0];
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        PrecisePoint2D[] squarePoints = TilePatternLibrary.getRegularPolygonSideLength(4, TilePatternLibrary.UNIT_SIDE_LENGTH, 0);
        Tile square = new Tile(squarePoints);

        double halfAngle = Math.PI / 4.0;
        double apothemLen = TilePatternLibrary.getRegPolyApothemLength(TilePatternLibrary.UNIT_SIDE_LENGTH, 4);
        PrecisePoint2D[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(4, 2 * apothemLen, halfAngle);

        double[] neighborRotations = new double[] { 0,0,0,0 };
        return new TilePattern(new Tile[] { square }, neighborCenters, neighborRotations);
    }
}