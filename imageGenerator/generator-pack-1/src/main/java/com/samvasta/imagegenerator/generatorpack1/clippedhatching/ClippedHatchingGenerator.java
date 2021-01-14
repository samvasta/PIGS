package com.samvasta.imagegenerator.generatorpack1.clippedhatching;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.PaletteFactory;
import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.*;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Sam on 10/15/2016.
 */
public class ClippedHatchingGenerator extends SimpleGenerator
{
    private static final Logger logger = Logger.getLogger(ClippedHatchingGenerator.class);

    private Rectangle2D.Double getShapeBounds(Dimension imgSize, RandomGenerator random){
        int width, height, minx, miny;

        width = (int)(imgSize.width * ((random.nextDouble() * 0.3) + 0.4));
        height = (int)(imgSize.height * ((random.nextDouble() * 0.3) + 0.4));

        minx = (int)(( imgSize.width - width ) / 2d);
        miny = (int)(( imgSize.height - height ) / 2d);

        Rectangle2D.Double shapeBounds = new Rectangle2D.Double(minx, miny, width, height);
        return shapeBounds;
    }

    private Area getHatchingArea(Rectangle2D.Double shapeBounds, Polygon clipArea, RandomGenerator random){
        int hatchWidth = random.nextInt(10) + 3;
        double hatchAngle = random.nextDouble() * Math.PI;

        int hatchDeltaX = (int)Math.ceil(shapeBounds.getY() / Math.tan(hatchAngle));

        int startX;
        int endX;

        if(hatchDeltaX < 0){
            startX = (int)Math.floor(shapeBounds.getMinX());
        }
        else{
            startX = (int)Math.floor(shapeBounds.getMinX()) - hatchDeltaX;
        }
        endX = (int)Math.ceil(startX + shapeBounds.getWidth() + Math.abs(hatchDeltaX));


        Area hatchUnion = new Area();

        int currentX = startX;
        do{
            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            xPoints[0] = currentX;
            xPoints[1] = currentX + hatchWidth;
            xPoints[2] = currentX + hatchWidth + hatchDeltaX;
            xPoints[3] = currentX + hatchDeltaX;

            yPoints[0] = (int)Math.ceil(shapeBounds.getMaxY());
            yPoints[1] = (int)Math.ceil(shapeBounds.getMaxY());
            yPoints[2] = (int)Math.floor(shapeBounds.getMinY());
            yPoints[3] = (int)Math.floor(shapeBounds.getMinY());

            Polygon hatchPoly = new Polygon(xPoints, yPoints, 4);
            hatchUnion.add(new Area(hatchPoly));

            currentX += hatchWidth * 2;
        }while(currentX < endX);

        Area hatchArea = new Area(clipArea);
        hatchArea.intersect(hatchUnion);
        return hatchArea;
    }


    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        Rectangle2D.Double shapeBounds = getShapeBounds(imageSize, random);

        int numMiddlePoints = 3 + random.nextInt(3);

        int x, y;
        x = (int)shapeBounds.getMinX();
        y = (int)(shapeBounds.getCenterY() + (random.nextDouble() * shapeBounds.height * 0.3) - (shapeBounds.height * 0.15));

        Stack<Integer> ystack = new Stack<Integer>();

        int[] xpoints = new int[numMiddlePoints * 2 + 2];
        int[] ypoints = new int[numMiddlePoints * 2 + 2];

        xpoints[0] = x;
        ypoints[0] = y;

        for(int i = 0; i < numMiddlePoints; i++) {
            x += (int)(shapeBounds.width / (numMiddlePoints + 1));
            y = random.nextInt((int)(shapeBounds.height / 4));
            ystack.push(y);

            y += shapeBounds.getMinY();
            xpoints[i+1] = x;
            ypoints[i+1] = y;
        }
        x = (int)shapeBounds.getMaxX();
        y = ypoints[0];
        xpoints[numMiddlePoints + 1] = x;
        ypoints[numMiddlePoints + 1] = y;

        int pointidx = 2 + numMiddlePoints;

        while(ystack.isEmpty() == false){
            y = ystack.pop();
            y += shapeBounds.height * 0.667 + shapeBounds.getMinY();
            x -= (int)(shapeBounds.width / (numMiddlePoints + 1));
            xpoints[pointidx] = x;
            ypoints[pointidx] = y;
            pointidx++;
        }

        Polygon polygon = new Polygon(xpoints, ypoints, xpoints.length);
        Area areaPoly = new Area(polygon);


        double centerX = 0, centerY = 0;
        double polygonArea = GeomHelper.getArea(polygon);

        for(int i = 0; i < xpoints.length - 1; i++){
            centerX += (xpoints[i] + xpoints[i+1]) * ( (xpoints[i] * ypoints[i+1]) - (xpoints[i+1] * ypoints[i]) );
            centerY += (ypoints[i] + ypoints[i+1]) * ( (xpoints[i] * ypoints[i+1]) - (xpoints[i+1] * ypoints[i]) );
        }

        centerX = centerX / (6 * polygonArea);
        centerY = centerY / (6 * polygonArea);

        logger.info("Center @ " + centerX + ", " + centerY);

        int strokeWidth = 20;

        int[] xpoints2 = new int[xpoints.length], ypoints2 = new int[ypoints.length];

        for(int i = 0 ; i < xpoints.length; i++){
            double tempx = xpoints[i];
            double tempy = ypoints[i];

            double neighbor_x1, neighbor_y1, neighbor_x2, neighbor_y2;

            neighbor_x2 = xpoints[(i + 1) % xpoints.length];
            neighbor_y2 = ypoints[(i + 1) % ypoints.length];
            neighbor_x1 = xpoints[(i - 1 + xpoints.length) % xpoints.length];
            neighbor_y1 = ypoints[(i - 1 + ypoints.length) % ypoints.length];

            Point.Double prev = new Point2D.Double(neighbor_x1, neighbor_y1);
            Point2D.Double cur = new Point2D.Double(tempx, tempy);
            Point2D.Double next = new Point2D.Double(neighbor_x2, neighbor_y2);

            double angleToPrev = GeomHelper.getAngleTo(cur, prev);

            double bisectorAngle = GeomHelper.getBisectorAngle(prev, cur, next);

            //MAGIC:
            double halfBisector = Math.abs(angleToPrev + Math.PI/2.0 - bisectorAngle);

            // angle A = halfBisector, angle B is a right angle
            double angleC = Math.PI/2.0 - halfBisector;

            //use stroke width (side length) + known right angle + angle C in angle-angle-side alg. to find hypotenuse
            double hyp = -strokeWidth / Math.sin(angleC);

            xpoints2[i] = (int)((hyp * Math.cos(bisectorAngle)) + tempx);
            ypoints2[i] = (int)((hyp * Math.sin(bisectorAngle)) + tempy);


        }

        Polygon p2 = new Polygon(xpoints2, ypoints2, xpoints.length);

        areaPoly.subtract(new Area(p2));

        ColorPalette palette = PaletteFactory.getRandomPalette(random);
        g.setColor(palette.getColor(0));
        g.fillRect(0, 0, (int)imageSize.getWidth()+1, (int)imageSize.getHeight()+1);

        g.setColor(palette.getColor(1));
        g.fill(areaPoly);

        Area hatchArea = getHatchingArea(shapeBounds, polygon, random);

        g.fill(hatchArea);
    }
}
