package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.images.BlendMode;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.sun.istack.internal.NotNull;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class CompositeTexture implements ITexture
{
    private ITexture baseTexture;
    private List<TextureBlendModePair> actions;


    public CompositeTexture(@NotNull final ITexture baseTexture){
        this.baseTexture = baseTexture;
        actions = new LinkedList<>();
    }

    public void addTexture(@NotNull ITexture texture, BlendMode blendMode){
        if(texture == null){
            throw new IllegalArgumentException("texture cannot be null");
        }
        actions.add(new TextureBlendModePair(texture, blendMode));
    }

    public void insertTexture(@NotNull ITexture texture, BlendMode blendMode, int index){
        if(texture == null){
            throw new IllegalArgumentException("texture cannot be null");
        }
        actions.add(index, new TextureBlendModePair(texture, blendMode));
    }

    public int size(){
        return actions.size();
    }

    @Override
    public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random)
    {
        ProtoTexture texture = baseTexture.getTexture(textureSize, random);

        for(TextureBlendModePair tbmp : actions){
            texture.blendWithTexture(tbmp.texture, random, tbmp.blendMode);
        }

        return texture;
    }

    /**
     * Uses the {@link #baseTexture} to colorize.
     */
    @Override
    public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, RandomGenerator random){
        return baseTexture.colorize(protoTexture, palette, random);
    }
}
