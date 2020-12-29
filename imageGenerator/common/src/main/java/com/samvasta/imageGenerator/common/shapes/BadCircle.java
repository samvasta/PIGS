package com.samvasta.imageGenerator.common.shapes;

import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.models.PolarVector;
import com.samvasta.imageGenerator.common.models.curves.HermiteSpline;
import com.samvasta.imageGenerator.common.noise.NoiseHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.*;

public class BadCircle implements Shape {

    private final double radius;
    private final Point2D.Double center;
    private final Path2D.Double path;
    private final double baddness;
    private final HermiteSpline spline;

    public BadCircle(final double radius, final Point2D center, final RandomGenerator random) {
        this(radius, center.getX(), center.getY(), 0.5, 100, random);
    }

    public BadCircle(final double radius, final Point2D center, final int resolution, final RandomGenerator random) {
        this(radius, center.getX(), center.getY(), 0.5, resolution, random);
    }

    public BadCircle(final double radiusIn, final double centerXIn, final double centerYIn, final RandomGenerator random) {
        this(radiusIn, centerXIn, centerYIn, 0.5, 100, random);
    }

    public BadCircle(final double radiusIn, final double centerXIn, final double centerYIn, final int resolution, final RandomGenerator random) {
        this(radiusIn, centerXIn, centerYIn, 0.5, resolution, random);
    }

    public BadCircle(final double radius, final Point2D center, final double baddnessIn, final RandomGenerator random) {
        this(radius, center.getX(), center.getY(), baddnessIn, 100, random);
    }

    public BadCircle(final double radius, final Point2D center, final double baddnessIn, final int resolution, final RandomGenerator random) {
        this(radius, center.getX(), center.getY(), baddnessIn, resolution, random);
    }

    public BadCircle(final double radiusIn, final double centerXIn, final double centerYIn, final double baddnessIn, final RandomGenerator random) {
        this(radiusIn, centerXIn, centerYIn, baddnessIn, 100, random);
    }

    public BadCircle(final double radiusIn, final double centerXIn, final double centerYIn, final double baddnessIn, final int resolution, final RandomGenerator random) {
        radius = radiusIn;
        center = new Point2D.Double(centerXIn, centerYIn);
        baddness = MathHelper.clamp01(baddnessIn);

        FastNoise blobDisplacement = NoiseHelper.getFractalSimplex(random, 8);
        PolarVector[] vectors = new PolarVector[resolution / 2];
        Point2D.Double[] blobPoints = new Point2D.Double[vectors.length];
        for (int i = 0; i < vectors.length; i++) {
            double percent = ((double) i / (double) vectors.length);
            double theta = Math.PI * 2.0 * percent;

            double displacement = NoiseHelper.sampleTiling1DNoise(blobDisplacement, 10 + (60 * baddness), percent);
            double magnitude = 1 + displacement;//.abs(2 + displacement) / 2.0;

            vectors[i] = new PolarVector(theta, magnitude);
            blobPoints[i] = vectors[i].toCartesianPoint();
        }

        //Center blobs & compute max magnitude for normalization in next step
        Point2D.Double centroid = GeomHelper.getCentroid(blobPoints);
        double maxMagnitude = 0;
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = vectors[i].add(PolarVector.fromCartesian(centroid).negative());
            double magnitude = vectors[i].magnitude;
            maxMagnitude = Math.max(magnitude, maxMagnitude);
        }

        //Normalize the magnitudes & compute final points
        for (int i = 0; i < vectors.length; i++) {
            double normalizedMagnitude = vectors[i].magnitude / maxMagnitude;
            blobPoints[i] = new PolarVector(vectors[i].angle, radius * normalizedMagnitude).toCartesianPoint();
        }


        spline = new HermiteSpline(blobPoints, 5);
        Point2D.Double[] splinePoints = spline.getInterpolatedPoints(resolution);
        path = new Path2D.Double();

        path.moveTo(splinePoints[0].x + Math.round(center.x), splinePoints[0].y + Math.round(center.y));
        for (int i = 1; i < splinePoints.length; i++) {
            path.lineTo(splinePoints[i].x + Math.round(center.x), splinePoints[i].y + Math.round(center.y));
        }
        path.lineTo(splinePoints[0].x + Math.round(center.x), splinePoints[0].y + Math.round(center.y));
        path.closePath();
    }

    public Point2D.Double getCenter() {
        return center;
    }

    public Path2D getPath() {
        return path;
    }

    public double getRadius(double angle) {
        Point2D.Double p = spline.interpolate(angle / (Math.PI * 2.0));
        return p.distance(0, 0);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(center.x - radius), (int) Math.round(center.y - radius), (int) Math.round(radius * 2), (int) Math.round(radius * 2));
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2);
    }

    @Override
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return path.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }

    private static final int NUM_SAMPLES = 4;
    private static final double CONE_ANGLE = Math.PI / 2.0;

    public boolean intersects(BadCircle other) {
        //Not 100% correct, just a decent approximation

        double directAngle = Math.atan2(other.center.y - center.y, other.center.x - center.x);

        //first check the direct angle
        if (intersectsAtAngle(other, directAngle)) {
            return true;
        }

        //then check 8 angles in both directions in a cone of 45deg centered around the direct angle
        for (double i = CONE_ANGLE / (2 * NUM_SAMPLES); i < CONE_ANGLE / 2; i += CONE_ANGLE / (2 * NUM_SAMPLES)) {
            if (intersectsAtAngle(other, directAngle + i) ||
                    intersectsAtAngle(other, directAngle - i)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsAtAngle(BadCircle other, double angle) {
        double negativeAngle = MathHelper.wrap(angle + Math.PI, 0, Math.PI * 2.0);
        double distToOtherEdge = other.getRadius(negativeAngle);
        double x = distToOtherEdge * Math.cos(negativeAngle) + other.center.x;
        double y = distToOtherEdge * Math.sin(negativeAngle) + other.center.y;
        Point2D.Double otherEdgePoint = new Point2D.Double(x, y);

        double angleToOtherEdge = Math.atan2(y - center.y, x - center.x);
        double distToEdge = getRadius(angleToOtherEdge);

        double dist = otherEdgePoint.distanceSq(center.x, center.y);
        return dist < distToEdge * distToEdge;
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return path.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }
}
