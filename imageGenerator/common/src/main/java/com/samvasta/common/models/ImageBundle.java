//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.models.ImageBundle
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
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
