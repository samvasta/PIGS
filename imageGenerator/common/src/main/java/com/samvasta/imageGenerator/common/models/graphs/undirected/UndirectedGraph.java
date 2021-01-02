package com.samvasta.imageGenerator.common.models.graphs.undirected;

import java.util.*;

public class UndirectedGraph<T> {

    private final T[] vertices;
    private final Set<Integer>[] edges;

    /**
     * Lookup table for the index of a vertex in the vertices or edges array
     */
    private final HashMap<T, Integer> vertexIndices;

    public UndirectedGraph(T... verticesIn) {
        vertices = verticesIn;
        edges = new Set[verticesIn.length];
        vertexIndices = new HashMap<>();

        for (int i = 0; i < verticesIn.length; i++) {
            vertexIndices.put(verticesIn[i], i);
            edges[i] = new HashSet<>();
        }
    }

    public T[] getVertices() {
        return vertices;
    }

    public List<UndirectedEdge<T>> getEdges() {
        List<UndirectedEdge<T>> edgesList = new ArrayList<>();
        for (T key : vertexIndices.keySet()) {
            int index = vertexIndices.get(key);

            for(Integer otherEndIndex : edges[index]) {

                //Filter out any other index less than current index
                // to avoid adding the same edge twice. (A->B and B->A)
                if(otherEndIndex >= index) {
                    T otherEnd = vertices[otherEndIndex];
                    edgesList.add(new UndirectedEdge<>(key, otherEnd));
                }
            }
        }
        return edgesList;
    }

    public boolean addEdge(T vertex1, T vertex2) {
        int index1 = vertexIndices.get(vertex1);
        int index2 = vertexIndices.get(vertex2);

        boolean result = true;
        result = result && edges[index1].add(index2);
        result = result && edges[index2].add(index1);
        return result;
    }

    public boolean removeEdge(T vertex1, T vertex2) {
        int index1 = vertexIndices.get(vertex1);
        int index2 = vertexIndices.get(vertex2);

        boolean result = true;
        result = result && edges[index1].remove(index2);
        result = result && edges[index2].remove(index1);
        return result;
    }

    public boolean hasEdge(T vertex1, T vertex2) {
        int index1 = vertexIndices.get(vertex1);
        int index2 = vertexIndices.get(vertex2);

        return edges[index1].contains(index2);
    }


    public int getShortestDistance(T vertexA, T vertexB) {
        return getShortestPath(vertexA, vertexB).size();
    }

    public List<UndirectedEdge<T>> getShortestPath(T vertexA, T vertexB) {
        // simple bi-directional bfs algorithm
        int indexA = vertexIndices.get(vertexA);
        int indexB = vertexIndices.get(vertexB);

        Queue<PathNode> queueA = new LinkedList<>();
        Queue<PathNode> queueB = new LinkedList<>();


        //visited from side A
        Map<Integer, PathNode> visitedA = new HashMap<>();
        //visited from side B
        Map<Integer, PathNode> visitedB = new HashMap<>();

        visitedA.put(indexA, new PathNode(indexA, null));
        visitedB.put(indexB, new PathNode(indexB, null));

        queueA.add(new PathNode(indexA, null));
        queueB.add(new PathNode(indexB, null));

        // Both queues need to be empty to exit the while loop.
        while (!queueA.isEmpty() || !queueB.isEmpty()) {
            //take 1 step from side A
            PathNode path = bfsStep(queueA, visitedA, visitedB);
            if (path != null) {
                return path.toEdgeList(vertices);
            }

            //take 1 step from side B
            path = bfsStep(queueB, visitedB, visitedA);
            if (path != null) {
                //reverse path because it goes B->A but we want A->B
                return path.reverse().toEdgeList(vertices);
            }
        }

        return null;
    }

    private PathNode bfsStep(Queue<PathNode> queueFromThisSide, Map<Integer, PathNode> visitedFromThisSide, Map<Integer, PathNode> visitedFromOtherSide) {
        if (!queueFromThisSide.isEmpty()) {
            PathNode next = queueFromThisSide.remove();

            int vertexIndex = next.getCurrent();
            Set<Integer> adjacentNodeIndices = edges[vertexIndex];

            for (int adjacentIndex : adjacentNodeIndices) {
                // If the visited nodes, starting from the other direction,
                // contain the "adjacent" node of "next", then we can terminate the search
                if (visitedFromOtherSide.containsKey(adjacentIndex)) {
                    PathNode otherSidePath = visitedFromOtherSide.get(adjacentIndex);
                    return next.append(otherSidePath.reverse());
                } else if (!visitedFromThisSide.containsKey(adjacentIndex)) {
                    PathNode node = new PathNode(adjacentIndex, next);
                    visitedFromThisSide.put(adjacentIndex, node);
                    queueFromThisSide.add(node);
                }
            }
        }

        return null;
    }

}
