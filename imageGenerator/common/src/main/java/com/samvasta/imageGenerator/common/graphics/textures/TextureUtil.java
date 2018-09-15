package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.SingleColorPalette;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextureUtil
{
    private static final ColorPalette MASK_PALETTE = new SingleColorPalette(Color.BLUE);

//    public static ProtoTexture getBlendedImage(ITexture texture1, ITexture texture2, Dimension textureSize, MersenneTwister random, BlendMode blendMode){
//        ProtoTexture img = texture1.getTexture(textureSize, palette, random);
//        ProtoTexture img2 = texture2.getTexture(textureSize, palette, random);
//
//        img.blendPixels(0, 0, textureSize.width, textureSize.height, img2.getPixelsCopy(), blendMode);
//        return img;
//    }
//
//    public static ProtoTexture getMaskedTexture(ITexture textureGenerator, ITexture maskGenerator, Dimension textureSize, MersenneTwister random){
//        ProtoTexture texture = textureGenerator.getTexture(textureSize, palette, random);
//        ProtoTexture mask = maskGenerator.getTexture(textureSize, MASK_PALETTE, random);
//
//        texture.blendPixels(0, 0, textureSize.width, textureSize.height, mask.getPixelsCopy(), BlendMode.BLUE_AS_ALPHA);
//
//        return texture;
//    }



    public static BufferedImage colorizeSingleColor(ProtoTexture protoTexture, Color color){
        int width = protoTexture.getWidth();
        int height = protoTexture.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = new int[width * height];
        pixels = img.getRGB(0, 0, width, height, pixels, 0, width);

        byte[] textureBytes = protoTexture.toByteArray();

        int colorInt = color.getRGB();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                pixels[x + y * width] = (colorInt & 0x00ffffff) | (textureBytes[x + y * width] << 24);
            }
        }

        img.setRGB(0, 0, width, height, pixels, 0, width);

        return img;
    }



    public static BufferedImage colorizeSmooth(ProtoTexture protoTexture, ColorPalette palette){
        int width = protoTexture.getWidth();
        int height = protoTexture.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = new int[width * height];
        pixels = img.getRGB(0, 0, width, height, pixels, 0, width);

        double[] texture = protoTexture.getPixelsCopy();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                pixels[x + y * width] = palette.getColorSmooth(texture[x + y * width]).getRGB();
            }
        }

        img.setRGB(0, 0, width, height, pixels, 0, width);

        return img;
    }

    public static BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette){
        int width = protoTexture.getWidth();
        int height = protoTexture.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = new int[width * height];
        pixels = img.getRGB(0, 0, width, height, pixels, 0, width);

        double[] texture = protoTexture.getPixelsCopy();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                pixels[x + y * width] = palette.getColor(texture[x + y * width]).getRGB();
            }
        }

        img.setRGB(0, 0, width, height, pixels, 0, width);

        return img;
    }
}
