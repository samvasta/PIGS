//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imagegenerator.debuggenerator.it.NoiseTest
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imagegenerator.debuggenerator.it;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
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

        FastNoise noiseFunc = new FastNoise();
        noiseFunc.setSeed(random.nextInt());
        noiseFunc.setNoiseType(FastNoise.NoiseType.SimplexFractal);
        noiseFunc.setFractalOctaves(7);
        noiseFunc.setFractalGain(0.5f);

        int tilesX = imageSize.width;
        int tilesY = imageSize.height;
        int tileSize = 1;

        int xOffset = 0;
        int yOffset = 0;

        int centerX = xOffset + (tilesX/2);
        int centerY = yOffset + (tilesY/2);

        double vignetteRadius = tilesY * 0.55;

        for(int x = xOffset; x < tilesX+xOffset; x++){
            for(int y = yOffset; y < tilesY+yOffset; y++){
                double noiseValue = (noiseFunc.getValueFractal(x, y) + 1d)/2d;
                int dx = x - centerX;
                int dy = y - centerY;
                double distFromCenter = Math.sqrt(dx*dx + dy*dy);
                double percentDistFromCenter = MathHelper.clamp01(distFromCenter / vignetteRadius);
                noiseValue *= 1 - percentDistFromCenter;
                int v = (int)Math.round(noiseValue * 255d);
                g.setColor(new Color(v, v, v));
                g.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
            }
        }

    }
}
