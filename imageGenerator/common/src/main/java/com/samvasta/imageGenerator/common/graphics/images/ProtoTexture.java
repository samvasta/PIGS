package com.samvasta.imageGenerator.common.graphics.images;

import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import com.samvasta.imageGenerator.common.helpers.MathHelper;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.util.Arrays;

public class ProtoTexture implements Cloneable
{
    private final double[] pixels;
    private final Dimension imageSize;

    public ProtoTexture(Dimension imageSize){
        this(imageSize, 0);
    }

    public ProtoTexture(Dimension imageSize, double defaultValue){
        this.imageSize = imageSize;
        pixels = new double[imageSize.width * imageSize.height];
        Arrays.fill(pixels, defaultValue);
    }

    private ProtoTexture(ProtoTexture original) {
        this.imageSize = original.imageSize;
        this.pixels = Arrays.copyOf(original.pixels, original.pixels.length);
    }

    public double[] getPixelsCopy(){
        return Arrays.copyOf(pixels, pixels.length);
    }

    public void setPixel(int x, int y, double value){
        pixels[x + y * imageSize.width] = value;
    }

    public void setPixels(int startX, int startY, int width, int height, double[] pixels){
        if(pixels.length != width * height){
            throw new IllegalArgumentException("length of pixels must equal (width * height)");
        }

        //Clip image if needed
        if(startX < 0){
            startX = 0;
        }
        if(startY < 0){
            startY = 0;
        }
        if(startX + width > imageSize.width){
            width = imageSize.width - startX;
        }
        if(startY + height > imageSize.height){
            height = imageSize.height - startY;
        }

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int destX = startX + x;
                int destY = startY + y;
                this.pixels[destX + destY * imageSize.width] = pixels[x + y * width];
            }
        }
    }

    public void blendWithTexture(ITexture texture, MersenneTwister random, BlendMode blendMode){
        ProtoTexture otherTex = texture.getTexture(this.imageSize, random);
        for(int i = 0; i < pixels.length; i++){
            pixels[i] = blendMode.applyBlend(pixels[i], otherTex.pixels[i]);
        }
    }

    public byte[] toByteArray(){
        byte[] bytes = new byte[pixels.length];
        for(int i = 0; i < pixels.length; i++){
            double pix = MathHelper.clamp01(pixels[i]);
            byte b = (byte)(0xff * pix);
            bytes[i] = b;
        }

        return bytes;
    }

    public int getWidth(){
        return imageSize.width;
    }

    public int getHeight(){
        return imageSize.height;
    }

    @Override
    public ProtoTexture clone(){
        return new ProtoTexture(this);
    }
}
