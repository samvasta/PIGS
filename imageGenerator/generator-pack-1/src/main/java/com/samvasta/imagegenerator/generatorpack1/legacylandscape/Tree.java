package com.samvasta.imagegenerator.generatorpack1.legacylandscape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Tree {

    Color col;
    int height;

    Image tree;

    public Tree(Color c, Point origin, int height){
        col = c;
        this.height = height;
        generate(origin);
    }

    private void generate(Point origin){
        Branch trunk = new Branch(origin, new Point(origin.x + (int)(Math.random()*20 - 10), origin.y - height));
        tree = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) tree.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(col);
        trunk.addBranchToImage(g, 10);
    }

    public Image getImage(){
        return tree;
    }

}
