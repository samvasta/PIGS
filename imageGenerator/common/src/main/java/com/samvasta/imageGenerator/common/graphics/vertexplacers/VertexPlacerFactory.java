package com.samvasta.imageGenerator.common.graphics.vertexplacers;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;

public class VertexPlacerFactory {
    public static IVertexPlacer getRandomVertexPlacer(RandomGenerator random, Rectangle bounds){
        int decision = random.nextInt(3);
        if(decision == 0){
            double divisor = 1000d + (int)Math.abs(random.nextGaussian() * 10000d);
            int numPoints = (int)(bounds.getWidth() * bounds.getHeight() / divisor);
            return new UniformVertexPlacer(numPoints);
        }
        else if(decision == 1){
            double cellSize = Math.min(bounds.getWidth(), bounds.getHeight()) / 20;
            cellSize += random.nextGaussian() * 100;
            cellSize = Math.abs(cellSize) + JitterGridPlacer.MIN_CELL_SIZE;
            return new JitterGridPlacer(cellSize);
        }
        else if(decision == 2){
            double divisor = 2 + Math.abs(random.nextGaussian() * 8);
            double minDistance = Math.min(bounds.getWidth(), bounds.getHeight()) / divisor;
            return new PoissonVertexPlacer(minDistance);
        }
        else{
            throw new IllegalStateException();
        }
    }
}
