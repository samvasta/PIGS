package com.samvasta.imagegenerator.generatorpack1.amoebas;

import com.samvasta.imageGenerator.common.noise.NoiseHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import com.samvasta.imageGenerator.common.shapes.BadCircle;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class SpaceFiller {
    public static final int K = 30;

    public static List<BadCircle> fillSpace(Rectangle2D bounds, double minSize, double maxSize, RandomGenerator random) {
        List<BadCircle> placed = new ArrayList<>();

        FastNoise noise = NoiseHelper.getFractalSimplex(random, 8);

        double currentSize = maxSize;
        double sizeStep = (maxSize - minSize) / 10;

        List<BadCircle> processingList = new ArrayList<>();

        // seed initial points
        int numFails = 0;
        do {
            Point2D.Double center = new Point2D.Double(bounds.getX() + random.nextDouble() * bounds.getWidth(), bounds.getY() + random.nextDouble() * bounds.getHeight());
            BadCircle candidate = new BadCircle(currentSize, center, random.nextDouble() * 0.3 + 0.6, 150, random);
            if (isValidLocation(candidate, bounds, placed, random, noise)) {
                placed.add(candidate);
                processingList.add(candidate);
                numFails = 0;
            } else {
                ++numFails;
            }
        } while (processingList.isEmpty() || (numFails < 50 && processingList.size() < 1000));

        while (currentSize >= minSize) {
            // Also seed with a few previously placed circles
            for (int i = 0; i < Math.max(4, placed.size() / 2); i++) {
                processingList.add(placed.get(random.nextInt(placed.size())));
            }

            // Place new circles
            while (!processingList.isEmpty()) {
                BadCircle anchor = processingList.remove(random.nextInt(processingList.size()));

                for (int i = 0; i < K; i++) {
                    double angle = 2d * Math.PI * random.nextDouble();
                    double distance = random.nextDouble() * currentSize * 2 + anchor.getRadius(angle)*1.1;
                    Point2D.Double center = new Point2D.Double(distance * Math.cos(angle) + anchor.getCenter().x, distance * Math.sin(angle) + anchor.getCenter().y);
                    BadCircle candidate = new BadCircle(currentSize, center, random.nextDouble() * 0.3 + 0.6, 150, random);

                    if (isValidLocation(candidate, bounds, placed, random, noise)) {
                        placed.add(candidate);
                        processingList.add(candidate);
                    }
                }
            }

            currentSize -= sizeStep;
        }

        return placed;
    }

    private static boolean isValidLocation(BadCircle candidate, Rectangle2D bounds, List<BadCircle> placed, RandomGenerator random, FastNoise noiseField) {
        if (!bounds.contains(candidate.getCenter())) {
            return false;
        }

        float normalizedX = 100f * (float) (candidate.getCenter().x - bounds.getX()) / (float) bounds.getWidth();
        float normalizedY = 100f * (float) (candidate.getCenter().y - bounds.getY()) / (float) bounds.getHeight();
        float noise = noiseField.getSimplexFractal(normalizedX, normalizedY);
        //Arbitrary threshold of 0 means about 50% of the area is not valid
        if (noise < 0) {
            return false;
        }

        for (BadCircle circle : placed) {
            if (circle.intersects(candidate)) {
                return false;
            }
        }
        return true;
    }

}
