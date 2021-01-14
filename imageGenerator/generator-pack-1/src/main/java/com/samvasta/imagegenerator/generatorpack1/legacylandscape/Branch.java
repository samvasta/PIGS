package com.samvasta.imagegenerator.generatorpack1.legacylandscape;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;

public class Branch {

    ArrayList<Branch> branches = new ArrayList<Branch>();
    Point start, end;
    Branch parent;
    int branchNumber;

    public Branch(Branch parent, int branchNumber, double length){
        this.branchNumber = branchNumber;
        this.parent = parent;
        this.start = parent.getEnd();
        double angle = parent.getAngle() + (Math.random()*Math.PI/2) - Math.PI/4;
        end = new Point((int)(start.getX() + (length* Math.cos(angle))), (int)(start.getY() + (length * Math.sin(angle))));

        int numBranches = (int)(Math.random()*4 + 1);
        if(branchNumber <= numBranches+4)
            for(int i = 0; i < numBranches; i++){
                branches.add(new Branch(this, branchNumber+1, length*(Math.random()*0.5 + 0.5)));
            }

    }

    public Branch(Point start, Point end){
        this.start = start;
        this.end = end;
        int numBranches = (int)(Math.random()*4 + 1);
        for(int i = 0; i < numBranches; i++){
            branches.add(new Branch(this, 1, start.distance(end)*0.8));
        }
    }

    public void addBranchToImage(Graphics2D g, int width){
//		int width = (int) (10 - (branchNumber*2)*(20f/start.distance(end)));
        if(width < 2)
            width = 2;
        Polygon p = new Polygon();
        p.addPoint((int)(start.getX()-width/4), (int)start.getY());
        p.addPoint((int)(start.getX()+width/4), (int)start.getY());
//		p.addPoint((int)(end.getX() + (width*0.7/2)), (int)end.getY());
        p.addPoint((int)(end.getX()), (int)end.getY());

        Shape s = new BasicStroke(width/2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(p);
//		g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		g.drawLine(start.x, start.y, end.x, end.y);
        g.fill(s);
        for(Branch b: branches){
            b.addBranchToImage(g, (int)(width*0.7));
        }
    }

    public Point getEnd(){
        return end;
    }

    public Point getStart(){
        return start;
    }

    public double getAngle(){
        return Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
    }

}
