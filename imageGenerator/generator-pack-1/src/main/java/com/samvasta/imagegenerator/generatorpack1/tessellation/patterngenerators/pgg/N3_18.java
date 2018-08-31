package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.pgg;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.IPatternGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.PatternGeneratorParameter;

import java.awt.geom.Point2D;

public class N3_18 implements IPatternGenerator
{
    private static final PatternGeneratorParameter[] PARAMETERS = new PatternGeneratorParameter[]{
        new PatternGeneratorParameter(MathHelper.DEG30, MathHelper.DEG120, MathHelper.DEG60)
    };

    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return PARAMETERS;
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        final double angleParam = parameters[0];

        final double angleA = angleParam;
        final double angleB = (Math.PI - angleA) * 2.0 / 3.0;
        final double angleC = Math.PI - angleA - angleB;

        double widthA = 1.0/Math.tan(angleA);
        double widthB = 1.0/Math.tan(angleB);
        double totalWidth = Math.max(Math.max(Math.max(0, widthA), widthB), widthA + widthB) - Math.min(Math.min(Math.min(0, widthA), widthB), widthA + widthB);

        widthA = TilePatternLibrary.UNIT_SIDE_LENGTH * (widthA / totalWidth);
        widthB = TilePatternLibrary.UNIT_SIDE_LENGTH * (widthB / totalWidth);

        double lenAB = widthA + widthB;
        double lenAC;
        if(widthA < 0){
            lenAC = widthA / Math.cos(Math.PI - angleA);
        }
        else{
            lenAC = widthA / Math.cos(angleA);
        }
        lenAC = Math.abs(lenAC);

        double lenBC;
        if(widthB < 0){
            lenBC = widthB / Math.cos(Math.PI - angleB);
        }
        else{
            lenBC = widthB / Math.cos(angleB);
        }
        lenBC = Math.abs(lenBC);

        double height = lenAC * Math.sin(Math.PI - angleA);

        Tile[] tiles = new Tile[12];

        tiles[0] = new Tile(getReflectedTile(widthA, widthB, height, 0, 0, 0));

        tiles[1] = new Tile(getReflectedTile(widthA, widthB, height, Math.PI, lenAC * Math.cos(Math.PI - angleA), lenAC * Math.sin(Math.PI - angleA)));

        tiles[2] = new Tile(getBasicTile(widthA, widthB, height, Math.PI - angleA + angleC, lenAC * Math.cos(angleC),lenAC * Math.sin(angleC)));

        tiles[3] = new Tile(getBasicTile(widthA, widthB, height,  - angleA - angleC, lenAC * Math.cos(angleC) + lenAB * Math.cos(angleB), lenAC * Math.sin(angleC) + lenAB * Math.sin(angleB)));

        tiles[4] = new Tile(getBasicTile(widthA, widthB, height, - angleA - angleC + angleB, lenBC * Math.cos(angleB), lenBC * Math.sin(angleB)));

        tiles[5] = new Tile(getReflectedTile(widthA, widthB, height, Math.PI + angleA + angleC, lenBC * Math.cos(angleB), lenBC * Math.sin(angleB)));

        tiles[6] = new Tile(getReflectedTile(widthA, widthB, height, angleA + angleC, lenBC * Math.cos(angleB) + lenAB * Math.cos(Math.PI - angleB), lenBC * Math.sin(angleB) + lenAB * Math.sin(Math.PI - angleB)));

        tiles[7] = new Tile(getBasicTile(widthA, widthB, height, -Math.PI + 2.0*angleB, -lenAB + lenAB * Math.cos(2*angleB), lenAB * Math.sin(2*angleB)));

        tiles[8] = new Tile(getBasicTile(widthA, widthB, height, Math.PI - angleA - angleC, -lenAB + lenAB * Math.cos(2*angleB) - lenAB * Math.cos(Math.PI - angleA - angleC), lenAB * Math.sin(2*angleB) - lenAB * Math.sin(Math.PI - angleA - angleC)));

        tiles[9] = new Tile(getBasicTile(widthA, widthB, height, 2 * angleB, -lenAB, 0));

        tiles[10] = new Tile(getReflectedTile(widthA, widthB, height, angleA + angleC, -lenAB, 0));

        tiles[11] = new Tile(getReflectedTile(widthA, widthB, height, Math.PI + angleA + angleC,-lenAB + lenAB * Math.cos(Math.PI + angleA + angleC),lenAB * Math.sin(Math.PI + angleA + angleC)));


        double tx0 = lenBC + lenBC * Math.cos(Math.PI + angleB);
        double ty0 = lenBC * Math.sin(Math.PI + angleB);

        double tx1 = lenAB * Math.cos(2*angleB) + lenAB * Math.cos(Math.PI-angleB);
        double ty1 = lenAB * Math.sin(2*angleB) + lenAB * Math.sin(Math.PI-angleB);

        double tx2 = lenAB + lenBC + 2*lenAC * Math.cos(angleC) + lenAC * Math.cos(Math.PI - angleC);
        double ty2 = 2*lenAC * Math.sin(angleC) + lenAC * Math.sin(Math.PI - angleC);

        Point2D.Double[] neighborCenters = new Point2D.Double[]{
                new Point2D.Double(tx0, ty0),
                new Point2D.Double(tx1, ty1),
                new Point2D.Double(tx2, ty2),
                new Point2D.Double(-tx2, -ty2),
                new Point2D.Double(-tx0 - tx2, -ty0 - ty2),
                new Point2D.Double(-tx1 + tx2, -ty1 + ty2)
        };


        double[] neighborRotations = new double[6];

        return new TilePattern(tiles, neighborCenters, neighborRotations);
    }

    private Point2D.Double[] getBasicTile(double widthA, double widthB, double height, double rotation, double offsetX, double offsetY){
        Point2D.Double[] verts = new Point2D.Double[3];

        double srcX = 0;
        double srcY = 0;
        verts[0] = new Point2D.Double(offsetX, offsetY);

        srcX = widthA + widthB;
        srcY = 0;
        double dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        double dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[1] = new Point2D.Double(offsetX + dstX, offsetY + dstY);

        srcX = widthA;
        srcY = height;
        dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[2] = new Point2D.Double(offsetX + dstX, offsetY + dstY);

        return verts;
    }
    private Point2D.Double[] getReflectedTile(double widthA, double widthB, double height, double rotation, double offsetX, double offsetY){
        Point2D.Double[] verts = new Point2D.Double[3];

        double srcX = 0;
        double srcY = 0;
        verts[0] = new Point2D.Double(offsetX, offsetY);

        srcX = -widthA - widthB;
        srcY = 0;
        double dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        double dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[1] = new Point2D.Double(offsetX + dstX, offsetY + dstY);

        srcX = -widthA;
        srcY = height;
        dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[2] = new Point2D.Double(offsetX + dstX, offsetY + dstY);

        return verts;
    }
}
