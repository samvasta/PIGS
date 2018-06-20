//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.models.GeneratorContext
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.common.models;

import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;

public class GeneratorContext
{
    public final Dimension imageSize;
    public final MersenneTwister seedGenerator;
    public final String outputDir;

    public GeneratorContext(Dimension imageSize, MersenneTwister seedGenerator, String outputDir){
        this.imageSize = imageSize;
        this.seedGenerator = seedGenerator;
        this.outputDir = outputDir;
    }
}
