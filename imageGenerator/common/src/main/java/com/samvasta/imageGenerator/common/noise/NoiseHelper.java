//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imageGenerator.common.noise.NoiseHelper
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imageGenerator.common.noise;

import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.RandomGenerator;

public class NoiseHelper {

    public static FastNoise getFractalSimplex(RandomGenerator random, int numOctaves){
        FastNoise noise = new FastNoise(random.nextInt());
        noise.setNoiseType(FastNoise.NoiseType.SimplexFractal);
        noise.setFractalOctaves(numOctaves);
        return noise;
    }
}
