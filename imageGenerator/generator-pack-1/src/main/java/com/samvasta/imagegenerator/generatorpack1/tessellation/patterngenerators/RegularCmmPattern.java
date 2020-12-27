package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import com.samvasta.imagegenerator.generatorpack1.tessellation.Tile;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePattern;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TilePatternLibrary;

import java.awt.geom.Point2D;

public class RegularCmmPattern  implements IPatternGenerator
{
    private static final double HEX_SIDE_LENGTH = TilePatternLibrary.UNIT_SIDE_LENGTH / 4.0;
    @Override
    public PatternGeneratorParameter[] getParameters()
    {
        return new PatternGeneratorParameter[0];
    }


    /*

    new TessellationMesh(
            new Point2D.Double[]{
                    //Dodecagon
                    new Point2D.Double(0.9659258262890683, 0.25881904510252074),
                    new Point2D.Double(0.7071067811865476, 0.7071067811865475),
                    new Point2D.Double(0.2588190451025209, 0.9659258262890682),
                    new Point2D.Double(-0.2588190451025207, 0.9659258262890683),
                    new Point2D.Double(-0.7071067811865474, 0.7071067811865477),
                    new Point2D.Double(-0.9659258262890683, 0.25881904510252074),
                    new Point2D.Double(-0.9659258262890683, -0.25881904510252063),
                    new Point2D.Double(-0.7071067811865478, -0.7071067811865472),
                    new Point2D.Double(-0.2588190451025213, -0.9659258262890681),
                    new Point2D.Double(0.2588190451025206, -0.9659258262890683),
                    new Point2D.Double(0.7071067811865477, -0.7071067811865475),
                    new Point2D.Double(0.9659258262890681, -0.2588190451025213),
                    //Hexagons
                    new Point2D.Double(1.414213562373095, -0.5176380902050415),

                    new Point2D.Double(1.1553945172705744, -0.9659258262890684),
                    new Point2D.Double(0.2588190451025209, -1.48356391649411),
                    new Point2D.Double(0.7071067811865474, -1.7423829615966304),
                    new Point2D.Double(1.1553945172705744, -1.48356391649411),

                    new Point2D.Double(-1.1553945172705737, -0.9659258262890684),
                    new Point2D.Double(-1.155394517270574, -1.48356391649411),
                    new Point2D.Double(-0.7071067811865476, -1.7423829615966304),
                    new Point2D.Double(-0.2588190451025205, -1.48356391649411),
                    //1 extra point for the square
                    new Point2D.Double(-1.4142135623730954, -0.5176380902050416)
            },
            new int[][] 	{
                    {0,1,2,3,4,5,6,7,8,9,10,11},
                    {13,10,9,14,15,16},
                    {8,7,17,18,19,20},
                    {10,11,12,13},
                    {8,9,14,20},
                    {6,7,17,21}
            },
            new Point2D.Double[]{
                    new Point2D.Double(2.121320343559643, 1.224744871391589),
                    new Point2D.Double(0, 2.4494897427831783),
                    new Point2D.Double(-2.121320343559643, 1.224744871391589),
                    new Point2D.Double(-2.1213203435596433, -1.2247448713915885),
                    new Point2D.Double(0, -2.4494897427831783),
                    new Point2D.Double(2.121320343559642, -1.2247448713915903),
            },
            new double[]{0,0,0,0,0,0},
            new int[][]{}
    );

     */


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
