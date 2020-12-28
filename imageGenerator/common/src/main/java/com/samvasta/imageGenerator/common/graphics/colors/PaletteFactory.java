package com.samvasta.imageGenerator.common.graphics.colors;

import com.samvasta.imageGenerator.common.exceptions.ColorPaletteException;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.*;
import org.apache.commons.math3.random.RandomGenerator;

public class PaletteFactory {
    public enum PaletteOptions {
        ANALOGOUS {
            @Override
            public ColorPalette generate(RandomGenerator rand) {
                return new LinearLchPaletteBuilder(rand, "Analogous").build();
            }
        },
        MONOCHROME {
            @Override
            public ColorPalette generate(RandomGenerator rand) {
                return new MonochromePalette(rand, "Monochrome");
            }
        },
        TRIAD {
            @Override
            public ColorPalette generate(RandomGenerator rand) {
                return new TriadPalette(rand, "Triad");
            }
        },
        COMPLEMENTARY {
            @Override
            public ColorPalette generate(RandomGenerator rand) {
                return new ComplementaryPalette(rand, "Complementary");
            }
        },
        BALANCED {
            @Override
            public ColorPalette generate(RandomGenerator rand) {
                return new BalancedPalette(rand, "Balanced");
            }
        }
        ;
        public abstract ColorPalette generate(RandomGenerator rand);
    }

    public static final ColorPalette getRandomPalette(RandomGenerator rand) {
        int i = rand.nextInt(PaletteOptions.values().length);
        return PaletteOptions.values()[i].generate(rand);
    }

    public static final ColorPalette getAnalogousPalette(RandomGenerator rand, CeiLchColor base) {
        return new LinearLchPaletteBuilder(rand, "Analogous")
                .startHue(base.hue)
                .startChroma(base.chroma)
                .startLum(base.luminance)
                .build();
    }
}
