package com.samvasta.imagegenerator.generatorpack1.triangles;

import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Map;

public class TrianglesGenerator extends SimpleGenerator
{
    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        double variation = random.nextDouble();
        setRadius(Math.max(imageSize.width, imageSize.height), variation);
        screenWidth = imageSize.width;
        screenHeight = imageSize.height;

        gridWidth = (int)Math.ceil(screenWidth / ((Math.sqrt(3)/2)*getRadius())) + 1;
        gridHeight = (int)(Math.ceil(screenHeight) / ((Math.sqrt(3)/2)*getRadius())) + 1;

        grid = new Color[gridWidth * gridHeight];
        variation = 1-variation;
        float saturation = (float)(1-variation) + (float)(variation*variation);
        float brightness = (float)(1-variation) + (float)(variation*variation);
        base = ColorUtil.getHSB(random.nextFloat(), saturation,  brightness);
        variation = 1-variation * variation;
//		if(variation*variation > 0.5 + random.nextDouble())
//			angle = variation * 720;

        if(random.nextDouble() > variation*variation){
            bg = ColorUtil.getHSB(Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null)[0], 0.15f, (float)(random.nextFloat() * variation));
            if(random.nextDouble() > variation){
                lines = base.darker().darker();
                lines = new Color(lines.getRed(), lines.getGreen(), lines.getBlue(), (int)(32*variation));
            }
            else{
                lines = new Color(0, 128, 128, (int)(32*variation));
            }
        }
        else{
            bg = ColorUtil.getHSB(random.nextFloat(), 0.8f, 0.1f * (float)variation);
            if(random.nextDouble() > variation){
                lines = new Color(255, 255, 255, 64);
            }
            else{
                int i = (int)(variation * 255);
                lines = new Color(i, i, i, i);
            }
        }
        if(variation > random.nextDouble()){
            bg = ColorUtil.getNextInSequence(bg, variation);
        }
        boolean diffCols = false;
        if(variation > random.nextDouble())
            diffCols = true;
        for(int i = 0; i < grid.length; i++){
            if(!diffCols || variation > random.nextDouble())
                grid[i] = ColorUtil.getClose(base, variation);
            else
                grid[i] = ColorUtil.getNextInSequence(base, variation);
//			grid[i].rotate(angle);
        }
        Point origin = new Point((int)(screenWidth/3*random.nextDouble()) + (int)(random.nextDouble()*screenWidth/6), (int)(screenHeight/4 + screenHeight*3/4*random.nextDouble()));
        if(random.nextDouble() > 0.5){
            origin.x += (int)(screenWidth/2);
        }
//		System.out.println("Origin = " + origin);
        double distToEdgeX = Math.min(origin.x, screenWidth - origin.x);
        double distance = Math.max(distToEdgeX*2, screenWidth/3);//Math.sqrt(screenWidth*screenWidth + screenHeight*screenHeight)/(2*variation);
        for(int i = 0; i < grid.length; i++){
            if(getCenter(i % gridWidth, i / gridWidth).distance(new Point(origin)) < distance && random.nextDouble()< 1 - Math.pow(getCenter(i % gridWidth, i / gridWidth).distance(new Point(origin))/(distance), 4*variation))
                grid[i] = null;
        }

