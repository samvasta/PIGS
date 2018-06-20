//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.helpers.ImageIOHelper
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.common.helpers;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageIOHelper
{
    private static final Logger LOGGER = Logger.getLogger(ImageIOHelper.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy_hh.mm.ss");

    private static int INDEX = 1;

    /**
     * Saves image with a random name
     * @param img image to save
     * @return The random name used to save the image
     */
    public static String saveImage(RenderedImage img, long seed, String outputDir) throws IOException {
        String name = String.format("%s_%s", DATE_FORMAT.format(new Date()), Long.toString(seed, 16));
        File outputFile = new File(String.format("%s/%s.png", outputDir, name));
        ImageIO.write(img, "png", outputFile);
        LOGGER.info(String.format("Saved image #%d%n\t\t%s", INDEX++, name));
        return name;
    }
}
