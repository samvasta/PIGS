package com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge;

/**
 * Created by Sam on 7/6/2017.
 */
public class Edge{
    private Vertex vert;
    private Edge twin;
    private Face face;
    private Edge next;

    public Vertex getVert(){
        return vert;
    }

    public void setVert(Vertex vertIn){
        if(vertIn == null){
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        this.vert = vertIn;
    }

    public Edge getTwin(){
        return twin;
    }

    public void setTwin(Edge twinIn){
        if(twinIn == null){
            throw new IllegalArgumentException("Edge cannot be null");
        }
        this.twin = twinIn;
    }

    public Face getFace(){
        return face;
    }

    public void setFace(Face faceIn){
        if(faceIn == null){
            throw new IllegalArgumentException("Face cannot be null");
        }
        this.face = faceIn;
    }

    public Edge getNext(){
        return next;
    }

    public void setNext(Edge nextIn){
        if(nextIn == null){
            throw new IllegalArgumentException("Edge cannot be null");
        }
        this.next = nextIn;
    }

}
