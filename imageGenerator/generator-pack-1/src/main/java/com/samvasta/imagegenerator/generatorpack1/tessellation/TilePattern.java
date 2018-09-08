package com.samvasta.imagegenerator.generatorpack1.tessellation;

import com.samvasta.imageGenerator.common.models.Transform2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TilePattern
{
    private final Tile[] tiles;

    private final Point2D.Double[] neighborCenters;
    private final double[] neighborRotations;

    public TilePattern(Tile[] tilesIn, Point2D.Double[] neighborCentersIn, double[] neighborRotationsIn){
        tiles = tilesIn;
        neighborCenters = neighborCentersIn;
        neighborRotations = neighborRotationsIn;
    }

    public List<Point2D.Double[]> getPolygons(Transform2D transform){
        List<Point2D.Double[]> polygons = new ArrayList<>();
        for(int i = 0; i < tiles.length; i++){

            Point2D.Double[] verts = tiles[i].getVerticies();
            Point2D.Double[] transformedVerts = transform.transform(verts);
            polygons.add(transformedVerts);
        }

        return polygons;
    }

    public double[] getNeighborRotations(){
        return neighborRotations;
    }

    public Point2D.Double[] getNeighborCenters(Transform2D transform){
        return transform.transform(neighborCenters);
    }

    public Point2D.Double[] getBoundingBox(Transform2D transform){
        Point2D.Double[] box = new Point2D.Double[4];

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for(int tileIdx = 0; tileIdx < tiles.length; tileIdx++){

            Point2D.Double[] verts = tiles[tileIdx].getBoundingBox();
            Point2D.Double[] transformedVerts = transform.transform(verts);

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

        box[0] = new Point2D.Double(minX, minY);
        box[1] = new Point2D.Double(minX, maxY);
        box[2] = new Point2D.Double(maxX, maxY);
        box[3] = new Point2D.Double(maxX, minY);
        return box;
    }

    public Dimension getDimension(Transform2D transform){
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for(int tileIdx = 0; tileIdx < tiles.length; tileIdx++){

            Point2D.Double[] verts = tiles[tileIdx].getBoundingBox();
            Point2D.Double[] transformedVerts = transform.transform(verts);

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
