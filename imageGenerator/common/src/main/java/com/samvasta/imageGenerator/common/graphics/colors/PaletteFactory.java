package com.samvasta.imageGenerator.common.graphics.colors;

import com.samvasta.imageGenerator.common.exceptions.ColorPaletteException;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.ComplementaryPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.MonochromePalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.TriadPalette;
import org.apache.commons.math3.random.RandomGenerator;

public class PaletteFactory {
    private enum PaletteOptions {
        ANALOGOUS {
            @Override
            ColorPalette generate(RandomGenerator rand) {
                return new LinearLchPaletteBuilder(rand).build();
            }
        },
        MONOCHROME {
            @Override
            ColorPalette generate(RandomGenerator rand) {
                return new MonochromePalette(rand);
            }
        },
        TRIAD {
            @Override
            ColorPalette generate(RandomGenerator rand) {
                return new TriadPalette(rand);
            }
        },
        COMPLEMENTARY {
            @Override
            ColorPalette generate(RandomGenerator rand) {
                return new ComplementaryPalette(rand);
            }
        },
        ;
        abstract ColorPalette generate(RandomGenerator rand);
    }

    public static final ColorPalette getRandomPalette(RandomGenerator rand) {
        int i = rand.nextInt(PaletteOptions.values().length);
        return PaletteOptions.values()[i].generate(rand);
    }

    public static final ColorPalette getAnalogousPalette(RandomGenerator rand, CeiLchColor base) {
        return new LinearLchPaletteBuilder(rand)
                .startHue(base.hue)
                .startChroma(base.chroma)
                .startLum(base.luminance)
                .build();
    }
}
