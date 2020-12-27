package com.samvasta.imageGenerator.common.ut.sequences;

import com.samvasta.imageGenerator.common.models.curves.HermiteCurve;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HermiteCurveTest {

    @Test
    public void testHermiteCurve(){
        Point2D.Double start = new Point2D.Double(0,0);
        Point2D.Double end = new Point2D.Double(1,1);
        Point2D.Double tanStart = new Point2D.Double(-1.7,-1);
        Point2D.Double tanEnd = new Point2D.Double(1,-1.5);

        HermiteCurve curve = new HermiteCurve(start, end, tanStart, tanEnd);

        double percent = 0.4;

        Point2D.Double interpPoint = curve.interpolate(percent);

        assertEquals(0.0112, interpPoint.getX(), 1e-4);
        assertEquals(0.352, interpPoint.getY(), 1e-3);

        percent = 0.45;

        interpPoint = curve.interpolate(percent);

        assertEquals(0.0825, interpPoint.getX(), 1e-4);
        assertEquals(0.4562, interpPoint.getY(), 1e-4);
    }
}
