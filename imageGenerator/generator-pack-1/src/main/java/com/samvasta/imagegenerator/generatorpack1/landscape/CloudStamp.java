package com.samvasta.imagegenerator.generatorpack1.landscape;

import com.samvasta.imageGenerator.common.graphics.stamps.IStamp;
import com.samvasta.imageGenerator.common.graphics.stamps.StampInfo;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class CloudStamp implements IStamp
{
    private boolean isTailOnLeft;
    private StampInfo stampInfo;
    private RandomGenerator random;
    private int baseHeight;
    private double maxCircleDiameter;
    private double minCircleDiameter;

    private List<Ellipse2D.Double> ellipseCenters;

    public CloudStamp(boolean isTailOnLeftIn){
        isTailOnLeft = isTailOnLeftIn;
    }

    @Override
    public void stamp(Graphics2D g, StampInfo stampInfo, RandomGenerator random)
    {
        this.stampInfo = stampInfo;
        this.random = random;
        Area area = new Area();
        ellipseCenters = new ArrayList<>();

        baseHeight = (int)(stampInfo.getHeightInt() * 0.2);
        maxCircleDiameter = stampInfo.getHeight() * 0.65 + random.nextGaussian() * stampInfo.getHeight() * 0.1;

        minCircleDiameter = Math.min(maxCircleDiameter * 0.4, stampInfo.getHeight() * 0.25 + random.nextGaussian() * stampInfo.getHeight() * 0.1);

        double startCircleDiameter = getCircleDiameter();
        double endCircleDiameter = getCircleDiameter();

        Rectangle2D.Double baseRect = new java.awt.geom.Rectangle2D.Double(startCircleDiameter/2.0, stampInfo.getHeight() - Math.min(startCircleDiameter/2.0, endCircleDiameter/2.0), stampInfo.getWidth() - endCircleDiameter/2.0 - startCircleDiameter/2.0, Math.min(startCircleDiameter/2.0, endCircleDiameter/2.0));
        area.add(new Area(baseRect));

        //add left-most ellipse
        Ellipse2D.Double left = addEllipse(0, stampInfo.getHeight() - startCircleDiameter, startCircleDiameter, area);

        //add right-most ellipse
        Ellipse2D.Double right = addEllipse(stampInfo.getWidth() - endCircleDiameter, stampInfo.getHeight() - endCircleDiameter, endCircleDiameter, area);

        double midEllipseX = random.nextGaussian() * baseRect.width * 0.3 + baseRect.x + baseRect.width/2;
        double midEllipseDiameter = getCircleDiameter();
        Ellipse2D.Double middle = addEllipse(midEllipseX, 0, midEllipseDiameter, area);

        ellipseCenters.add(left);
        ellipseCenters.add(middle);
        ellipseCenters.add(right);

        fillGaps(1, area, 0);
        fillGaps(0, area,  0);

        Rectangle2D.Double bottomRect = new Rectangle2D.Double(0, stampInfo.getHeight(), stampInfo.getWidth(), Integer.MAX_VALUE);
        area.subtract(new Area(bottomRect));

        int[] xPoints = new int[ellipseCenters.size() + 2];
        int[] yPoints = new int[ellipseCenters.size() + 2];
        for(int i = 0; i < ellipseCenters.size(); i++){
            Ellipse2D.Double ellipse = ellipseCenters.get(i);
            xPoints[i] = (int)ellipse.getCenterX();
            yPoints[i] = (int) Math.min(baseRect.getMinY(), ellipse.getCenterY());
        }
        xPoints[ellipseCenters.size()] = (int)baseRect.getMaxX();
        xPoints[ellipseCenters.size()+1] = (int)baseRect.getMinX();
        yPoints[ellipseCenters.size()] = (int)baseRect.getMaxY();
        yPoints[ellipseCenters.size()+1] = (int)baseRect.getMaxY();
        Polygon fillPoly = new Polygon(xPoints, yPoints, xPoints.length);
        area.add(new Area(fillPoly));

        AffineTransform originalTransform = g.getTransform();
        AffineTransform transform = new AffineTransform();
        transform.translate(stampInfo.getX(), stampInfo.getY());
        transform.rotate(stampInfo.getRotationAngle());
        g.setTransform(transform);
        g.fill(area);
        g.setTransform(originalTransform);
    }

    private Ellipse2D.Double addEllipse(double xLeft, double yTop, double diameter, Area area){
        Ellipse2D.Double ellipse = new Ellipse2D.Double(xLeft, yTop, diameter, diameter);
        area.add(new Area(ellipse));
        return ellipse;
    }

    private void fillGaps(int startIdx, Area area, int i){
        Ellipse2D.Double start = ellipseCenters.get(startIdx);
        Ellipse2D.Double end = ellipseCenters.get(startIdx+1);
        double dx = end.getCenterX() - start.getCenterX();
        double dy = end.getCenterY() - start.getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        double combinedRadius = start.getWidth()/2 + end.getWidth()/2;
        if(dist <= combinedRadius || dist < minCircleDiameter){
            return;
        }

        //midpoint of the top-left corner of the shapes
        Point2D.Double midpoint = new Point2D.Double(start.x + (end.x - start.x)/2, start.y + (end.y - start.y)/2);
        double angle = Math.atan2(dy, dx);
        angle += Math.PI / 2.0 * (random.nextBoolean() ? 1 : -1);
        midpoint.x += 0.1 * dist * Math.cos(angle);
        midpoint.y += 0.1 * dist * Math.sin(angle);

        double diameter = getCircleDiameter();
        Ellipse2D.Double newMiddle = addEllipse(midpoint.x, midpoint.y, diameter, area);

        ellipseCenters.add(startIdx+1, newMiddle);

        fillGaps(startIdx+1, area, i+1);
        fillGaps(startIdx, area, i + 1);
    }

    private double getCircleDiameter(){
        return (1 - random.nextDouble() * random.nextDouble()) * (maxCircleDiameter - minCircleDiameter) + minCircleDiameter;
    }
}
