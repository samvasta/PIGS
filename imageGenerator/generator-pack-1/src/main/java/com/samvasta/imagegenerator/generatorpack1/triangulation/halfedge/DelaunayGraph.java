package com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge;

import java.awt.geom.Point2D;
import java.util.*;
import java.awt.Polygon;

/**
 * Created by Sam on 7/6/2017.
 */
public class DelaunayGraph
{
    private Vertex[] initialVerticies;
    private Polygon graphBounds;
    private Set<Vertex> verticies;
    public Set<Face> faces;

    public DelaunayGraph(Point2D.Double initP1, Point2D.Double initP2, Point2D.Double initP3){
        reset(initP1, initP2, initP3);
    }

    public void reset(Point2D.Double initP1, Point2D.Double initP2, Point2D.Double initP3){
        verticies = new HashSet<>();
        faces = new HashSet<>();

        Vertex v1 = new Vertex();
        v1.setPos(initP1);

        Vertex v2 = new Vertex();
        v2.setPos(initP2);

        Vertex v3 = new Vertex();
        v3.setPos(initP3);

        Edge e1 = new Edge();
        e1.setVert(v1);

        Edge e2 = new Edge();
        e2.setVert(v2);

        Edge e3 = new Edge();
        e3.setVert(v3);

        e1.setNext(e2);
        e2.setNext(e3);
        e3.setNext(e1);

        Face face = new Face();
        face.setEdge(e1);

        e1.setFace(face);
        e2.setFace(face);
        e3.setFace(face);

        verticies.add(v1);
        verticies.add(v2);
        verticies.add(v3);
        faces.add(face);
        initialVerticies = new Vertex[]{v1, v2, v3};
        graphBounds = new Polygon();
        graphBounds.addPoint((int)Math.round(initP1.x), (int)Math.round(initP1.y));
        graphBounds.addPoint((int)Math.round(initP2.x), (int)Math.round(initP2.y));
        graphBounds.addPoint((int)Math.round(initP3.x), (int)Math.round(initP3.y));
    }

    public boolean isVertex(Point2D.Double point){
        Vertex vertex = new Vertex();
        vertex.setPos(point);
        return verticies.contains(vertex);
    }

    private boolean isInitialVertex(Vertex vertex){
        return vertex.equals(initialVerticies[0]) || vertex.equals(initialVerticies[1]) || vertex.equals(initialVerticies[2]);
    }

    public Set<Edge> getEdges(){
        Set<Edge> edges = new HashSet<>();
        for(Face face : faces){
            Iterator<Edge> edgeIterator = face.getEdgeIterator();
            do{
                edges.add(edgeIterator.next());
            }while(edgeIterator.hasNext());
        }
        return edges;
    }

    public void removeInitialPoints(){
        List<Face> facesToRemove = new ArrayList<>();
        for(Face face : faces){
            Iterator<Edge> edgeIterator = face.getEdgeIterator();
            do{
                if(isInitialVertex(edgeIterator.next().getVert())){
                    facesToRemove.add(face);
                    break;
                }
            }while(edgeIterator.hasNext());
        }
        System.out.println("Removed " + facesToRemove.size() + " faces");
        faces.removeAll(facesToRemove);
        verticies.remove(initialVerticies[0]);
        verticies.remove(initialVerticies[1]);
        verticies.remove(initialVerticies[2]);
    }

    public void addPoint(Point2D.Double point){
        if(!graphBounds.contains(point)){
            throw new IllegalArgumentException("point not within graph bounds");
        }
        Face containerFace = getFaceContaining(point);
        if(containerFace == null){
            throw new IllegalStateException();
        }

        Vertex vertex = new Vertex();
        vertex.setPos(point);

        List<Edge> effectedEdges = new ArrayList<>();
        Iterator<Edge> edgeIterator = containerFace.getEdgeIterator();
        do{
            effectedEdges.add(edgeIterator.next());
        }while(edgeIterator.hasNext());

        if(effectedEdges.size() != 3){
            throw new IllegalStateException("Delaunay Graph should only contain triangle faces");
        }

        Edge eA = effectedEdges.get(0);
        Edge eB = effectedEdges.get(1);
        Edge eC = effectedEdges.get(2);

        Edge a2 = new Edge();
        Edge a2_p = new Edge();
        Edge a3 = new Edge();
        Edge a3_p = new Edge();
        Edge b2 = new Edge();
        Edge b2_p = new Edge();

        Face f1 = new Face();
        Face f2 = new Face();
        Face f3 = new Face();


        //Face 1
        //eA vert is already OK
        //eA twin is already OK
        eA.setFace(f1);
        eA.setNext(a2);

        a2.setVert(eB.getVert());
        a2.setTwin(a2_p);
        a2.setFace(f1);
        a2.setNext(a3);

        a3.setVert(vertex);
        a3.setTwin(a3_p);
        a3.setFace(f1);
        a3.setNext(eA);
        f1.setEdge(eA);
        faces.add(f1);


        //Face 2
        //eB vert is already OK
        //eB twin is already OK
        eB.setFace(f2);
        eB.setNext(b2);

        b2.setVert(eC.getVert());
        b2.setTwin(b2_p);
        b2.setFace(f2);
        b2.setNext(a2_p);

        a2_p.setVert(vertex);
        a2_p.setTwin(a2);
        a2_p.setFace(f2);
        a2_p.setNext(eB);
        f2.setEdge(eB);
        faces.add(f2);


        //Face 3
        //eC vert is already OK
        //eC twin is already OK
        eC.setFace(f3);
        eC.setNext(a3_p);

        a3_p.setVert(eA.getVert());
        a3_p.setTwin(a3);
        a3_p.setFace(f3);
        a3_p.setNext(b2_p);

        b2_p.setVert(vertex);
        b2_p.setTwin(b2);
        b2_p.setFace(f3);
        b2_p.setNext(eC);
        f3.setEdge(eC);
        faces.add(f3);

        verticies.add(vertex);
        faces.remove(containerFace);

        swapTest(eA);
        swapTest(eB);
        swapTest(eC);
    }

