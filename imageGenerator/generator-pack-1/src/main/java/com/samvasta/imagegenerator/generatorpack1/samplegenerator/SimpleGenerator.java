package com.samvasta.imagegenerator.generatorpack1.samplegenerator;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.MonochromePalette;
import com.samvasta.imageGenerator.common.graphics.images.BlendMode;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.samvasta.imageGenerator.common.graphics.stamps.IStamp;
import com.samvasta.imageGenerator.common.graphics.textures.CompositeTexture;
import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import com.samvasta.imageGenerator.common.graphics.textures.TextureUtil;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleGenerator implements IGenerator
{
    public boolean isOnByDefault()
    {
        return true;
    }

    public List<IniSchemaOption<?>> getIniSettings()
    {
        //I need to make a change so I can test if my commit signing worked
        List<IniSchemaOption<?>> schemaOptions = new ArrayList<>();
        schemaOptions.add(new IniSchemaOption<>("text", "Hello World!", String.class));

        return schemaOptions;
    }

    public boolean isMultiThreadEnabled()
    {
        return true;
    }

    public void generateImage(final Map<String, Object> settings, final Graphics2D g, final Dimension imageSize, final MersenneTwister random) {
        final ColorPalette palette = new MonochromePalette(random);

        ITexture fadeUp = new ITexture()
        {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, MersenneTwister random)
            {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                double baseAlpha = 0.75 + (random.nextDouble() * 0.25);

                for(int x = 0; x < textureSize.width; x++){
                    for(int y = 0; y < textureSize.height; y++){
                        pixels[x + y * textureSize.width] = baseAlpha * (x + y * textureSize.width) / (pixels.length);
                    }
                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette1, MersenneTwister random){
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };
        ITexture fadeLeft = new ITexture()
        {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, MersenneTwister random)
            {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                double baseAlpha = 0.75 + (random.nextDouble() * 0.25);

                for(int i = 0; i < pixels.length; i++){
                    int x = i % textureSize.width;
                    int y = i / textureSize.width;
                    pixels[i] = baseAlpha * (y + x * textureSize.height) / (pixels.length);

                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette1, MersenneTwister random){
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };

        ITexture noise = new ITexture()
        {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, MersenneTwister random) {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                FastNoise noiseGen = new FastNoise(random.nextInt());

                for(int x = 0; x < textureSize.width; x++){
                    for(int y = 0; y < textureSize.height; y++){
                        pixels[x + y * textureSize.width] = (noiseGen.GetPerlin(x, y) + 1.0) * 0.5;
                    }
                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, MersenneTwister random) {
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };

        ITexture vignette = new ITexture()
        {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, MersenneTwister random){
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                int horizontalRadius = (int)(((textureSize.width) * random.nextDouble() * 0.3) + (textureSize.width*0.2));
                int verticalRadius = (int)(((textureSize.height) * random.nextDouble() * 0.3) + (textureSize.height*0.2));

                double hrSquared = horizontalRadius * horizontalRadius;
                double vrSquared = verticalRadius * verticalRadius;

                for(int x = 0; x < textureSize.width; x++){
                    for(int y = 0; y < textureSize.height; y++){

                        double xSquared = (textureSize.width/2)-x;

                        xSquared = xSquared * xSquared;

                        double ySquared = (textureSize.height/2)-y;

                        ySquared = ySquared * ySquared;

                        double one = (xSquared)/(hrSquared) + (ySquared)/(vrSquared);

                        double threshold = random.nextDouble();
                        if(one <= 1){
                            pixels[x + y * textureSize.width] = one * threshold;
                        }
                        else if(one * threshold * threshold > 1){
                            pixels[x + y * textureSize.width] = threshold;
                        }
                        else{
                            pixels[x + y * textureSize.width] = one * threshold;
                        }
                    }
                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, MersenneTwister random) {
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };

        g.setColor(palette.getBiggestColor());
        g.fillRect(0, 0, imageSize.width, imageSize.height);

        CompositeTexture backgroundTexture = new CompositeTexture(noise);
        backgroundTexture.addTexture(noise, BlendMode.LIGHTEN_ONLY);
        backgroundTexture.addTexture(noise, BlendMode.DARKEN_ONLY);
        backgroundTexture.addTexture(noise, BlendMode.LIGHTEN_ONLY);
        backgroundTexture.addTexture(vignette, BlendMode.SUBTRACT);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(TextureUtil.colorizeSingleColor(backgroundTexture.getTexture(imageSize, random), palette.getSmallestColor()), 0, 0, null);

        final CompositeTexture compositeTexture = new CompositeTexture(noise);
        compositeTexture.addTexture(fadeLeft, BlendMode.SCREEN);
        compositeTexture.addTexture(fadeUp, BlendMode.SCREEN);
//        compositeTexture.addTexture(noise, BlendMode.LIGHTEN_ONLY);
//        compositeTexture.addTexture(noise, BlendMode.LIGHTEN_ONLY);
//        compositeTexture.addTexture(noise, BlendMode.MULTIPLY);



        IStamp texturedCircleStamp = new IStamp(){
            @Override
            public void stamp(Graphics2D g, int x, int y){
                double diameter = imageSize.width * (random.nextDouble() * 0.15 + 0.01);
                Color col = palette.getColor(random.nextDouble());

                g.setClip(new Ellipse2D.Double(x, y, diameter, diameter));

                g.setColor(col);
                g.fillOval(x, y, (int)diameter, (int)diameter);

                BufferedImage textureImg = TextureUtil.colorizeSingleColor(compositeTexture.getTexture(new Dimension((int)diameter, (int)diameter), random), palette.getColor(random.nextDouble()));
                g.drawImage(textureImg, x, y, null);
                g.setClip(null);
            }
        };

        for(int i = 0; i < 17; i++){
            texturedCircleStamp.stamp(g, random.nextInt((int)(imageSize.width * 1.5)) - imageSize.width/4, random.nextInt((int)(imageSize.height * 1.5)) - imageSize.height/4);
        }
    }
}
