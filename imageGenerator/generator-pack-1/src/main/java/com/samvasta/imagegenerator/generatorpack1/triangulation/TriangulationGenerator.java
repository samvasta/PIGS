package com.samvasta.imagegenerator.generatorpack1.triangulation;

import com.samvasta.imageGenerator.common.graphics.vertexplacers.IVertexPlacer;
import com.samvasta.imageGenerator.common.graphics.vertexplacers.VertexPlacerFactory;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge.DelaunayGraph;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class TriangulationGenerator implements IGenerator {

    private static final Logger logger = Logger.getLogger(TriangulationGenerator.class);

    private static final String OPTION_MESH = "Mesh Kind (0=Tri, 1=Hex, 2=CMM, 3=Rand)";
    private static final List<IniSchemaOption<?>> OPTIONS = new ArrayList<IniSchemaOption<?>>() {{
        add(new IniSchemaOption<>(OPTION_MESH, 3, Integer.class));
    }};

    private List<ISnapshotListener> snapshotListeners;

    public HashSet<Point> knownVerticies = new HashSet<Point>();
    public HashSet<Point> knownTiles = new HashSet<Point>();

    public TriangulationGenerator() {
        snapshotListeners = new ArrayList<>();
    }

    @Override
    public boolean isOnByDefault() {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return OPTIONS;
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
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {

        Rectangle graphBounds = new Rectangle(-100, -100, imageSize.width+200, imageSize.height+200);


        IVertexPlacer vertexPlacer = VertexPlacerFactory.getRandomVertexPlacer(random, graphBounds);
        List<Point2D.Double> points = new ArrayList<>();

        vertexPlacer.placeVerticies(points, graphBounds, random);

        double maxBound = Math.max(imageSize.getHeight(), imageSize.getWidth());
        Point2D.Double p1 = new Point2D.Double(0, 3 * maxBound);
        Point2D.Double p2 = new Point2D.Double(-3 * maxBound, -3 * maxBound);
        Point2D.Double p3 = new Point2D.Double(3 * maxBound, 0);

        DelaunayGraph delaunayGraph = new DelaunayGraph(p1, p2, p3);
        for(Point2D.Double point : points){
            delaunayGraph.addPoint(point);
        }

        delaunayGraph.removeInitialPoints();

        int drawStrategy = random.nextInt(DrawStrategy.values().length);
        DrawStrategy.values()[drawStrategy].draw(g, delaunayGraph, imageSize, random);
    }
}
