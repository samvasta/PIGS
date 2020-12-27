package com.samvasta.imagegenerator.procgenideas.maps.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WaterTexture implements ITexture {
    @Override
    public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {
        return null;
    }

    @Override
    public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, RandomGenerator random) {
        return null;
    }
}
