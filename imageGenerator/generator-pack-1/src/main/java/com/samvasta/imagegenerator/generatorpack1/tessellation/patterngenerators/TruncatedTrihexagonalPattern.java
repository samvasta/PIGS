package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;

import java.awt.geom.Point2D;

public class TruncatedTrihexagonalPattern implements IPatternGenerator {
    private static final double DODECA_SIDE_LENGTH = TilePatternLibrary.UNIT_SIDE_LENGTH / 6.0;

    @Override
    public PatternGeneratorParameter[] getParameters() {
        return new PatternGeneratorParameter[0];
    }

    @Override
    public TilePattern generatePattern(double[] parameters) {
        double halfAngle = Math.PI / 12.0;
        double dodecaApothemLength = TilePatternLibrary.getRegPolyApothemLength(DODECA_SIDE_LENGTH, 12);
        double squareApothemLength = TilePatternLibrary.getRegPolyApothemLength(DODECA_SIDE_LENGTH, 4);
        double hexApothemLength = TilePatternLibrary.getRegPolyApothemLength(DODECA_SIDE_LENGTH, 6);

        Point2D.Double[] dodecaPoints = TilePatternLibrary.getRegularPolygonSideLength(12, DODECA_SIDE_LENGTH, halfAngle);
        Tile dodecagon = new Tile(dodecaPoints);

        //Hex and squares counter-clockwise order starting from the square on the bottom
        Point2D.Double[] sq1Points = TilePatternLibrary.getRegularPolygonSideLength(4, DODECA_SIDE_LENGTH, Math.PI/4.0);
        sq1Points = GeomHelper.addPolar(sq1Points, dodecaApothemLength + squareApothemLength, Math.PI/2);
        Tile sq1 = new Tile(sq1Points);

        Point2D.Double[] hex1Points = TilePatternLibrary.getRegularPolygonSideLength(6, DODECA_SIDE_LENGTH, Math.PI/2.0);
        hex1Points = GeomHelper.addPolar(hex1Points, dodecaApothemLength + hexApothemLength, Math.PI/3);
        Tile hex1 = new Tile(hex1Points);

        Point2D.Double[] sq2Points = TilePatternLibrary.getRegularPolygonSideLength(4, DODECA_SIDE_LENGTH, Math.PI/6.0  + Math.PI/4.0);
        sq2Points = GeomHelper.addPolar(sq2Points, dodecaApothemLength + squareApothemLength, Math.PI/6);
        Tile sq2 = new Tile(sq2Points);

        Point2D.Double[] hex2Points = TilePatternLibrary.getRegularPolygonSideLength(6, DODECA_SIDE_LENGTH, Math.PI/2.0);
        hex2Points = GeomHelper.addPolar(hex2Points, dodecaApothemLength+hexApothemLength, 0);
        Tile hex2 = new Tile(hex2Points);

        Point2D.Double[] sq3Points = TilePatternLibrary.getRegularPolygonSideLength(4, DODECA_SIDE_LENGTH, -Math.PI/6.0 + Math.PI/4.0);
        sq3Points = GeomHelper.addPolar(sq3Points, dodecaApothemLength + squareApothemLength, -Math.PI/6);
        Tile sq3 = new Tile(sq3Points);

        Point2D.Double[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(6, 2 * dodecaApothemLength + DODECA_SIDE_LENGTH, Math.PI/6);
        double[] neighborRotations = new double[]{0, 0, 0, 0, 0, 0};


        return new TilePattern(new Tile[]{dodecagon, hex1, hex2, sq1, sq2, sq3}, neighborCenters, neighborRotations);
    }
}
