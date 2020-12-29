package com.samvasta.imageGenerator.common.noise;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.RandomGenerator;

public class NoiseHelper {

    public static FastNoise getFractalSimplex(RandomGenerator random, int numOctaves){
        FastNoise noise = new FastNoise(random.nextInt());
        noise.setNoiseType(FastNoise.NoiseType.SimplexFractal);
        noise.setFractalOctaves(numOctaves);
        return noise;
    }

    /**
     * Samples a noise function in a circular path using polar coordinates provided.
     * Because simplex noise is continuous, the noise along the path will be continuous.
     * So the sample path will loop/tile seamlessly
     * @param noise noise function to sample
     * @param sampleRadius radius of the path to sample. Larger radii will tend to have "sharper" noise characteristics
     * @param percent how far along the path to sample. In range [0,1] (will be clamped if not in range)
     */
    public static double sampleTiling1DNoise(FastNoise noise, double sampleRadius, double percent) {
        //Samples in a circle by using the percent as theta in polar coordinates so we sample in a circular path
        // and because simplex noise is continuous, the sample path will also be continuous (loop/tile seamlessly)

        final double theta = Math.PI * 2.0 * MathHelper.clamp01(percent);

        final double x = sampleRadius * Math.cos(theta);
        final double y = sampleRadius * Math.sin(theta);

        return noise.getSimplex((float)x, (float)y);
    }
}
