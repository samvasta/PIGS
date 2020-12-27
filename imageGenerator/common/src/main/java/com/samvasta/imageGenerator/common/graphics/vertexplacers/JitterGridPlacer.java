package com.samvasta.imageGenerator.common.graphics.vertexplacers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created by Sam on 7/5/2017.
 */
public class JitterGridPlacer implements IVertexPlacer
{
    public static final int MIN_CELL_SIZE = 20;

    private double cellSize;

    public double getCellSize()
    {
        return cellSize;
    }

    public void setCellSize(double cellSize)
    {
        this.cellSize = cellSize;
    }

    public JitterGridPlacer(double cellSizeIn){
        setCellSize(cellSizeIn);
    }

    @Override
    public void placeVerticies(List<Point2D.Double> pointList, Rectangle bounds, RandomGenerator random)
    {
        double initialCellX = -1 * random.nextDouble() * cellSize + bounds.getMinX();
        double cellY = -1 * random.nextDouble() * cellSize + bounds.getMinY();

        while(cellY < bounds.getMaxY()){
            double cellX = initialCellX;
            while(cellX < bounds.getMaxX()){
                Rectangle cell = new Rectangle((int)cellX, (int)cellY, (int)cellSize, (int)cellSize);
                Point2D.Double p = new Point2D.Double(random.nextInt((int)cell.getWidth()) + cell.getMinX(), random.nextInt((int)cell.getHeight()) + cell.getMinY());
                pointList.add(p);
                cellX += cellSize;
            }
            cellY += cellSize;
        }
    }
}
