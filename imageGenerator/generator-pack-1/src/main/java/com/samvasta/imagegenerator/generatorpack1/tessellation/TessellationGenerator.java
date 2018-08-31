package com.samvasta.imagegenerator.generatorpack1.tessellation;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.Transform2D;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class TessellationGenerator implements IGenerator
{
    //Polygons shouldn't really be smaller than 5 pixels anyways
    private static final double MIN_POLYGON_DISTANCE = 5.0;

    private List<ISnapshotListener> snapshotListeners;

    private Graphics2D g;
    private Dimension imageSize;
    private MersenneTwister random;
    private double xScale;
    private double yScale;
    private double xShear;
    private double yShear;
    private double rotation;

    public TessellationGenerator(){
        snapshotListeners = new ArrayList<>();
    }

    @Override
    public boolean isOnByDefault()
    {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings()
    {
        return new ArrayList<IniSchemaOption<?>>();
    }

    @Override
    public boolean isMultiThreadEnabled()
    {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener)
    {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener)
    {
        snapshotListeners.remove(listener);
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D gIn, Dimension imageSizeIn, MersenneTwister randomIn)
    {
        g = gIn;
        imageSize = imageSizeIn;
        random = randomIn;

        xScale   = random.nextDouble() * imageSize.width/5 + imageSize.width / 25;
        yScale   = random.nextDouble() * imageSize.height/5 + imageSize.height / 25;
        xShear   = random.nextGaussian() * 0.25;
        yShear   = random.nextGaussian() * 0.25;
        rotation = random.nextDouble() * Math.PI * 2.0;

//        xScale   = 400;
//        yScale   = xScale;
//        xShear   = 0;
//        yShear   = 0;
//        rotation = 0;


        TilePattern pattern = TilePatternLibrary.INSTANCE.getRandomPattern(random);

        Stack<Transform2D> transformsToRender = new Stack<>();

        Transform2D transform = getTransform(imageSize.width/2, imageSize.height/2);
        transform.finalizeLinearTransform();
        transformsToRender.add(transform);

        fillCanvas(pattern, transformsToRender);
    }

    private void fillCanvas(TilePattern pattern, Stack<Transform2D> transformsToRender){
        ArrayList<Point2D.Double> visitedPoints = new ArrayList<>();

        ArrayList<Polygon> polygons = new ArrayList<>();

        while(!transformsToRender.empty()){
            Transform2D transform = transformsToRender.pop();
            Point2D.Double[] boundingBox = pattern.getBoundingBox(transform);

            visitedPoints.add(new Point2D.Double(transform.getTranslateX(), transform.getTranslateY()));

            if(!isAnyPointOnCanvas(boundingBox)){
                continue;
            }

            List<Point2D.Double[]> polys = pattern.getPolygons(transform);

            for(int i = 0; i < polys.size(); i++){
                Polygon poly = getPolygon(polys.get(i));
                polygons.add(poly);
            }

            Point2D.Double[] neighborPoints = pattern.getNeighborCenters(transform);
            double[] neighborRotations = pattern.getNeighborRotations();
            for(int neighborIdx = 0; neighborIdx < neighborPoints.length; neighborIdx++){
                Point2D.Double p = neighborPoints[neighborIdx];
                int neighborX = (int)p.x;
                int neighborY = (int)p.y;

                g.setColor(Color.BLUE);
                g.drawOval((int)p.x, (int)p.y, 3, 3);
                if(hasVisited(visitedPoints, neighborX, neighborY, MIN_POLYGON_DISTANCE)){
                    continue;
                }


                Transform2D neighborTransform = getTransform(p.x, p.y);
                neighborTransform.setRotationAngle(transform.getRotationAngle() + neighborRotations[neighborIdx]);
                neighborTransform.finalizeLinearTransform();
                transformsToRender.push(neighborTransform);
                visitedPoints.add(new Point2D.Double(neighborTransform.getTranslateX(), neighborTransform.getTranslateY()));
            }
        }

        for(Polygon poly : polygons){
            g.setColor(new Color(255, 0,0,64));
            g.fillPolygon(poly);
            g.setColor(Color.BLACK);
            g.drawPolygon(poly);
        }
    }

    private boolean hasVisited(List<Point2D.Double> vistedPoints, double x, double y, double tolerance){
        double toleranceSq = tolerance * tolerance;
        for(Point2D.Double visited : vistedPoints){
            if(visited.distanceSq(x, y) <= toleranceSq){
                return true;
            }
        }
        return false;
    }

    private Transform2D getTransform(double translateX, double translateY){
        Transform2D transform = new Transform2D();
        transform.setTranslation(translateX, translateY);
        transform.setRotationAngle(rotation);
        transform.setScale(xScale, yScale);
        transform.setShear(xShear, yShear);
        return transform;
    }

    private Polygon getPolygon(Point2D.Double[] points){
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];

        for(int i = 0; i < points.length; i++){
            xPoints[i] = (int)(Math.round(points[i].x));
            yPoints[i] = (int)(Math.round(points[i].y));
        }

        return new Polygon(xPoints, yPoints, points.length);
    }

    private boolean isAnyPointOnCanvas(Point2D.Double[] points){
        for(int i = 0; i < points.length; i++){
            double x = points[i].x;
            double y = points[i].y;
            if(x <= imageSize.width && x >= 0 && y <= imageSize.height && y >= 0){
                return true;
            }
        }
        return false;
    }
}