    public void swapTest(Edge edge){
        if(edge.getTwin() == null){
            if(!isInitialVertex(edge.getVert())){
                throw new IllegalStateException();
            }
            return;
        }

        Vertex a = edge.getNext().getVert();
        Vertex b = edge.getNext().getNext().getVert();
        Vertex c = edge.getVert();
        Vertex d = edge.getTwin().getNext().getNext().getVert();
        if(inCircle(a, b, c, d)){
            swapEdge(edge);
        }
    }

    public void swapEdge(Edge edge){
        Edge ab = edge;
        Edge ba = edge.getTwin();

        Edge ax = ba.getNext();
        Edge xb = ax.getNext();
        Edge bd = ab.getNext();
        Edge da = bd.getNext();

        Face old1 = ab.getFace();
        Face old2 = ba.getFace();
        faces.remove(old1);
        faces.remove(old2);

        Face f3 = new Face();
        Face f4 = new Face();

        faces.add(f3);
        faces.add(f4);

        Edge xd = new Edge();
        Edge dx = new Edge();

        xd.setVert(xb.getVert());
        xd.setTwin(dx);
        xd.setFace(f4);
        xd.setNext(da);

        dx.setVert(da.getVert());
        dx.setTwin(xd);
        dx.setFace(f3);
        dx.setNext(xb);

        ax.setFace(f4);
        ax.setNext(xd);

        xb.setFace(f3);
        xb.setNext(bd);

        bd.setFace(f3);
        bd.setNext(dx);

        da.setFace(f4);
        da.setNext(ax);

        f3.setEdge(dx);
        f4.setEdge(xd);

        swapTest(ax);
        swapTest(xb);
    }


    public Face getFaceContaining(Point2D.Double point){
        if(!isVertex(point)){
            for(Face face : faces){
                if(face.getPolygon().contains(point)){
                    return face;
                }
            }
        }

        return null;
    }

    /**
     * Check this out:
     * <a href="http://www.comp.nus.edu.sg/~tantc/ioi_training/CG/l10cs4235.pdf">FORMULA</a>
     * @see <a href="https://stackoverflow.com/a/2937973">det(4x4 matrix) formula</a>
     */
    private boolean inCircle(Vertex c1, Vertex c2, Vertex c3, Vertex check){
        if(check.equals(initialVerticies[0]) || check.equals(initialVerticies[1]) || check.equals(initialVerticies[2])){
            return false;
        }
        Point2D.Double a = c1.getPos();
        Point2D.Double b = c2.getPos();
        Point2D.Double c = c3.getPos();
        Point2D.Double d = check.getPos();

        //column 0
        double m00=1;
        double m10=1;
        double m20=1;
        double m30=1;

        //column 1
        double m01 = a.x;
        double m11 = b.x;
        double m21 = c.x;
        double m31 = d.x;

        //column 2
        double m02 = a.y;
        double m12 = b.y;
        double m22 = c.y;
        double m32 = d.y;

        //column 3
        double m03 = a.x*a.x + a.y*a.y;
        double m13 = b.x*b.x + b.y*b.y;
        double m23 = c.x*c.x + c.y*c.y;
        double m33 = d.x*d.x + d.y*d.y;


        double value;
        value =
                m03 * m12 * m21 * m30-m02 * m13 * m21 * m30-m03 * m11 * m22 * m30+m01 * m13 * m22 * m30+
                        m02 * m11 * m23 * m30-m01 * m12 * m23 * m30-m03 * m12 * m20 * m31+m02 * m13 * m20 * m31+
                        m03 * m10 * m22 * m31-m00 * m13 * m22 * m31-m02 * m10 * m23 * m31+m00 * m12 * m23 * m31+
                        m03 * m11 * m20 * m32-m01 * m13 * m20 * m32-m03 * m10 * m21 * m32+m00 * m13 * m21 * m32+
                        m01 * m10 * m23 * m32-m00 * m11 * m23 * m32-m02 * m11 * m20 * m33+m01 * m12 * m20 * m33+
                        m02 * m10 * m21 * m33-m00 * m12 * m21 * m33-m01 * m10 * m22 * m33+m00 * m11 * m22 * m33;
        return value < 0;
    }

}

