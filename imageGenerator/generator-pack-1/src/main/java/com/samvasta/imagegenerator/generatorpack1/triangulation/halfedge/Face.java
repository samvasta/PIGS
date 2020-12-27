package com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Created by Sam on 7/6/2017.
 */
public class Face {
    private Edge edge;

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null");
        }
        this.edge = edge;
    }

    public boolean contains(Vertex vert) {
        return contains(vert.getPos());
    }

    public boolean contains(Point2D.Double point) {
        return getPolygon().contains(point);
    }

    public Polygon getPolygon() {
        Polygon polygon = new Polygon();
        Edge currentEdge = edge;
        do {
            if (currentEdge == null) {
                throw new IllegalStateException("The face is not a closed polygon");
            }
            Point2D.Double point = currentEdge.getVert().getPos();
            polygon.addPoint((int) Math.round(point.x), (int) Math.round(point.y));

            currentEdge = currentEdge.getNext();
        } while (!currentEdge.equals(edge));
        return polygon;
    }

    public Polygon getInsidePoly(double offset) {
        Polygon polygon = new Polygon();
        Edge currentEdge = edge;
        do {
            if (currentEdge == null) {
                throw new IllegalStateException("The face is not a closed polygon");
            }

            Point2D.Double point1 = currentEdge.getVert().getPos();
            Point2D.Double point2 = currentEdge.getNext().getVert().getPos();
            Point2D.Double point3 = currentEdge.getNext().getNext().getVert().getPos();

            double a = Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
            double b = Math.sqrt((point2.x - point3.x) * (point2.x - point3.x) + (point2.y - point3.y) * (point2.y - point3.y));
            double c = Math.sqrt((point1.x - point3.x) * (point1.x - point3.x) + (point1.y - point3.y) * (point1.y - point3.y));

            double aSq = a * a;
            double bSq = b * b;
            double cSq = c * c;

            double angle = Math.acos((-cSq + aSq + bSq) / (2 * a * b));
            double halfAngle = angle / 2d;
            double finalAngle = halfAngle + Math.atan2(point3.y - point2.y, point3.x - point2.x);


            int x = (int) Math.round(point2.x + offset * Math.cos(finalAngle));
            int y = (int) Math.round(point2.y + offset * Math.sin(finalAngle));

            polygon.addPoint(x, y);

            currentEdge = currentEdge.getNext();
        } while (!currentEdge.equals(edge));
        return polygon;
    }

    public Iterator<Vertex> getVertexIterator() {
        return new Iterator<Vertex>() {
            final Edge startEdge = edge;
            Edge currentEdge = startEdge;

            @Override
            public boolean hasNext() {
                return !currentEdge.getNext().equals(startEdge);
            }

            @Override
            public Vertex next() {
                Vertex vert = currentEdge.getVert();
                currentEdge = currentEdge.getNext();
                return vert;
            }

            @Override
            public void remove() {
                // Nothing to do
            }
        };
    }

    public Iterator<Edge> getEdgeIterator() {
        return new Iterator<Edge>() {
            final Edge startEdge = edge;
            Edge currentEdge = startEdge;

            @Override
            public boolean hasNext() {
                return !currentEdge.equals(startEdge);
            }

            @Override
            public Edge next() {
                Edge edge = currentEdge;
                currentEdge = currentEdge.getNext();
                return edge;
            }

            @Override
            public void remove() {
                // Nothing to do
            }
        };
    }
}