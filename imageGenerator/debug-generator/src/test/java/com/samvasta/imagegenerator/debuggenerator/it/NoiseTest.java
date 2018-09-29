//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imagegenerator.debuggenerator.it.NoiseTest
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imagegenerator.debuggenerator.it;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.images.BlendMode;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import com.samvasta.imageGenerator.common.graphics.textures.TextureUtil;
import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.noise.NoiseHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoiseTest implements IGenerator {
    private ArrayList<ISnapshotListener> snapshotListeners = new ArrayList<>();

    @Override
    public boolean isOnByDefault() {
        return false;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<IniSchemaOption<?>>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return false;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, imageSize.width, imageSize.height);

        ColorPalette palette = ColorUtil.getRandomPalette(random);

        NoiseTexture tex1 = new NoiseTexture();

        Dimension texSize = new Dimension(100, 100);

        float i = 1f;
        for(int y = 0; y < imageSize.height/texSize.height; y++){
            for(int x = 0; x < imageSize.width/texSize.width; x++){
                tex1.noiseFunc = NoiseHelper.getFractalSimplex(random, 4);
                tex1.noiseFunc.setFractalLacunarity(1f/i);
                ProtoTexture texture = tex1.getTexture(texSize, random);
                texture.blendWithTexture(tex1, random, BlendMode.SCREEN);
                BufferedImage img = tex1.colorize(texture, palette, random);
                g.drawImage(img, x * texSize.width, y*texSize.height,null);
                i+=0.2f;
            }
        }
    }

    private static class NoiseTexture implements ITexture {

        private FastNoise noiseFunc;

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
}
