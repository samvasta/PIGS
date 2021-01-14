package com.samvasta.imageGenerator.common.shapes.deformable;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sam on 7/8/2017.
 */
public class LineSegment
{

    private Point2D.Double start;
    private Point2D.Double end;
    private Point2D.Double midpoint;

    private LineSegment firstDivision;
    private LineSegment secondDivision;

    private boolean isSplit;

    public LineSegment(double startX, double startY, double endX, double endY){
        this(new Point2D.Double(startX, startY), new Point2D.Double(endX, endY));
    }

    public LineSegment(Point2D.Double startIn, Point2D.Double endIn){
        start = startIn;
        end = endIn;
        midpoint = new Point2D.Double( (start.x + end.x) / 2d, (start.y + end.y) / 2d);

        firstDivision = null;
        secondDivision = null;
        isSplit = false;
    }

    public void trySplit(){
        if(!isSplit){
            firstDivision = new LineSegment(start, midpoint);
            secondDivision = new LineSegment(midpoint, end);
            isSplit = true;
        }
    }

    public void deform(RandomGenerator random, int depth){
        if(depth > 0){
            trySplit();
        }

        double deflectionAmt = random.nextGaussian() * getMagnitude() / 5d;
        double angle = getAngle() + Math.PI / 2d;

        Point2D.Double deformedMidpoint = new Point2D.Double(midpoint.x + deflectionAmt * Math.cos(angle), midpoint.y + deflectionAmt * Math.sin(angle));

        setMidpoint(deformedMidpoint);

        if(isSplit){
            firstDivision.deform(random, depth-1);
            secondDivision.deform(random, depth-1);
        }
    }

    private void setMidpoint(Point2D.Double point){
        midpoint = point;
        if(isSplit){
            firstDivision.end = midpoint;
            secondDivision.start = midpoint;
        }
    }

    public double getMagnitude(){
        return start.distance(end);
    }

    public double getMagnitudeSq(){
        return start.distanceSq(end);
    }

    public double getAngle(){
        return Math.atan2(end.y - start.y, end.x - start.x);
    }

    public List<Point2D.Double> getPath(boolean includeEnd){
        List<Point2D.Double> path = new ArrayList<>();
        appendToPath(path, includeEnd);
        return path;
    }

    public List<Point2D.Double> getPath(){
        return getPath(true);
    }

    public void appendToPath(List<Point2D.Double> path, boolean includeEnd){

        if(firstDivision != null){
            firstDivision.appendToPath(path, false);
        }
        else{
            path.add(start);
        }

        if(secondDivision != null){
            secondDivision.appendToPath(path, false);
        }

        if(includeEnd){
            path.add(end);
        }
    }

    public LineSegment clone(){
        LineSegment clone = new LineSegment(this.start.x, this.start.y, this.end.x, this.end.y);
        clone.midpoint = new Point2D.Double(this.midpoint.x, this.midpoint.y);
        clone.isSplit = this.isSplit;
        if(isSplit){
            clone.firstDivision = this.firstDivision.clone();
            clone.secondDivision = this.secondDivision.clone();
        }
        else{
            clone.firstDivision = null;
            clone.secondDivision = null;
        }
        return clone;
    }
}
