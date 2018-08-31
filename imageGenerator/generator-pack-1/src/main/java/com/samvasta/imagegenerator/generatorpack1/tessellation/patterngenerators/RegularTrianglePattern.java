package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;


public class RegularTrianglePattern implements IPatternGenerator
{
    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return new PatternGeneratorParameter[0];
    }

    @Override
    public TilePattern generatePattern(double[] parameters) {
        PrecisePoint2D[] triPoints = TilePatternLibrary.getRegularPolygonSideLength(3, TilePatternLibrary.UNIT_SIDE_LENGTH, 0);
        Tile triangle = new Tile(triPoints);

        double halfAngle = Math.PI / 3.0;
        double apothemLen = TilePatternLibrary.getRegPolyApothemLength(TilePatternLibrary.UNIT_SIDE_LENGTH, 3);
        PrecisePoint2D[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(3, 2 * apothemLen, halfAngle);

        double[] neighborRotations = new double[] { Math.PI, Math.PI, Math.PI };
        return new TilePattern(new Tile[] { triangle }, neighborCenters, neighborRotations);
    }
}
