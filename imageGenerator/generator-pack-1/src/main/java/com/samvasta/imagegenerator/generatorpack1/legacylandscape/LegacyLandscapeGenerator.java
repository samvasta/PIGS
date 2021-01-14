package com.samvasta.imagegenerator.generatorpack1.legacylandscape;

import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Map;

public class LegacyLandscapeGenerator extends SimpleGenerator
{
    ArrayList<Point[]> pointListList = new ArrayList<Point[]>();
    //	ArrayList<Point> points = new ArrayList<Point>();
    Color[] colorList;
    double calmness;
    double v;

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        v = random.nextDouble();
        pointListList.clear();
        int numLayers = 3 + (int)(10 * v);
        int intricacyValue = 5 + random.nextInt((int)(10 * v)+1);
        calmness = v;
        while(calmness > 0.35)
            calmness *= v;
        colorList = new Color[numLayers+2];
        genColors(random);
        genLandscape(numLayers, intricacyValue, imageSize);
        genImage(g, imageSize);
    }

//	public Landscape(Dimension size, int numLayers, int intricacyValue, float calmness){
//		this.size = size;
//		colorList = new Color[numLayers+2];
//		this.calmness = calmness;
//		genColors();
//
//		genLandscape(numLayers, intricacyValue);
//
//	}
//
//	public Landscape(int width, int height, int numLayers, int intricacyValue, float calmness){
//		this(new Dimension(width, height), numLayers, intricacyValue, calmness);
//	}

    private void genLandscape(int numLayers, int intricacyValue, Dimension size){
        int yStart = (int)(Math.random()*size.height/4 + size.height/2);
//		int yEnd = yStart + (int)(Math.random()*size.height/4);
        ArrayList<Point> points = new ArrayList<Point>();
        for(int i = 0; i < numLayers; i++){
            points.clear();
            if(Math.random() < Math.random()){
                points.add(new Point(0, size.height-yStart));
                points.add(new Point((int)(size.getWidth()),size.height-(yStart/2)));
            }
            else{
                points.add(new Point(0, size.height-(yStart/2)));
                points.add(new Point((int)(size.getWidth()), size.height-yStart));
            }
            for(int j = 0; j < intricacyValue; j++)
                points = addMidPoints(points);

            pointListList.add(points.toArray(new Point[]{}));
            yStart *= 0.8;
//			yEnd *= 0.6;
        }
    }



    private ArrayList<Point> addMidPoints(ArrayList<Point> points){
        ArrayList<Point> toReturn = new ArrayList<Point>();
        Point[] originalPoints = points.toArray(new Point[]{});
        Point first, next;
        first = originalPoints[0];
        toReturn.add(first);
        for(int i = 1; i < originalPoints.length; i++){
            next = originalPoints[i];
            float lineLength = (float)(Point.distance(first.getX(), first.getY(), next.getX(), next.getY())*calmness);
            toReturn.add(getAdjustedMidpoint(first, next, lineLength));
            toReturn.add(next);
            first = next;
        }

//		points.add(new Point(0, (int)(Math.random()*(size.getHeight()*0.5) + size.getHeight()*0.25)));
//		points.add(new Point((int)(size.getWidth()), (int)(Math.random()*(size.getHeight()*0.25) + size.getHeight()*0.125)));
//
//		points.add(1, getAdjustedMidpoint(points.get(0), points.get(1)));
        return toReturn;
    }

    private Point getAdjustedMidpoint(Point p1, Point p2, float amplitude){
        double angle = getPerpendicular(p1.x, p1.y, p2.x, p2.y);
        double amount = Math.random()*amplitude - (amplitude/2f);
        Point midPoint = new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2);
        midPoint.setLocation(midPoint.getX() + Math.cos(angle)*amount, midPoint.getY() + Math.sin(angle)*amount);
        return midPoint;
    }

    private void genColors(RandomGenerator random){
        colorList[0] = getBgColor();
        colorList[colorList.length-1] = getFgColor();
        for(int i = 1; i < colorList.length-1; i++){
            colorList[i] = getColorFromRamp(colorList[0], colorList[colorList.length-1], colorList.length, i);
        }
        if(v < random.nextDouble())
            colorList[0] = ColorUtil.getClose(ColorUtil.shift(colorList[0], (float)ColorUtil.GOLDEN_RATIO, 0, 0), v);
    }

    private void genImage(Graphics2D g, Dimension size){
        g.setColor(colorList[0]);
        g.fillRect(0, 0, size.width, size.height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int treeLayer = (int)(Math.random()*pointListList.size()) + 1;
        if(treeLayer >= pointListList.size()-1) treeLayer--;
        if(treeLayer <= 1) treeLayer++;
        for(int i = 0; i < pointListList.size(); i++){
            if(Math.random()< 0.8 || i==treeLayer){
                Tree t = new Tree(colorList[i], new Point(500, 1000), (int)(100*((float)i/pointListList.size())));
                Point p;
                do{
                    p = getRandomPoint(pointListList.get(i));
                }while(p.getX() < size.width/7 || p.getX() > size.width*6/7);
                int y = (int)p.getY();
                int x = (int)p.getX();
                g.drawImage(t.getImage(), x-500, y-1000, null);
                g.setColor(colorList[i].darker());
                g.fillRect(x-5, y-1000+size.height, 10, size.height);
                if(treeLayer==i)treeLayer=-1;
            }
            drawLand(g, pointListList.get(i), i+1, size);
        }
//		Tree t = new Tree(Color.BLACK, new Point(500, 500), 100);
//		g.drawImage(t.getImage(), 0, 0, null);
    }

    private Point getRandomPoint(Point[] p){
        return p[(int)(Math.random()*p.length)];
    }

    private void drawLand(Graphics g, Point[] points, int colNumber, Dimension size){
        Polygon p = new Polygon();

        p.addPoint((int)size.getWidth(), (int)size.getHeight());
        p.addPoint(0, (int)size.getHeight());
        for(Point point: points){
            p.addPoint(point.x, point.y);
        }

        g.setColor(colorList[colNumber]);
        g.fillPolygon(p);
    }

    private Color getBgColor(){
        return setHue(new Color(180 + (int)(40*v), 190 + (int)(40 * v), 170 + (int)(40*v)));
    }

    private Color getFgColor(){
        return setHue(new Color(80 + (int)(40*v), 95 + (int)(40*v), 60 + (int)(40*v)));
    }

    private Color getColorFromRamp(int stepWanted){
        return getColorFromRamp(colorList[0], colorList[colorList.length-1], colorList.length, stepWanted);
    }

    private Color getColorFromRamp(Color c1, Color c2, int stepsInRamp, int stepWanted){
        float percent = (1f/(stepsInRamp)) * ((float)stepWanted);
        return blendColors(c1, c2, percent);
    }

    private Color blendColors(Color c1, Color c2, float percent){
        int r1 = c1.getRed(), r2 = c2.getRed(),
                g1 = c1.getGreen(), g2 = c2.getGreen(),
                b1 = c1.getBlue(), b2 = c2.getBlue();

        int r = (int)Math.min(Math.max((r2*percent) + (r1*(1-percent)), 0), 255),
                g = (int)Math.min(Math.max((g2*percent) + (g1*(1-percent)), 0), 255),
                b = (int)Math.min(Math.max((b2*percent) + (b1*(1-percent)), 0), 255);
        return new Color(r, g, b);
    }

    private Color setHue(Color c){
        float hsv[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), new float[]{0,0,0});
        hsv[0] += Math.random();
        if(hsv[0] > 1){
            hsv[0]--;
        }
        return Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
    }

    private double getPerpendicular(double x1, double y1, double x2, double y2){
        return Math.atan2(y2-y1, x2-x1)+Math.PI/2;
    }
}
