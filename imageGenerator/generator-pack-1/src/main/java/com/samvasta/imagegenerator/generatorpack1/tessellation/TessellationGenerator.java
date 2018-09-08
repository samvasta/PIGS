package com.samvasta.imagegenerator.generatorpack1.tessellation;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.MonochromePalette;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.Transform2D;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class TessellationGenerator implements IGenerator
{
    private static final Logger logger = Logger.getLogger(TessellationGenerator.class);

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

        xScale = random.nextDouble() * imageSize.width/5 + imageSize.width / 50;
        if(random.nextDouble() < 0.2){
            yScale = random.nextDouble() * imageSize.height/5 + imageSize.height / 50;
        }
        else{
            yScale = xScale;
        }

        //Prevent the case where xScale and yScale are too different. We don't want something too far from a square ratio
        if(Math.abs(xScale - yScale) > imageSize.width / 10){
            yScale = xScale;
        }

        if(random.nextDouble() < 3){
            xShear   = random.nextDouble() * 0.125;
            yShear   = random.nextDouble() * 0.125;
        }
        else{
            xShear = 0;
            yShear = 0;
        }
        rotation = random.nextDouble() * Math.PI * 2.0;

//        xScale   = 200;
//        yScale   = xScale;
//        xShear   = 0;
//        yShear   = 0;
//        rotation = 0;

        logger.debug(String.format("%nxScale\t%s%nyScale\t%s%nxShear\t%s%nyShear\t%s%nrot\t%s", xScale, yScale, xShear, yShear, rotation));

        TilePattern pattern = TilePatternLibrary.INSTANCE.getRandomPattern(random);

        Transform2D noTranslation = getTransform(0, 0);
        noTranslation.finalizeLinearTransform();
        Dimension patternSize = pattern.getDimension(noTranslation);

        LinkedList<Transform2D> transformsToRender = new LinkedList<>();

        Transform2D transform = getTransform(
                imageSize.width/2 - patternSize.getWidth()/2 + random.nextDouble() * patternSize.getWidth(),
                imageSize.height/2 - patternSize.getHeight()/2 + random.nextDouble() * patternSize.getHeight()
        );
        transform.finalizeLinearTransform();
        transformsToRender.add(transform);

        fillCanvas(pattern, transformsToRender);
    }

    private void fillCanvas(TilePattern pattern, LinkedList<Transform2D> transformsToRender){
        ArrayList<Point2D.Double> visitedPoints = new ArrayList<>();

        ArrayList<Polygon> polygons = new ArrayList<>();

        while(!transformsToRender.isEmpty()){
            Transform2D transform = transformsToRender.removeFirst();
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
                int neighborX = (int)Math.round(p.getX());
                int neighborY = (int)Math.round(p.getY());

                g.setColor(Color.BLUE);
                g.drawOval(neighborX, neighborY, 3, 3);
                if(hasVisited(visitedPoints, neighborX, neighborY, MIN_POLYGON_DISTANCE)){
                    continue;
                }


                Transform2D neighborTransform = getTransform(p.getX(), p.getY());
                neighborTransform.setRotationAngle(transform.getRotationAngle() + neighborRotations[neighborIdx]);
                neighborTransform.finalizeLinearTransform();
                transformsToRender.addLast(neighborTransform);
                visitedPoints.add(new Point2D.Double(neighborTransform.getTranslateX(), neighborTransform.getTranslateY()));
            }
        }

        boolean useStroke = random.nextDouble() < 0.7;
        ColorPalette palette = new LinearLchPaletteBuilder(random).build();
        palette = new MonochromePalette(random);
        g.setColor(palette.getBiggestColor());
        g.fillRect(0,0,imageSize.width, imageSize.height);

        int strokeWeight = 2 + (int)(random.nextDouble() * 50 * xScale / imageSize.width);
        g.setStroke(new BasicStroke(strokeWeight, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        Color color = palette.getColor(random.nextDouble());
        double colorClosenessFactor = Math.abs(random.nextGaussian() * random.nextDouble()) + 0.05;
        for(Polygon poly : polygons){
            if(random.nextDouble() < 0.1){
                color = palette.getColor(random.nextDouble());
            }
            g.setColor(ColorUtil.getClose(color, colorClosenessFactor));
            g.fillPolygon(poly);
            if(useStroke){
                g.setColor(Color.BLACK);
                g.drawPolygon(poly);
            }
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
            xPoints[i] = (int)(Math.round(points[i].getX()));
            yPoints[i] = (int)(Math.round(points[i].getY()));
        }

        return new Polygon(xPoints, yPoints, points.length);
    }

    private boolean isAnyPointOnCanvas(Point2D.Double[] points){
        boolean isAbove = false;
        boolean isBelow = false;
        boolean isRight = false;
        boolean isLeft = false;

        for(int i = 0; i < points.length; i++){
            double x = points[i].getX();
            double y = points[i].getY();

            boolean isMiddleHorizontal = false;
            if(x < 0){
                isLeft = true;
            }
            else if(x > imageSize.width){
                isRight = true;
            }
            else{
                isMiddleHorizontal = true;
            }

            if(y < 0){
                isAbove = true;
            }
            else if(y > imageSize.height){
                isBelow = true;
            }
            else if(isMiddleHorizontal){
                return true;
            }
        }

        //corner case where two points span the width or span the height
        return (isAbove && isBelow) ^ (isLeft && isRight);
    }
}
