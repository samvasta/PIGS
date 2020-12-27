package com.samvasta.imagegenerator.generatorpack1.triangulation;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.PaletteFactory;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.ComplementaryPalette;
import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge.DelaunayGraph;
import com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge.Edge;
import com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge.Face;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sam on 7/7/2017.
 */
public enum DrawStrategy
{
    ColorFill
            {
                @Override
                void draw(Graphics2D g, DelaunayGraph graph, Dimension imgSize, MersenneTwister random)
                {
                    ColorPalette palette = PaletteFactory.getRandomPalette(random);
                    g.setColor(palette.getColorByIndex(0));
                    g.fillRect(0, 0, imgSize.width, imgSize.height);

                    double paletteWidth = random.nextDouble();
                    Iterator<Face> faceIterator = graph.faces.iterator();
                    while(faceIterator.hasNext()){
                        Polygon p = faceIterator.next().getPolygon();
                        paletteWidth = paletteWidth + random.nextGaussian()*0.2;
                        paletteWidth = MathHelper.clamp(paletteWidth, 0, 1);
                        Color currentCol = ColorUtil.getClose(palette.getColorSmooth(paletteWidth), random.nextDouble()*0.3);
                        g.setColor(currentCol);
                        g.fill(p);
                    }


                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(1 + random.nextInt(3)));

                    for(Edge e : graph.getEdges()){
                        Point2D.Double start = e.getVert().getPos();
                        Point2D.Double end = e.getNext().getVert().getPos();
                        g.drawLine((int)start.x, (int)start.y, (int)end.x, (int)end.y);
                    }
                }
            },
    Lines
            {
                @Override
                void draw(Graphics2D g, DelaunayGraph graph, Dimension imgSize, MersenneTwister random)
                {
                    double offsetMin = Math.min(imgSize.width, imgSize.height) / 100;
                    ColorPalette palette = new ComplementaryPalette(random);

                    g.setColor(palette.getColorByIndex(0));
                    g.fillRect(0, 0, imgSize.width, imgSize.height);

                    g.setColor(palette.getColorByIndex(1));
                    g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

                    List<Polygon> polygons = new ArrayList<>();
                    int minArea = Integer.MAX_VALUE;
                    int maxArea = Integer.MIN_VALUE;
                    for(Face f : graph.faces){
                        Polygon polygon = f.getInsidePoly(offsetMin + random.nextInt((int)(offsetMin/2.0)));
                        polygons.add(polygon);
                        int area = (int)(polygon.getBounds().getWidth() * polygon.getBounds().getHeight());
                        if(area < minArea){
                            minArea = area;
                        }
                        if(area > maxArea){
                            maxArea = area;
                        }
                    }

                    ColorPalette secondaryPalette = PaletteFactory.getAnalogousPalette(random, CeiLchColor.fromColor(palette.getColorByIndex(0)));
                    Color strokeCol = palette.getColorByIndex(1);

                    for(Polygon polygon : polygons){
                        int area = (int)(polygon.getBounds().getWidth() * polygon.getBounds().getHeight());

                        double paletteWidth = (double)(area - minArea) / (double)(maxArea - minArea);

                        Color fillCol = secondaryPalette.getColorSmooth(paletteWidth);
                        g.setColor(ColorUtil.getTransparent(fillCol, 0.25));
                        g.fill(polygon);

                        g.setColor(strokeCol);
                        g.draw(polygon);
                    }
                }
            },
    ;



    abstract void draw(Graphics2D g, DelaunayGraph graph, Dimension imgSize, MersenneTwister random);
}
