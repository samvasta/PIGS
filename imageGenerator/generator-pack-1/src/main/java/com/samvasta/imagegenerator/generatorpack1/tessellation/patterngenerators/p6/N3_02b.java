package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.p6;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.IPatternGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.PatternGeneratorParameter;

import java.awt.geom.Point2D;

public class N3_02b implements IPatternGenerator
{

    private static final double SIDE_LENGTH_SHORT = 0.5 * TilePatternLibrary.UNIT_SIDE_LENGTH;
    private static final double SIDE_LENGTH_LONG = MathHelper.SQRT_3 * 0.5 * TilePatternLibrary.UNIT_SIDE_LENGTH;
    private static final double SIDE_LENGTH_HYP = Math.sqrt(SIDE_LENGTH_LONG * SIDE_LENGTH_LONG + SIDE_LENGTH_SHORT * SIDE_LENGTH_SHORT);

    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return new PatternGeneratorParameter[0];
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        Tile[] tiles = new Tile[12];

        tiles[0] = new Tile(getTile(SIDE_LENGTH_SHORT * Math.cos(MathHelper.DEG120), SIDE_LENGTH_SHORT * Math.sin(MathHelper.DEG120), -MathHelper.DEG60));

        tiles[1] = new Tile(getTile(SIDE_LENGTH_LONG * Math.cos(MathHelper.DEG30), SIDE_LENGTH_LONG * Math.sin(MathHelper.DEG30), MathHelper.DEG120));

        tiles[2] = new Tile(getReflectedTile(SIDE_LENGTH_LONG * Math.cos(MathHelper.DEG30), SIDE_LENGTH_LONG * Math.sin(MathHelper.DEG30), MathHelper.DEG120));

        tiles[3] = new Tile(getReflectedTile(SIDE_LENGTH_SHORT, 0, Math.PI));

        tiles[4] = new Tile(getTile(SIDE_LENGTH_SHORT, 0, Math.PI));

        tiles[5] = new Tile(getTile(0, -SIDE_LENGTH_LONG, 0));

        tiles[6] = new Tile(getReflectedTile(0, -SIDE_LENGTH_LONG, 0));

        tiles[7] = new Tile(getReflectedTile(SIDE_LENGTH_SHORT *Math.cos(MathHelper.DEG120),-SIDE_LENGTH_SHORT *Math.sin(MathHelper.DEG120), MathHelper.DEG60));

        tiles[8] = new Tile(getTile(SIDE_LENGTH_SHORT *Math.cos(MathHelper.DEG120),-SIDE_LENGTH_SHORT *Math.sin(MathHelper.DEG120), MathHelper.DEG60));

        tiles[9] = new Tile(getTile(SIDE_LENGTH_LONG * Math.cos(Math.PI + MathHelper.DEG30), SIDE_LENGTH_LONG * Math.sin(MathHelper.DEG30), Math.PI + MathHelper.DEG60));

        tiles[10] = new Tile(getReflectedTile(SIDE_LENGTH_LONG * Math.cos(Math.PI + MathHelper.DEG30), SIDE_LENGTH_LONG * Math.sin(MathHelper.DEG30), Math.PI + MathHelper.DEG60));

        tiles[11] = new Tile(getReflectedTile(SIDE_LENGTH_SHORT * Math.cos(MathHelper.DEG120), SIDE_LENGTH_SHORT * Math.sin(MathHelper.DEG120), -MathHelper.DEG60));

        Point2D.Double[] neighborCenters = TilePatternLibrary.getRegularPolygonRadius(6, SIDE_LENGTH_LONG * 2, MathHelper.DEG30);

        double[] neighborRotations = new double[6];

        return new TilePattern(tiles, neighborCenters, neighborRotations);
    }

    private Point2D.Double[] getTile(double offsetX, double offsetY, double rotation){
        Point2D.Double[] verts = new Point2D.Double[3];

        //    30/60/90 special right triangle

        double srcX = 0;
        double srcY = 0;
        verts[0] = new Point2D.Double(offsetX + srcX, offsetY + srcY);

        srcX = SIDE_LENGTH_SHORT;
        srcY = 0;
        double dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        double dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[1] = new Point2D.Double(offsetX + dstX, offsetY + dstY);



        srcX = 0;
        srcY = SIDE_LENGTH_LONG;
        dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[2] = new Point2D.Double(offsetX + dstX, offsetY + dstY);

        return verts;
    }

    private Point2D.Double[] getReflectedTile(double offsetX, double offsetY, double rotation){
        Point2D.Double[] verts = new Point2D.Double[3];

        //    30/60/90 special right triangle

        double srcX = 0;
        double srcY = 0;
        verts[0] = new Point2D.Double(offsetX + srcX, offsetY + srcY);

        srcX = -SIDE_LENGTH_SHORT;
        srcY = 0;
        double dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        double dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[1] = new Point2D.Double(offsetX + dstX, offsetY + dstY);



        srcX = 0;
        srcY = SIDE_LENGTH_LONG;
        dstX = srcX * Math.cos(rotation) - srcY * Math.sin(rotation);
        dstY = srcX * Math.sin(rotation) + srcY * Math.cos(rotation);
        verts[2] = new Point2D.Double(offsetX + dstX, offsetY + dstY);

        return verts;
    }
}
