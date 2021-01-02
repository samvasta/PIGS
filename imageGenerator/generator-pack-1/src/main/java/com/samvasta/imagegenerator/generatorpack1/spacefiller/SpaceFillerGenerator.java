package com.samvasta.imagegenerator.generatorpack1.spacefiller;

import com.samvasta.imageGenerator.common.graphics.vertexplacers.IVertexPlacer;
import com.samvasta.imageGenerator.common.graphics.vertexplacers.VertexPlacerFactory;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import com.samvasta.imageGenerator.common.models.graphs.delaunay.DelaunayGraph;
import com.samvasta.imageGenerator.common.models.graphs.undirected.UndirectedEdge;
import com.samvasta.imageGenerator.common.models.graphs.undirected.UndirectedGraph;
import com.samvasta.imagegenerator.generatorpack1.triangulation.DrawStrategy;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceFillerGenerator extends SimpleGenerator {

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {

        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageSize.width,  imageSize.height);

        Rectangle graphBounds = new Rectangle(0, 0, imageSize.width, imageSize.height);

        IVertexPlacer vertexPlacer = VertexPlacerFactory.getRandomVertexPlacer(random, graphBounds);
        List<Point2D.Double> points = new ArrayList<>();

        vertexPlacer.placeVertices(points, graphBounds, random);

        double maxBound = Math.max(imageSize.getHeight(), imageSize.getWidth());
        Point2D.Double p1 = new Point2D.Double(0, 3 * maxBound);
        Point2D.Double p2 = new Point2D.Double(-3 * maxBound, -3 * maxBound);
        Point2D.Double p3 = new Point2D.Double(3 * maxBound, 0);

        DelaunayGraph delaunayGraph = new DelaunayGraph(p1, p2, p3);
        for(Point2D.Double point : points){
            delaunayGraph.addPoint(point);
        }

        delaunayGraph.removeInitialPoints();

        UndirectedGraph<Point2D.Double> graph = delaunayGraph.toUndirectedGraph();

        List<UndirectedEdge<Point2D.Double>> edges = graph.getEdges();

        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(1f, 1f, 1f, 0.5f));
        g.draw(new Line2D.Double(0,0,100,100));
        for(UndirectedEdge<Point2D.Double> e : edges) {
            g.draw(new Line2D.Double(e.vertexA, e.vertexB));
        }

        Point2D.Double start = new Point2D.Double(random.nextInt(imageSize.width), random.nextInt(imageSize.height));
        Point2D.Double end = new Point2D.Double(random.nextInt(imageSize.width), random.nextInt(imageSize.height));

        Point2D.Double closestVertexStart = getClosest(graph.getVertices(), start);
        Point2D.Double closestVertexEnd = getClosest(graph.getVertices(), end);

        List<UndirectedEdge<Point2D.Double>> path = graph.getShortestPath(closestVertexStart, closestVertexEnd);

        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(5));
        for(UndirectedEdge<Point2D.Double> e : path) {
            g.draw(new Line2D.Double(e.vertexA, e.vertexB));
        }

        /*
        # How To Convert A *Connected, Simple, Planar Graph* Into A Loop

        **Consider a connected, simple, planar graph `G` with edges `E` and vertices `V`**

        ## Variables
        `loop_edges` - a list of edges that will form the final loop.
        `visited_vertices` - a list of vertices which have been visited.

        ## Ground rules:
        1. When an edge is removed, add its two connected vertices to the `visited_vertices` list.
        2. When any vertex has exactly 2 edges, add the edges to `loop_edges` and add the vertex to `visited_vertices`
        3. Edges from `loop_edges` may never be removed.


        ## Procedure:
        1. Apply Ground Rule 2 until it is not applicable.

        2. If `visited_vertices` is empty:
           1. find a vertex with the lowest degree and remove a random edge.
           2. repeat step 2.1 until a vertex of degree 2 is found, then start over at step 1.

        3. Find a vertex from `visited_vertices` with the lowest degree. Until the vertex is degree 2, do:
           1. Remove all edges which create a loop of fixed vertices of `length < |V|`
           2. Remove one random edge connected to the vertex.

        4. Repeat from step 1 until no moves can be made.

         */

    }

    private static Point2D.Double getClosest(Point2D.Double[] options,  Point2D.Double target) {
        Point2D.Double closest = null;
        double minDist = Double.MAX_VALUE;
        for(Point2D.Double test : options) {
            double dist = target.distanceSq(test);
            if(dist < minDist) {
                closest = test;
                minDist = dist;
            }
        }
        return closest;
    }
}
