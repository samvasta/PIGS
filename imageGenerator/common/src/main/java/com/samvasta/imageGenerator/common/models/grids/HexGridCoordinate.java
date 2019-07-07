package com.samvasta.imageGenerator.common.models.grids;

import java.awt.geom.Point2D;

/**
 * <p>Implementation of the haxagonal grid system. Uses a cube-based (3-axis) coordinate system</p>
 * <p>X goes from left to right, completely horizontally</p> //was Z
 * <p>Y goes from top-left to bottom-right</p>  //was X
 * <p>Z goes from top-right to bottom-left</p>  // was Y
 */
public class HexGridCoordinate implements IGridCoordinate {

    private static final double PI_OVER_SIX = Math.PI / 6.0;
    private static final double THREE_PI_OVER_SIX = 3.0 * Math.PI / 6.0;
    private static final double FIVE_PI_OVER_SIX = 5.0 * Math.PI / 6.0;
    private static final double APOTHEM_CONSTANT = (2.0 * Math.tan(PI_OVER_SIX));
    private static final double RADIUS_CONSTANT = (2.0 * Math.sin(PI_OVER_SIX));

    private static final double TWO_THIRD_PI = 2.0*Math.PI / 3.0;


    public final int y;
    public final int x;


    public HexGridCoordinate(int xIn, int yIn){
        x = xIn;
        y = yIn;
    }

    @Override
    public Point2D toScreenPoint(double sideLength, double angle, Point2D origin) {

        double apothemLen = 2.0* sideLength / (2.0 * Math.tan(PI_OVER_SIX)); //mult by 2 to get inner radius

        //initialize with x-axis because it is already aligned with the cartesian grid
        double xVal = x * Math.cos(angle) * apothemLen;
        double yVal = x * Math.sin(angle) * apothemLen;

        //Add y-axis
        xVal += Math.cos(TWO_THIRD_PI + angle) * apothemLen * y;
        yVal += Math.sin(TWO_THIRD_PI + angle) * apothemLen * y;

        //add origin
        xVal += origin.getX();
        yVal += origin.getY();

        return new Point2D.Double(xVal, yVal);
    }

    @Override
    public Point2D[] getCellShapePoints(double sideLength, double angle, Point2D origin){
        Point2D[] points = new Point2D[6];

        final Point2D center = toScreenPoint(sideLength, angle, origin);
        final double radius = sideLength / RADIUS_CONSTANT;

        points[0] = new Point2D.Double(center.getX() + radius*Math.cos(angle + PI_OVER_SIX),
                                       center.getY() + radius*Math.sin(angle + PI_OVER_SIX));

        points[1] = new Point2D.Double(center.getX() + radius*Math.cos(angle + THREE_PI_OVER_SIX),
                                       center.getY() + radius*Math.sin(angle + THREE_PI_OVER_SIX));

        points[2] = new Point2D.Double(center.getX() + radius*Math.cos(angle + FIVE_PI_OVER_SIX),
                                       center.getY() + radius*Math.sin(angle + FIVE_PI_OVER_SIX));

        points[3] = new Point2D.Double(center.getX() + radius*Math.cos(angle - FIVE_PI_OVER_SIX),
                                       center.getY() + radius*Math.sin(angle - FIVE_PI_OVER_SIX));

        points[4] = new Point2D.Double(center.getX() + radius*Math.cos(angle - THREE_PI_OVER_SIX),
                                       center.getY() + radius*Math.sin(angle - THREE_PI_OVER_SIX));

        points[5] = new Point2D.Double(center.getX() + radius*Math.cos(angle - PI_OVER_SIX),
                                       center.getY() + radius*Math.sin(angle - PI_OVER_SIX));

        return points;
    }

    @Override
    public double getAngleOffset(){
        return PI_OVER_SIX;
    }

    @Override
    public int getNumNeighbors(){
        return 4;
    }

    @Override
    public int getNumShapeSides() {
        return 6;
    }

    @Override
    public IGridCoordinate[] getNeighbors() {
        return new IGridCoordinate[]{
                new HexGridCoordinate(x + 1, y),
                new HexGridCoordinate(x - 1, y),
                new HexGridCoordinate(x, y+1),
                new HexGridCoordinate(x, y-1)
        };
    }

    @Override
    public Point2D getBoundingBoxMin(double sideLength, double angle, Point2D origin) {
        Point2D center = toScreenPoint(sideLength, angle, origin);
        double radius = sideLength / RADIUS_CONSTANT;
        double apothemLen = sideLength / APOTHEM_CONSTANT;

        return new Point2D.Double(center.getX() - apothemLen, center.getY() - radius);
    }

    @Override
    public Point2D getBoundingBoxMax(double sideLength, double angle, Point2D origin) {
        Point2D center = toScreenPoint(sideLength, angle, origin);
        double radius = sideLength / RADIUS_CONSTANT;
        double apothemLen = sideLength / APOTHEM_CONSTANT;

        return new Point2D.Double(center.getX() + apothemLen, center.getY() + radius);
    }


    @Override
    public boolean equals(IGridCoordinate other) {
        if(other instanceof HexGridCoordinate){
            HexGridCoordinate otherHex = (HexGridCoordinate)other;
            return  this.x == otherHex.x &&
                    this.y == otherHex.y;
        }
        return false;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof IGridCoordinate){
            return equals((IGridCoordinate)other);
        }
        return false;
    }

    @Override
    public int hashCode(){
        int hash = 17;
        hash = hash*31 + x;
        hash = hash*31 + y;
        return hash;
    }

    @Override
    public String toString(){
        return String.format("[%d,%d]", x,y);
    }
}
