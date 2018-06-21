package com.samvasta.common.models;

import java.awt.image.RenderedImage;

public class ImageBundle
{
    public final RenderedImage image;
    public final long seed;

    public ImageBundle(RenderedImage image, long seed){
        this.image = image;
        this.seed = seed;
    }
}
