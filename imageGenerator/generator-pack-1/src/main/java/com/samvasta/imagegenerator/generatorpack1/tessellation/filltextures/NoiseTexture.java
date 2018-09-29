//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imagegenerator.generatorpack1.tessellation.filltextures.NoiseTexture
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imagegenerator.generatorpack1.tessellation.filltextures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import com.samvasta.imageGenerator.common.graphics.textures.TextureUtil;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NoiseTexture implements ITexture {

    private FastNoise noiseFunc;

    public NoiseTexture(FastNoise noiseFuncIn){
        noiseFunc = noiseFuncIn;
    }

    public FastNoise getNoiseFunc(){
        return noiseFunc;
    }

    @Override
    public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {

        ProtoTexture tex = new ProtoTexture(textureSize);
        for(int x = 0; x < textureSize.width; x++){
            for(int y = 0; y < textureSize.height; y++){
                double noiseValue = (noiseFunc.getValueFractal(x, y) + 1d)/2d;
                tex.setPixel(x, y, noiseValue);
            }
        }

        return tex;
    }

    @Override
    public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, RandomGenerator random) {
        return TextureUtil.colorizeInverseSmooth(protoTexture, palette);
    }
}