package com.samvasta.imageGenerator.common.graphics.images;

import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.helpers.MathHelper;

public enum BlendMode
{
    /**
     * {@link #applyBlend(double, double)} returns {@code color2}
     * @see <a href="https://en.wikipedia.org/wiki/Blend_modes#Normal_blend_mode">https://en.wikipedia.org/wiki/Blend_modes#Normal_blend_mode</a>
     */
    NORMAL {
        @Override
        public double applyBlend(double value1, double value2){
            return value2;
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns addition of {@code color1} and {@code color2}
     */
    ADD{
        @Override
        public double applyBlend(double value1, double value2){
            return MathHelper.clamp01(value1 + value2);
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns subtraction of {@code color1} and {@code color2}
     */
    SUBTRACT{
        @Override
        public double applyBlend(double value1, double value2){
            return MathHelper.clamp01(value1 - value2);
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns multiplication of {@code color1} and {@code color2}
     * @see <a href="https://en.wikipedia.org/wiki/Blend_modes#Multiply">https://en.wikipedia.org/wiki/Blend_modes#Multiply</a>
     */
    MULTIPLY{
        @Override
        public double applyBlend(double value1, double value2){
            return MathHelper.clamp01(value1 * value2);
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns division of {@code color1} / {@code color2}
     * @see <a href="https://en.wikipedia.org/wiki/Blend_modes#Divide">https://en.wikipedia.org/wiki/Blend_modes#Multiply</a>
     */
    DIVIDE{
        @Override
        public double applyBlend(double value1, double value2){
            return MathHelper.clamp01(value1 / value2);
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns screen of {@code color1} and {@code color2}
     * @see <a href="https://en.wikipedia.org/wiki/Blend_modes#Screen">https://en.wikipedia.org/wiki/Blend_modes#Screen</a>
     */
    SCREEN{
        @Override
        public double applyBlend(double value1, double value2){
            return 1 - (1 - value1) * (1 - value2);
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns the darker (minimum) of {@code color1} and {@code color2}
     * @see <a href="https://en.wikipedia.org/wiki/Blend_modes#Darken_Only">https://en.wikipedia.org/wiki/Blend_modes#Darken_Only</a>
     */
    DARKEN_ONLY{
        @Override
        public double applyBlend(double value1, double value2){
            return Math.min(value1, value2);
        }
    },

    /**
     * {@link #applyBlend(double, double)} returns the lighter (maximum) of {@code color1} and {@code color2}
     * @see <a href="https://en.wikipedia.org/wiki/Blend_modes#Lighten_Only">https://en.wikipedia.org/wiki/Blend_modes#Lighten_Only</a>
     */
    LIGHTEN_ONLY{
        @Override
        public double applyBlend(double value1, double value2){
            return Math.max(value1, value2);
        }
    },
    ;

    BlendMode(){}

    public abstract double applyBlend(double value1, double value2);
}
