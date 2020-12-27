package com.samvasta.imageGenerator.common.models.grids;

import com.samvasta.imageGenerator.common.models.PrecisePoint2D;

import java.awt.geom.Point2D;

public class SquareGridCoordinate implements IGridCoordinate {

    private static final double PI_OVER_FOUR = Math.PI / 4.0;
    private static final double THREE_PI_OVER_FOUR = 3.0 * Math.PI / 4.0;

    private static final double RADIUS_CONSTANT = Math.sin(PI_OVER_FOUR);

    public final int x;
    public final int y;

    public SquareGridCoordinate(int xIn, int yIn) {
        x = xIn;
        y = yIn;
    }

    @Override
    public Point2D toScreenPoint(double sideLength, double angle, Point2D origin) {
        return new Point2D.Double(x * sideLength * Math.cos(angle) - y * sideLength * Math.sin(angle) + origin.getX(),
                x * sideLength * Math.sin(angle) + y * sideLength * Math.cos(angle) + origin.getY());
    }

    @Override
    public Point2D[] getCellShapePoints(double sideLength, double angle, Point2D origin) {
        Point2D[] points = new Point2D[4];

        final double radius = sideLength / RADIUS_CONSTANT;

        Point2D center = toScreenPoint(sideLength, angle, origin);

        points[0] = new Point2D.Double(center.getX() + radius * Math.cos(PI_OVER_FOUR + angle) / 2.0,
                center.getY() + radius * Math.sin(PI_OVER_FOUR + angle) / 2.0);

        points[1] = new Point2D.Double(center.getX() + radius * Math.cos(THREE_PI_OVER_FOUR + angle) / 2.0,
                center.getY() + radius * Math.sin(THREE_PI_OVER_FOUR + angle) / 2.0);

        points[2] = new Point2D.Double(center.getX() - radius * Math.cos(PI_OVER_FOUR + angle) / 2.0,
                center.getY() - radius * Math.sin(PI_OVER_FOUR + angle) / 2.0);

        points[3] = new Point2D.Double(center.getX() - radius * Math.cos(THREE_PI_OVER_FOUR + angle) / 2.0,
                center.getY() - radius * Math.sin(THREE_PI_OVER_FOUR + angle) / 2.0);

        return points;
    }

    @Override
    public double getAngleOffset() {
        return 0;
    }

    @Override
    public int getNumNeighbors() {
        return 4;
    }

    @Override
    public int getNumShapeSides() {
        return 4;
    }

    @Override
    public IGridCoordinate[] getNeighbors() {
        return new IGridCoordinate[]{
                new SquareGridCoordinate(x + 1, y),
                new SquareGridCoordinate(x - 1, y),
                new SquareGridCoordinate(x, y + 1),
                new SquareGridCoordinate(x, y - 1)
        };
    }

    @Override
    public Point2D getBoundingBoxMin(double sideLength, double angle, Point2D origin) {
        Point2D center = toScreenPoint(sideLength, angle, origin);
        return new Point2D.Double(center.getX() - sideLength / 2.0, center.getY() - sideLength / 2.0);
    }

    @Override
    public Point2D getBoundingBoxMax(double sideLength, double angle, Point2D origin) {
        Point2D center = toScreenPoint(sideLength, angle, origin);
        return new Point2D.Double(center.getX() + sideLength / 2.0, center.getY() + sideLength / 2.0);
    }

    @Override
    public boolean equals(IGridCoordinate other) {
        if (other instanceof SquareGridCoordinate) {
            SquareGridCoordinate otherSquare = (SquareGridCoordinate) other;
            return this.x == otherSquare.x &&
                    this.y == otherSquare.y;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof IGridCoordinate) {
            return equals((IGridCoordinate) other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        return hash;
    }

    public SquareGridCoordinate top() {
        return new SquareGridCoordinate(this.x, this.y - 1);
    }

    public SquareGridCoordinate right() {
        return new SquareGridCoordinate(this.x + 1, this.y);
    }

    public SquareGridCoordinate bottom() {
        return new SquareGridCoordinate(this.x, this.y + 1);
    }

    public SquareGridCoordinate left() {
        return new SquareGridCoordinate(this.x - 1, this.y);
    }
}
