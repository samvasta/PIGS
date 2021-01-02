package com.samvasta.imageGenerator.common.models.graphs.undirected;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PathNode {

    private int current;
    private PathNode prev;

    public PathNode(int currentIn, PathNode prevIn) {
        current = currentIn;
        prev = prevIn;
    }

    public int getCurrent(){
        return current;
    }

    public PathNode getPrev(){
        return prev;
    }

    public int getPathLength() {
        if(prev == null) {
            return 0;
        }
        else {
            return prev.getPathLength() + 1;
        }
    }

    public PathNode append(PathNode other) {
        PathNode thisCopy = this.copy();
        PathNode otherCopy = other.copy();

        otherCopy.getRoot().prev = thisCopy;
        return otherCopy;
    }

    public PathNode reverse() {
        if(this.prev == null) {
            return this.copy();
        }

        PathNode prevReverse = prev.reverse();
        PathNode root = new PathNode(this.current, null);
        prevReverse.getRoot().prev = root;
        return prevReverse;
    }

    public PathNode getRoot() {
        if(prev == null) {
            return this;
        }
        return prev.getRoot();
    }

    public PathNode copy() {
        if(prev == null) {
            return new PathNode(this.current, null);
        }

        return new PathNode(this.current, prev.copy());
    }

    public List<Integer> getPath() {
        LinkedList<Integer> path = new LinkedList<>();
        populatePathRecursive(path);
        return path;
    }

    public <T> List<UndirectedEdge<T>> toEdgeList(T[] vertices) {
        List<UndirectedEdge<T>> edgeList = new ArrayList<>();
        populateEdgeList(vertices, edgeList);
        return edgeList;
    }

    private <T> void populateEdgeList(T[] vertices, List<UndirectedEdge<T>> edgeList) {
        if(prev != null) {
            prev.populateEdgeList(vertices, edgeList);
            edgeList.add(new UndirectedEdge<>(vertices[prev.current], vertices[current]));
        }
    }

    private void populatePathRecursive(LinkedList<Integer> pathList) {
        pathList.addFirst(current);
        if(prev != null) {
            prev.populatePathRecursive(pathList);
        }
    }
}