//		for(int i = 0; i < grid.length; i++){
//			if(grid[i] != null && !hasNeighbors(i))
//				grid[i] = null;
//		}

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(bg);
        g.fillRect(0, 0, (int)imageSize.getWidth(), (int)imageSize.getHeight());

        g.setColor(lines);
        for(int i = -gridWidth; i < gridWidth; i++){
            g.drawLine((int)((i)*((Math.sqrt(3))*getRadius())), 0, (int)(i*((Math.sqrt(3))*getRadius())) + (int)(screenHeight/Math.tan(Math.toRadians(60))), screenHeight);
            g.drawLine((int)((i)*((Math.sqrt(3))*getRadius())), 0, (int)((i)*((Math.sqrt(3))*getRadius())) - (int)(screenHeight/Math.tan(Math.toRadians(60))), screenHeight);
            g.drawLine(0, (int)((i)*(Math.sqrt(3)*Math.sqrt(3)/2)*getRadius()), screenWidth, (int)((i)*(Math.sqrt(3)*Math.sqrt(3)/2)*getRadius()));
        }

        RadialGradientPaint rgp;
        rgp = new RadialGradientPaint(origin, (float)distance, new float[]{0.0f, 0.4f, 1.0f}, new Color[]{bg, bg, new Color(bg.getRed(), bg.getGreen(),bg.getBlue(),10)}, MultipleGradientPaint.CycleMethod.NO_CYCLE);

        g.setPaint(rgp);
        g.fillOval((int)(origin.x-distance*1.5), (int)(origin.y-distance*1.5), (int)(distance*3), (int)(distance*3));

        for(int i = 0; i < grid.length; i++){
            Color c = grid[i];
            if(c!=null){
                g.setColor(c);
                g.fill(getRotatedTriangle(i % gridWidth, i / gridWidth));
            }
        }

        Point2D start = new Point2D.Float(0,0);
        Point2D end = new Point2D.Float(1000,0);
        float[] dist = {0.0f, 0.6f, 1.0f};
        Color[] colors = {new Color(32,32,32,192), new Color(32,32,32,192), new Color(32,32,32,0)};
        LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
    }

    private Point getCenter(int x, int y){
        if((x+y) % 2 == 0){
            return new Point((int)(x*((Math.sqrt(3)/2)*getRadius())), (int)((y)*(Math.sqrt(3)*Math.sqrt(3)/2)*getRadius()) + getRadius());
        }else{
            return new Point((int)(x*((Math.sqrt(3)/2)*getRadius())), (int)((y+1)*(Math.sqrt(3)*Math.sqrt(3)/2)*getRadius()) - (getRadius()));
        }
    }

    private Polygon getTriangle(int x, int y){
        Polygon triangle;
        if((x+y) % 2 == 0){
            triangle = new Polygon(TRIANGLE_UP(getRadius()).xpoints, TRIANGLE_UP(getRadius()).ypoints, 3);
            triangle.translate((int)(x*((Math.sqrt(3)/2)*getRadius())), (int)((y)*(Math.sqrt(3)*Math.sqrt(3)/2)*getRadius()) + getRadius());
        }else{
            triangle = new Polygon(TRIANGLE_DOWN(getRadius()).xpoints, TRIANGLE_DOWN(getRadius()).ypoints, 3);
            triangle.translate((int)(x*((Math.sqrt(3)/2)*getRadius())), (int)((y+1)*(Math.sqrt(3)*Math.sqrt(3)/2)*getRadius()) - (getRadius()));
        }
        return triangle;
    }

    public Polygon getRotatedTriangle(int x, int y){
        Polygon p = getTriangle(x, y);
        Point[] points = new Point[p.npoints], newPoints = new Point[points.length];
        for(int i = 0; i < points.length; i++)
            points[i] = new Point(p.xpoints[i], p.ypoints[i]);

        for(Point pt: points){
            pt = rotatePoint(pt, getCenter(x, y), angle);
        }

        int[] xpoints = new int[points.length], ypoints = new int[points.length];
        for(int i = 0; i < points.length; i++){
            xpoints[i] = points[i].x;
            ypoints[i] = points[i].y;
        }

        return new Polygon(xpoints, ypoints, newPoints.length);
    }

    public Point rotatePoint(Point pt, Point center, double angleDeg)
    {
        double angleRad = (angleDeg/180)*Math.PI;
        double cosAngle = Math.cos(angleRad );
        double sinAngle = Math.sin(angleRad );
        double dx = (pt.x-center.x);
        double dy = (pt.y-center.y);

        pt.x = center.x + (int) (dx*cosAngle-dy*sinAngle);
        pt.y = center.y + (int) (dx*sinAngle+dy*cosAngle);
        return pt;
    }


    private boolean hasNeighbors(int i){
        if(i+1 < grid.length && grid[i+1] != null)return true;
        if(i-1 >= 0 && grid[i-1] != null) return true;
        if(i-gridWidth >= 0 && grid[i-gridWidth] != null) return true;
        if(i+gridWidth< grid.length && grid[i+gridWidth] != null) return true;
        return false;
    }

    private Color base, bg, lines;
    double angle = 0;
    private Color[] grid;
    //	private Tile[] grid;
    private int gridWidth, gridHeight;
    private int screenWidth, screenHeight;

    private int radius = 10;
    public void setRadius(int size, double varience){
        radius = (int)(size/((150.0*varience) + 10.0));
    }
    public int getRadius(){
        return radius;
    }

    public Polygon TRIANGLE_DOWN(final int radius){
        return new Polygon(){{
            addPoint(0, (int)((radius+1)));
            addPoint((int)((radius+1)*Math.cos(Math.toRadians(330))), (int)((radius+1)*Math.sin(Math.toRadians(330))));
            addPoint((int)((radius+1)*Math.cos(Math.toRadians(210))), (int)((radius+1)*Math.sin(Math.toRadians(210))));
        }};
    }

    public Polygon TRIANGLE_UP(final int radius){
        return new Polygon(){{
            addPoint(0, -(int)((radius+1)));
            addPoint((int)((radius+1)*Math.cos(Math.toRadians(30))), (int)((radius+1)*Math.sin(Math.toRadians(30))));
            addPoint((int)((radius+1)*Math.cos(Math.toRadians(150))), (int)((radius+1)*Math.sin(Math.toRadians(150))));
        }};
    }

    public final Polygon TRIANGLE_RIGHT(final int radius){
        return new Polygon(){{
            addPoint(radius, 0);
            addPoint((int)(radius*Math.cos(Math.toRadians(120))), (int)(radius*Math.sin(Math.toRadians(120))));
            addPoint((int)(radius*Math.cos(Math.toRadians(240))), (int)(radius*Math.sin(Math.toRadians(240))));
        }};
    }

    public final Polygon TRIANGLE_LEFT(final int radius){
        return new Polygon(){{
            addPoint(-radius, 0);
            addPoint((int)(radius*Math.cos(Math.toRadians(300))), (int)(radius*Math.sin(Math.toRadians(300))));
            addPoint((int)(radius*Math.cos(Math.toRadians(60))), (int)(radius*Math.sin(Math.toRadians(60))));
        }};
    }


}
