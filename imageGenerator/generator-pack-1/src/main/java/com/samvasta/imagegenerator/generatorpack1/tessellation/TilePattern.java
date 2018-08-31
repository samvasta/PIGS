package com.samvasta.imagegenerator.generatorpack1.tessellation;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;
import com.samvasta.imageGenerator.common.models.Transform2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TilePattern
{
    private final Tile[] tiles;

    private final PrecisePoint2D[] neighborCenters;
    private final double[] neighborRotations;

    public TilePattern(Tile[] tilesIn, PrecisePoint2D[] neighborCentersIn, double[] neighborRotationsIn){
        tiles = tilesIn;
        neighborCenters = neighborCentersIn;
        neighborRotations = neighborRotationsIn;
    }

    public List<PrecisePoint2D[]> getPolygons(Transform2D transform){
        List<PrecisePoint2D[]> polygons = new ArrayList<>();
        for(int i = 0; i < tiles.length; i++){

            PrecisePoint2D[] verts = tiles[i].getVerticies();
            PrecisePoint2D[] transformedVerts = transform.transform(verts);
            polygons.add(transformedVerts);
        }

        return polygons;
    }

    public double[] getNeighborRotations(){
        return neighborRotations;
    }

    public PrecisePoint2D[] getNeighborCenters(Transform2D transform){
        return transform.transform(neighborCenters);
    }

    public PrecisePoint2D[] getBoundingBox(Transform2D transform){
        PrecisePoint2D[] box = new PrecisePoint2D[4];

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for(int tileIdx = 0; tileIdx < tiles.length; tileIdx++){

            PrecisePoint2D[] verts = tiles[tileIdx].getBoundingBox();
            PrecisePoint2D[] transformedVerts = transform.transform(verts);

            for(int vertIdx = 0; vertIdx < transformedVerts.length; vertIdx++){
                double x = transformedVerts[vertIdx].getX();
                double y = transformedVerts[vertIdx].getY();
                if(x < minX){
                    minX = x;
                }
                if(x > maxX){
                    maxX = x;
                }

                if(y < minY){
                    minY = y;
                }
                if(y > maxY){
                    maxY = y;
                }
            }
        }

        box[0] = new PrecisePoint2D(minX, minY);
        box[1] = new PrecisePoint2D(minX, maxY);
        box[2] = new PrecisePoint2D(maxX, maxY);
        box[3] = new PrecisePoint2D(maxX, minY);
        return box;
    }

    public Dimension getDimension(Transform2D transform){
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for(int tileIdx = 0; tileIdx < tiles.length; tileIdx++){

            PrecisePoint2D[] verts = tiles[tileIdx].getBoundingBox();
            PrecisePoint2D[] transformedVerts = transform.transform(verts);

            for(int vertIdx = 0; vertIdx < transformedVerts.length; vertIdx++){
                double x = transformedVerts[vertIdx].getX();
                double y = transformedVerts[vertIdx].getY();
                if(x < minX){
                    minX = x;
                }
                if(x > maxX){
                    maxX = x;
                }

                if(y < minY){
                    minY = y;
                }
                if(y > maxY){
                    maxY = y;
                }
            }
        }

        return new Dimension((int)(maxX - minX), (int)(maxY - minY));
    }

}
