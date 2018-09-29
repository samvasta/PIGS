package com.samvasta.imageGenerator.common.models.curves;

import java.awt.geom.Point2D;

public interface IParametricCurve {
    public Point2D.Double interpolate(double percent);
}
