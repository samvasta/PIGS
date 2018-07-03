package com.samvasta.imagegenerator.generatorpack1.landscape;

import com.samvasta.imageGenerator.common.graphics.stamps.IStamp;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TreeStamp implements IStamp {
    private static final int NUM_LEAF_LAYERS = 5;

    private int width;
    private int height;

    @Override
    public void stamp(Graphics2D g, int x, int y, int width, int height, double rotationAngle)
    {
        this.width = width;
        this.height = height;

        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(rotationAngle);
        g.setTransform(transform);

        drawTrunk(g);
        g.setColor(Color.GREEN);
        drawLeaves(g);
    }

    private void drawTrunk(Graphics2D g){

        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = (int)(width * -0.15);
        xPoints[1] = (int)(width * 0.15);
        xPoints[2] = 0;

        yPoints[0] = 0;
        yPoints[1] = 0;
        yPoints[2] = -height;

        Polygon trunkPoly = new Polygon(xPoints, yPoints, xPoints.length);
        g.fill(trunkPoly);
    }

    private void drawLeaves(Graphics2D g){
        int[] xPoints = new int[2 * NUM_LEAF_LAYERS];
        int[] yPoints = new int[2 * NUM_LEAF_LAYERS];

        for(int i = 0; i < NUM_LEAF_LAYERS; i++){
            double percent = (i / (double)NUM_LEAF_LAYERS);

            int y = (int)Math.round(percent * height * 0.8) - height;

            int x;
            if(i % 2 == 0){
                x = -(int)(percent * width);
            }
            else{
                x = -(int)(percent * width * 0.5);
            }

            //left side, top-down
            xPoints[i] = -x;
            yPoints[i] = y;

            //right side
            xPoints[2 * NUM_LEAF_LAYERS - 1 - i] = x;
            yPoints[2 * NUM_LEAF_LAYERS - 1 - i] = y;
        }



        Polygon leafPoly = new Polygon(xPoints, yPoints, xPoints.length);
        g.fill(leafPoly);
    }
}
