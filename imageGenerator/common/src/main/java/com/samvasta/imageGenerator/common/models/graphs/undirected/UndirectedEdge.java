package com.samvasta.imageGenerator.common.models.graphs.undirected;

public class UndirectedEdge<T> {

    public final T vertexA;
    public final T vertexB;

    public UndirectedEdge(T vertexAIn, T vertexBIn) {
        vertexA = vertexAIn;
        vertexB = vertexBIn;
    }
}
