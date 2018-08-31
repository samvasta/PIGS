package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.p6;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.models.PrecisePoint2D;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.IPatternGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.PatternGeneratorParameter;

public class P5_16 implements IPatternGenerator
{
    private static final double SQRT3OVER6 = Math.sqrt(3.0)/6.0;

    private static final PatternGeneratorParameter[] PARAMETERS = new PatternGeneratorParameter[]{
            new PatternGeneratorParameter(0, Math.PI * 2 - MathHelper.DEG90, MathHelper.DEG120),
            new PatternGeneratorParameter(0, 2.0, 1.0)
    };

    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return PARAMETERS;
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        ShapeDetails shapeDetails = new ShapeDetails(parameters);

        Tile[] tiles = new Tile[6];

        tiles[0] = getBasicTile(shapeDetails, 0, 0, 0*MathHelper.DEG60);
        tiles[1] = getBasicTile(shapeDetails, 0, 0, 1*MathHelper.DEG60);
        tiles[2] = getBasicTile(shapeDetails, 0, 0, 2*MathHelper.DEG60);
        tiles[3] = getBasicTile(shapeDetails, 0, 0, 3*MathHelper.DEG60);
        tiles[4] = getBasicTile(shapeDetails, 0, 0, 4*MathHelper.DEG60);
        tiles[5] = getBasicTile(shapeDetails, 0, 0, 5*MathHelper.DEG60);

        double neighborOffsetX = shapeDetails.cX + shapeDetails.bX;
        double neighborOffsetY = shapeDetails.cY + shapeDetails.bY;
        double neighborRadius = Math.sqrt(neighborOffsetX * neighborOffsetX + neighborOffsetY * neighborOffsetY);
        double angleOffset = Math.atan2(neighborOffsetY, neighborOffsetX);

        PrecisePoint2D[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(6, neighborRadius, angleOffset);


        double[] neighborRotations = new double[6];

        return new TilePattern(tiles, neighborCenters, neighborRotations);
    }

    private Tile getBasicTile(ShapeDetails shapeDetails, double xOffset, double yOffset, double rotation){
        PrecisePoint2D[] verts = new PrecisePoint2D[5];

        verts[0] = new PrecisePoint2D(xOffset + shapeDetails.aX, yOffset + shapeDetails.aY);

        double dstX = shapeDetails.bX * Math.cos(rotation) - shapeDetails.bY * Math.sin(rotation);
        double dstY = shapeDetails.bX * Math.sin(rotation) + shapeDetails.bY * Math.cos(rotation);
        verts[1] = new PrecisePoint2D(xOffset + dstX, yOffset + dstY);


        dstX = shapeDetails.cX * Math.cos(rotation) - shapeDetails.cY * Math.sin(rotation);
        dstY = shapeDetails.cX * Math.sin(rotation) + shapeDetails.cY * Math.cos(rotation);
        verts[2] = new PrecisePoint2D(xOffset + dstX, yOffset + dstY);


        dstX = shapeDetails.dX * Math.cos(rotation) - shapeDetails.dY * Math.sin(rotation);
        dstY = shapeDetails.dX * Math.sin(rotation) + shapeDetails.dY * Math.cos(rotation);
        verts[3] = new PrecisePoint2D(xOffset + dstX, yOffset + dstY);


        dstX = shapeDetails.eX * Math.cos(rotation) - shapeDetails.eY * Math.sin(rotation);
        dstY = shapeDetails.eX * Math.sin(rotation) + shapeDetails.eY * Math.cos(rotation);
        verts[4] = new PrecisePoint2D(xOffset + dstX, yOffset + dstY);

        return new Tile(verts);
    }

    private class ShapeDetails{
        final double aX;
        final double aY;
        final double bX;
        final double bY;
        final double cX;
        final double cY;
        final double dX;
        final double dY;
        final double eX;
        final double eY;

        final double lenAB;
        final double lenBC;
//        final double lenCD;
//        final double lenDE;
//        final double lenAE;
//
//        final double angleA;
//        final double angleB;
//        final double angleC;
//        final double angleD;
//        final double angleE;

        ShapeDetails(double[] parameters){

            lenAB = TilePatternLibrary.UNIT_SIDE_LENGTH;
            lenBC = parameters[1]*0.5;

            aX = 0;
            aY = 0;

            bX = lenAB;
            bY = 0;

            cX = lenAB - lenBC * Math.cos(parameters[0]);
            cY = lenBC * Math.sin(parameters[0]);

            dX = lenAB - lenBC*Math.cos(parameters[0])/2.0 - cY*SQRT3OVER6;
            dY = SQRT3OVER6*2.0 + cY/2.0 - lenBC*Math.cos(parameters[0])*SQRT3OVER6;

            eX = lenAB * 0.5;
            eY = 3*SQRT3OVER6;

        }
    }
}
