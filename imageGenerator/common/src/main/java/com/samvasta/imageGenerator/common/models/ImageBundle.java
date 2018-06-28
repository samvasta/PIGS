package com.samvasta.imageGenerator.common.models;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageBundle
{
    public final List<BufferedImage> images;
    public final long seed;

    public ImageBundle(List<BufferedImage> images, long seed){
        this.images = images;
        this.seed = seed;
    }
}
