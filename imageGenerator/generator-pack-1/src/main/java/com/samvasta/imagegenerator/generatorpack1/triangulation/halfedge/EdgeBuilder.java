package com.samvasta.imagegenerator.generatorpack1.triangulation.halfedge;

/**
 * Created by Sam on 7/6/2017.
 */
public class EdgeBuilder{
    public static EdgeBuilder edge(){
        return new EdgeBuilder();
    }

    private Edge edge;
    private EdgeBuilder(){
        edge = new Edge();
    }

    public EdgeBuilder vert(Vertex vert){
        edge.setVert(vert);
        return this;
    }

    public EdgeBuilder face(Face face)
    {
        edge.setFace(face);
        return this;
    }

    public EdgeBuilder twin(Edge twin){
        edge.setTwin(twin);
        return this;
    }

    public EdgeBuilder next(Edge next){
        edge.setNext(next);
        return this;
    }

    private boolean isValid(){
        return edge.getFace() != null &&
                edge.getVert() != null &&
                edge.getNext() != null &&
                edge.getTwin() != null;
    }

    public Edge build(){
        if(isValid()){
            return edge;
        }
        else{
            throw new IllegalArgumentException();
        }
    }
}
