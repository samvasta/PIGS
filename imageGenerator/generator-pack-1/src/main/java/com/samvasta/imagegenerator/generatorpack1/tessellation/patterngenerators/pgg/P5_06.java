package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.pgg;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.IPatternGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.PatternGeneratorParameter;

import java.awt.geom.Point2D;

public class P5_06 implements IPatternGenerator
{
    private static final PatternGeneratorParameter[] PARAMETERS = new PatternGeneratorParameter[]{
            new PatternGeneratorParameter(-MathHelper.DEG90 + MathHelper.DEG01, MathHelper.DEG90 - MathHelper.DEG01, MathHelper.DEG01*10),
            new PatternGeneratorParameter(0, 1, 0.4),
            new PatternGeneratorParameter(0, 1, 0.6),
            new PatternGeneratorParameter(-0.25, 2.5, 0.1)
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

        Tile[] tiles = new Tile[4];

        tiles[0] = getBasicTile(shapeDetails, 0, 0, 0);

        double offsetX = 2*shapeDetails.lenAB * Math.cos(parameters[0]) + shapeDetails.lenBC * Math.cos(shapeDetails.anglePointBToPointC);
        double offsetY = 2*shapeDetails.lenAB * Math.sin(parameters[0]) + shapeDetails.lenBC * Math.sin(shapeDetails.anglePointBToPointC);
        tiles[1] = getBasicTile(shapeDetails, offsetX, offsetY, Math.PI);

        offsetX += shapeDetails.lenDE * Math.cos(Math.PI/2.0 - shapeDetails.angleEAD);
        offsetY += shapeDetails.lenDE * Math.sin(Math.PI/2.0 - shapeDetails.angleEAD);
        tiles[2] = getBasicTile(shapeDetails, offsetX, offsetY - shapeDetails.height, 0);

        offsetX += shapeDetails.lenAB * Math.cos(parameters[0]) + shapeDetails.lenCD * Math.cos(parameters[0]);
        offsetY += shapeDetails.lenAB * Math.sin(parameters[0]) + shapeDetails.lenCD * Math.sin(parameters[0]);
        tiles[3] = getBasicTile(shapeDetails, offsetX, offsetY, Math.PI);

        Point2D.Double[] neighborCenters = new Point2D.Double[]{
                new Point2D.Double(0, shapeDetails.height),
                new Point2D.Double(2*shapeDetails.rectWidth*Math.cos(parameters[0]) + 2*parameters[3], (2*shapeDetails.rectWidth)*Math.sin(parameters[0])),
                new Point2D.Double(-(2*shapeDetails.rectWidth*Math.cos(parameters[0]) + 2*parameters[3]), -(2*shapeDetails.rectWidth)*Math.sin(parameters[0])),
                new Point2D.Double(0, -shapeDetails.height)
        };


        double[] neighborRotations = new double[6];

        return new TilePattern(tiles, neighborCenters, neighborRotations);
    }

    private Tile getBasicTile(ShapeDetails shapeDetails, double xOffset, double yOffset, double rotation){
        Point2D.Double[] verts = new Point2D.Double[5];

        verts[0] = new Point2D.Double(xOffset + shapeDetails.aX, yOffset + shapeDetails.aY);

        double dstX = shapeDetails.bX * Math.cos(rotation) - shapeDetails.bY * Math.sin(rotation);
        double dstY = shapeDetails.bX * Math.sin(rotation) + shapeDetails.bY * Math.cos(rotation);
        verts[1] = new Point2D.Double(xOffset + dstX, yOffset + dstY);


        dstX = shapeDetails.cX * Math.cos(rotation) - shapeDetails.cY * Math.sin(rotation);
        dstY = shapeDetails.cX * Math.sin(rotation) + shapeDetails.cY * Math.cos(rotation);
        verts[2] = new Point2D.Double(xOffset + dstX, yOffset + dstY);


        dstX = shapeDetails.dX * Math.cos(rotation) - shapeDetails.dY * Math.sin(rotation);
        dstY = shapeDetails.dX * Math.sin(rotation) + shapeDetails.dY * Math.cos(rotation);
        verts[3] = new Point2D.Double(xOffset + dstX, yOffset + dstY);


        dstX = shapeDetails.eX * Math.cos(rotation) - shapeDetails.eY * Math.sin(rotation);
        dstY = shapeDetails.eX * Math.sin(rotation) + shapeDetails.eY * Math.cos(rotation);
        verts[4] = new Point2D.Double(xOffset + dstX, yOffset + dstY);

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
        final double lenCD;
        final double lenDE;
        final double lenAE;

        final double angleA;
        final double angleB;
        final double angleC;
        final double angleD;
        final double angleE;

        final double anglePointBToPointC;
        final double angleEAD;

        final double height;
        final double rectWidth;

        ShapeDetails(double[] parameters){
            height = parameters[1];
            rectWidth = TilePatternLibrary.UNIT_SIDE_LENGTH - height;
            lenAB = rectWidth * parameters[2];
            double ln5 = parameters[3];

            aX = 0;
            aY = 0;

            bX = lenAB * Math.cos(parameters[0]);
            bY = lenAB * Math.sin(parameters[0]);

            cX = (rectWidth - lenAB) * Math.cos(parameters[0]);
            cY = height + (rectWidth - lenAB) * Math.sin(parameters[0]);

            dX = 0;
            dY = height;

            eX = -ln5;
            eY = height / 2.0;

            lenBC = Math.sqrt((cX - bX) * (cX - bX) + (cY - bY) * (cY - bY));
            lenCD = Math.sqrt((cX - dX) * (cX - dX) + (cY - dY) * (cY - dY));
            lenDE = Math.sqrt((eX - dX) * (eX - dX) + (eY - dY) * (eY - dY));
            lenAE = Math.sqrt((eX - aX) * (eX - aX) + (eY - aY) * (eY - aY));

            angleE = 2*Math.atan2(height*0.5, ln5);
            anglePointBToPointC = Math.atan2(cY-bY, cX-bX);
            angleB = parameters[0] + (Math.PI - anglePointBToPointC);

            angleEAD = (Math.PI - angleE)/2.0;
            angleA = angleEAD + (Math.PI/2.0 - parameters[0]);
            angleD = 2*Math.PI - angleA - angleE;

            angleC = Math.PI - angleB;

        }
    }
}
