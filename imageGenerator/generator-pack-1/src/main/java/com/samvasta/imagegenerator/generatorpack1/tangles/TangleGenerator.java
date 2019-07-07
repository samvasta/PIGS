package com.samvasta.imagegenerator.generatorpack1.tangles;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.PolarVector;
import com.samvasta.imageGenerator.common.models.grids.HexGridCoordinate;
import com.samvasta.imageGenerator.common.models.grids.IGridCoordinate;
import com.samvasta.imageGenerator.common.models.grids.SquareGridCoordinate;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class TangleGenerator implements IGenerator {
    private ArrayList<ISnapshotListener> snapshotListeners = new ArrayList<>();

    @Override
    public boolean isOnByDefault() {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    @Override
    public void generateImage(Map<String, Object> settings,
                              Graphics2D g,
                              Dimension imageSize,
                              MersenneTwister random) {
        final int width = imageSize.width;
        final int height = imageSize.height;
        final int sideLength = (int)(random.nextDouble() * height * 0.1) + (int)(height*0.05);
        final double angle = random.nextDouble() * Math.PI;
        final double tubeWidthPercent = random.nextDouble() * 0.65 + 0.2;

        final Point2D gridOrigin = new Point2D.Double(width/2.0, height/2.0);
        Map<IGridCoordinate, TangleGridCell> cellMap = new HashMap<>();

        ColorPalette palette = ColorUtil.getRandomPalette(random);
        Color fg = palette.getBiggestColor();
        Color bg = palette.getSmallestColor();

        g.setColor(bg);
        g.fillRect(0,0,width,height);

        int boundaryWidth = 0;
        double minX = boundaryWidth;
        double minY = boundaryWidth;
        double maxX = width-boundaryWidth;
        double maxY = height-boundaryWidth;

        TangleGridCell origin;
        if(random.nextFloat() < 0.5f){
            origin = addCell(new HexGridCoordinate(0,0), cellMap);
        }
        else{
            origin = addCell(new SquareGridCoordinate(0,0), cellMap);
        }

        //Fill the space
        LinkedList<IGridCoordinate> unvisitedCells = new LinkedList<>();
        LinkedList<IGridCoordinate> visitedCells = new LinkedList<>();
        unvisitedCells.push(origin.getCoordinate());
        while(unvisitedCells.size()>0){
            IGridCoordinate cell = unvisitedCells.pop();
            IGridCoordinate[] neighbors = cell.getNeighbors();

            if(!visitedCells.contains(cell)){
                visitedCells.push(cell);
            }

            for(IGridCoordinate neighbor : neighbors){
                Point2D min = neighbor.getBoundingBoxMin(sideLength, angle, gridOrigin);
                Point2D max = neighbor.getBoundingBoxMax(sideLength, angle, gridOrigin);

                if(min.getX() < maxX && max.getX() > minX &&
                   min.getY() < maxY && max.getY() > minY &&
                   !cellMap.containsKey(neighbor) &&
                   !unvisitedCells.contains(neighbor) &&
                   !visitedCells.contains(neighbor)){

                    addCell(neighbor, cellMap);
                    unvisitedCells.push(neighbor);
                }
            }
        }

        int cell = 0;
        for(IGridCoordinate key : cellMap.keySet()){
            if(cell++ % 5 == 0){
                takeSnapshot();
            }
            paintCell(g, fg, bg, cellMap.get(key), sideLength, angle, gridOrigin, tubeWidthPercent);
        }
    }

    private void takeSnapshot(){
        for(ISnapshotListener listener : snapshotListeners){
            listener.takeSnapshot();
        }
    }

    private TangleGridCell addCell(IGridCoordinate coord, Map<IGridCoordinate, TangleGridCell> cellMap){
        TangleGridCell cell = new TangleGridCell(coord);
        cellMap.put(coord, cell);
        return cell;
    }

    private void paintCell(Graphics2D g, Color fg, Color bg, TangleGridCell cell, double sideLength, double angle, Point2D origin, double tubeWidthPercent){
        double shadowTubeWidthPercent = 1;
        Color shadow = CeiLchColor.fromColor(bg).add(-40, 10, 0).toColor();
        Point2D[] points = cell.getCellShapePoints(sideLength, angle, origin);

        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];

        for(int i = 0; i < points.length; i++){
            xPoints[i] = (int)Math.round(points[i].getX());
            yPoints[i] = (int)Math.round(points[i].getY());
        }

        Polygon poly = new Polygon(xPoints, yPoints, points.length);

        g.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 32));
        g.drawPolygon(poly);
        g.setColor(fg);


        List<TubeSide> connectedPoints = cell.getConnectedPointsList(sideLength, angle, origin, tubeWidthPercent);
        for(int i = 0; i < connectedPoints.size(); i+=2){
            TubeSide side1 = connectedPoints.get(i);
            TubeSide side2 = connectedPoints.get(i+1);

            List<PointAndAngle> tubeSide1Points = side1.getTubePoints(tubeWidthPercent);
            List<PointAndAngle> tubeSide2Points = side2.getTubePoints(tubeWidthPercent);

            List<PointAndAngle> shadowSide1Points = side1.getTubePoints(shadowTubeWidthPercent);
            List<PointAndAngle> shadowSide2Points = side2.getTubePoints(shadowTubeWidthPercent);
            //Shadows
            Point2D intersection = GeomHelper.getIntersection(side1.getMidPoint(), side1.getAngle()+Math.PI/2.0, side2.getMidPoint(), side2.getAngle()+Math.PI/2.0);

            if(intersection != null){
                Color[] colors = new Color[]{ColorUtil.getTransparent(bg, 0), shadow, ColorUtil.getTransparent(bg, 0)};

                Point2D midpoint = side1.getMidPoint();

                double radius1 = shadowSide1Points.get(0).point.distance(intersection);
                double radius2 = shadowSide2Points.get(0).point.distance(intersection);

                double innerRadius = Math.min(radius1, radius2);
                double midRadius = midpoint.distance(intersection);
                double outerRadius = Math.max(radius1, radius2);

                float[] fractions = new float[]{(float)(innerRadius / outerRadius), (float)(midRadius / outerRadius), 1f};
                g.setPaint(new RadialGradientPaint(intersection, (float)outerRadius, fractions, colors));
            }
            else{
                Color[] colors = new Color[]{ColorUtil.getTransparent(bg, 0), shadow, ColorUtil.getTransparent(bg, 0)};
                float[] fractions = new float[]{0f, 0.5f, 1f};
                g.setPaint(new LinearGradientPaint(shadowSide1Points.get(0).point, shadowSide1Points.get(1).point, fractions, colors));
            }

            //Shadow
            drawTube(g, shadowSide1Points, shadowSide2Points);


            //Tube
            g.setColor(fg);
            drawTube(g, tubeSide1Points, tubeSide2Points);
        }

        for(int i = 0; i < connectedPoints.size(); i++) {
            TubeSide side1 = connectedPoints.get(i);

            List<PointAndAngle> tubeSide1Points = side1.getTubePoints(tubeWidthPercent);

            g.setColor(fg);
            Point2D orig1 = tubeSide1Points.get(0).point;
            Point2D orig2 = tubeSide1Points.get(1).point;
            Point2D p1;
            Point2D p2;
            Point2D p3;
            Point2D p4;

            p1 = GeomHelper.addPolar(orig1, 2, tubeSide1Points.get(0).angle);
            p2 = GeomHelper.addPolar(orig2, 2, tubeSide1Points.get(1).angle);
            p3 = GeomHelper.addPolar(orig2, -2, tubeSide1Points.get(1).angle);
            p4 = GeomHelper.addPolar(orig1, -2, tubeSide1Points.get(0).angle);
            Path2D path = new Path2D.Double();
            path.moveTo(p1.getX(), p1.getY());
            path.lineTo(p2.getX(), p2.getY());
            path.lineTo(p3.getX(), p3.getY());
            path.lineTo(p4.getX(), p4.getY());
            path.closePath();
            //g.setColor(new Color(255,0,0,128));
            g.fill(path);
            //g.draw(path);
        }
    }

    private void drawTube(Graphics2D g, List<PointAndAngle> side1Points, List<PointAndAngle> side2Points){
        Path2D path = new Path2D.Double();
        path.append(getConnectionPath(side1Points.get(0),  side2Points.get(1), false), true);
        path.append(getConnectionPath(side1Points.get(1), side2Points.get(0), true), true);

        g.setStroke(new BasicStroke(0));
        g.fill(path);
    }

    private Shape getConnectionPath(PointAndAngle p1, PointAndAngle p2, boolean reversePath){
        Point2D intersection = GeomHelper.getIntersection(p1.point, p1.angle+Math.PI/2.0, p2.point, p2.angle+Math.PI/2.0);

        if(intersection == null){
            if(reversePath){
                return new Line2D.Double(p2.getX(), p2.getY(), p1.getX(), p1.getY());
            }
            return new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
        else {
            double radius = p1.point.distance(intersection);

            double angleToP1 = GeomHelper.getAngleTo(intersection, p1.point);
            double angleToP2 = GeomHelper.getAngleTo(intersection, p2.point);

            double startAngle = -GeomHelper.rad2Deg(Math.min(angleToP1, angleToP2));

            double arcAngle;
            if(angleToP1 < angleToP2){
                arcAngle = -GeomHelper.getPositiveAngle(angleToP2 - angleToP1);
            }
            else{
                arcAngle = -GeomHelper.getPositiveAngle(angleToP1 - angleToP2);
            }
            while(arcAngle > Math.PI){
                //startAngle = -startAngle;
                arcAngle -= 2.0*Math.PI;
            }
            while(arcAngle < -Math.PI){
                //startAngle = -startAngle;
                arcAngle += 2.0*Math.PI;
            }
            arcAngle = GeomHelper.rad2Deg(arcAngle);

            if(reversePath){
                startAngle += arcAngle;
                arcAngle = -arcAngle;
            }

            return new Arc2D.Double(intersection.getX()-radius, intersection.getY()-radius, radius*2, radius*2, startAngle, arcAngle, Arc2D.OPEN);
        }
    }
}
