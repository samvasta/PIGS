package com.samvasta.imageGenerator.common.models.parametricfunctions;

import java.awt.geom.Point2D;

public interface IParametricFunction {

    /**
     * <p>Gets the value of the parametric function at time = <code>time</code></p>
     * <p>Assumes time is in interval [0,1], although time values outside that range are acceptable</p>
     * @param time The time parameter of the parametric function
     * @return point with both X and Y in range [0,1]
     */
    Point2D get(double time);
}
