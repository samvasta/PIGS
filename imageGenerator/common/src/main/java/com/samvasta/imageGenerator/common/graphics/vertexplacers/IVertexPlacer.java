package com.samvasta.imageGenerator.common.graphics.vertexplacers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created by Sam on 7/5/2017.
 */
public interface IVertexPlacer
{
    /**
     * Populates a list with vertices that fit inside the bounds
     * @param pointList list to populate
     * @param bounds all points will be inside these bounds
     * @param random for rng
     */
    void placeVertices(List<Point2D.Double> pointList, Rectangle bounds, RandomGenerator random);
}
