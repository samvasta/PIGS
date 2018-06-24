package com.samvasta.imageGenerator.common.models;

import com.samvasta.imageGenerator.common.helpers.IniHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;
import org.ini4j.Ini;

import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ImageCreator implements Callable<ImageBundle>
{
    private static final Logger LOGGER = Logger.getLogger(ImageCreator.class);

    private final long seed;
    private final MersenneTwister random;
    private final IGenerator generator;
    private final Dimension imageSize;
    private final Map<String, Object> settings;

    public ImageCreator(Dimension imageSize, IGenerator generatorIn, long seed, Ini settingsIn){
        this.imageSize = imageSize;
        this.generator = generatorIn;
        this.seed = seed;
        random = new MersenneTwister(seed);
        this.settings = new HashMap<>();

        List<IniSchemaOption<?>> options = generator.getIniSettings();
        String sectionName = generator.getClass().getSimpleName();
        for(IniSchemaOption<?> option : options){
            settings.put(option.getOptionName(), IniHelper.getValue(settingsIn, sectionName, option.getOptionName(), option.getValueType()));
        }
    }

    public ImageBundle call() throws Exception
    {
        Graphics2D g = null;
        try{
            BufferedImage bufferedImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
            g = (Graphics2D)bufferedImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            generator.generateImage(settings, g, imageSize, random);
            return new ImageBundle(bufferedImage, seed);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage(), e);
        }
        finally{
            if(g != null){
                g.dispose();
            }
        }
        throw new ExecutionException(new Exception("Something wrong in image generation..."));
    }
}