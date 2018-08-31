package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.pgg;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;
import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.IPatternGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators.PatternGeneratorParameter;

public class N3_01 implements IPatternGenerator
{
    private final PatternGeneratorParameter[] PARAMETERS = new PatternGeneratorParameter[]{
                    new PatternGeneratorParameter(0.15, 0.85, 0.3)
            };

    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return PARAMETERS;
    }

    @Override
    public TilePattern generatePattern(double[] parameters)
    {
        double parameter = parameters[0];
        parameter = 0.3;

        Tile[] tiles = new Tile[8];
        double ln1 = TilePatternLibrary.UNIT_SIDE_LENGTH * parameter;
        double ln2 = TilePatternLibrary.UNIT_SIDE_LENGTH - ln1;

        PrecisePoint2D[] points = getBasicTriangle(ln1, ln2, 0, 0, 0);
        tiles[0] = new Tile(points);

        points = getBasicTriangle(ln1, ln2, Math.PI / 2.0, ln2, -ln1);
        tiles[1] = new Tile(points);

        points = getBasicTriangle(ln1, ln2, -Math.PI / 2.0, ln1, ln1);
        tiles[2] = new Tile(points);

        points = getBasicTriangle(ln1, ln2, Math.PI, ln2 + ln1, 0);
        tiles[3] = new Tile(points);

        double angle = 2*Math.atan2(ln1, ln2);
        points = getReflectedTriangle(ln1, ln2, Math.PI / 2 + angle, ln1, ln1);
        tiles[4] = new Tile(points);

        points = getReflectedTriangle(ln1, ln2, Math.PI + angle, 2*ln1, ln2 + ln1);
        tiles[5] = new Tile(points);

        double angle1 = Math.atan2(ln2 * Math.cos(angle), ln2 * Math.sin(angle));
        double angle2 = Math.PI * 3.0 / 2.0 + angle1;
        double tx6 = ln1 + ln1 * Math.cos(angle1) + ln1 * Math.cos(angle2);
        double ty6 = ln1 + ln1 * Math.sin(angle1) + ln1 * Math.sin(angle2);
        points = getReflectedTriangle(ln1, ln2, angle, tx6, ty6);
        tiles[6] = new Tile(points);

        angle = angle - Math.PI/2.0;
        double tx7 = ln1 + (ln1 + ln2) * Math.cos(angle);
        double ty7 = ln1 + (ln1 + ln2) * -Math.sin(angle);
        points = getReflectedTriangle(ln1, ln2, angle, tx7, ty7);
        tiles[7] = new Tile(points);

        PrecisePoint2D[] neighborCenters = new PrecisePoint2D[]{
                new PrecisePoint2D(ln2 - ln1 -tx6, -ln2 - ln1 - ty6),
                new PrecisePoint2D(ln2 - ln1, -ln2 - ln1),
                new PrecisePoint2D(tx6, ty6),
                new PrecisePoint2D(tx6 + ln1 - ln2, ty6 + ln1 + ln2),
                new PrecisePoint2D(ln1 - ln2, ln1 + ln2),
                new PrecisePoint2D(-tx6, -ty6)
        };

        double[] neighborRotations = new double[] { 0, 0, 0, 0, 0, 0 };
        return new TilePattern(tiles, neighborCenters, neighborRotations);
    }

    private PrecisePoint2D[] getBasicTriangle(double ln1, double ln2, double rotation, double offsetX, double offsetY){
        PrecisePoint2D[] points = new PrecisePoint2D[3];

        points[0] = new PrecisePoint2D(offsetX, offsetY);
        points[1] = new PrecisePoint2D(offsetX + Math.cos(rotation) * ln1, offsetY + Math.sin(rotation) * ln1);
        points[2] = new PrecisePoint2D(offsetX + Math.cos(rotation) * ln1 - Math.sin(rotation) * ln2, offsetY + Math.sin(rotation) * ln1 + Math.cos(rotation) * ln2);

        return points;
    }

    private PrecisePoint2D[] getReflectedTriangle(double ln1, double ln2, double rotation, double offsetX, double offsetY){
        PrecisePoint2D[] points = new PrecisePoint2D[3];

        points[0] = new PrecisePoint2D(offsetX, offsetY);
        points[1] = new PrecisePoint2D(offsetX - Math.cos(rotation) * ln1, offsetY + Math.sin(rotation) * ln1);
        points[2] = new PrecisePoint2D(offsetX - Math.cos(rotation) * ln1 + Math.sin(rotation) * ln2, offsetY + Math.sin(rotation) * ln1 + Math.cos(rotation) * ln2);

        return points;
    }
}
