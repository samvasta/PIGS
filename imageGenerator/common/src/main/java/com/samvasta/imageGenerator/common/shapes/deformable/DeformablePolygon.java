package com.samvasta.imageGenerator.common.shapes.deformable;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sam on 7/8/2017.
 */
public class DeformablePolygon
{
    private ArrayList<Point2D.Double> points;
    private int numSides;
    private double radius;
    private Point2D.Double translation;

    public DeformablePolygon(int numSidesIn, double radius){
        numSides = numSidesIn;
        points = new ArrayList<>(numSides);
        this.radius = radius;

        double deltaAngle = Math.PI * 2d / (double)numSides;
        for(int i = 0; i < numSides; i++){
            Point2D.Double start = new Point2D.Double(radius * Math.cos(deltaAngle * i), radius * Math.sin(deltaAngle * i));
            points.add(start);
        }
        translation = new Point2D.Double(0, 0);
    }

    public void setTranslation(Point2D.Double translationIn){
        this.translation = translationIn;
    }

    public void setTranslation(double x, double y){
        translation.x = x;
        translation.y = y;
    }

    public void deform(RandomGenerator random, int depth){

        for(int d = 0; d < depth; d++){
            int startIdx = 0;
            while(startIdx < points.size()+1){
                Point2D.Double start = points.remove(startIdx%points.size());
                Point2D.Double end = points.remove(startIdx%points.size());
                LineSegment lineSegment = new LineSegment(start, end);
                lineSegment.deform(random, 1);
                List<Point2D.Double> path = lineSegment.getPath(true);
                while(!path.isEmpty()){
                    points.add(startIdx%points.size(), path.remove(path.size()-1));
                }
                startIdx += 2;
            }
        }

    }

    public Polygon getPolygon(){
        Polygon p = new Polygon();
        for(Point2D.Double point : getPath()){
            p.addPoint((int)Math.round(point.x), (int)Math.round(point.y));
        }
        return p;
    }

    public List<Point2D.Double> getPath(){
        List<Point2D.Double> transformedPath = new ArrayList<>();
        for(Point2D.Double p : points){
            transformedPath.add(new Point2D.Double(p.x + translation.x, p.y + translation.y));
        }
        return transformedPath;
    }

    public DeformablePolygon clone(){
        DeformablePolygon clone = new DeformablePolygon(this.numSides, radius);
        clone.translation = new Point2D.Double(this.translation.x, this.translation.y);
        clone.points.clear();
        for(int i = 0; i < this.points.size(); i++){
            Point2D.Double p = this.points.get(i);
            clone.points.add(new Point2D.Double(p.x, p.y));
        }
        return clone;
    }
}
