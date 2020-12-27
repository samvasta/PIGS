package com.samvasta.imagegenerator.procgenideas.maps;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imagegenerator.procgenideas.maps.palettes.LandPalette;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapGenerator implements IGenerator {

    private ArrayList<ISnapshotListener> snapshotListeners;

    public MapGenerator(){
        snapshotListeners = new ArrayList<>();
    }

    public boolean isOnByDefault() {
        return false;
    }

    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<IniSchemaOption<?>>();
    }

    public boolean isMultiThreadEnabled() {
        return false;
    }

    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        ColorPalette waterPalette = new LinearLchPaletteBuilder(random)
                .startHue(290)
                .deltaHue(-10)
                .startLum(25)
                .deltaLum(8)
                .startChroma(60)
                .deltaChroma(5)
                .numColors(8)
                .build();

        LandPalette landPalette = new LandPalette(random);

        testColors(g, imageSize, random, waterPalette, landPalette);
    }

    private void testColors(Graphics2D g, Dimension imageSize, RandomGenerator random, ColorPalette waterPalette, LandPalette landPalette){

        int width = imageSize.width / waterPalette.getNumColors();
        int height = imageSize.height/2;
        for(int i = 0; i < waterPalette.getNumColors(); i++){
            int x = width * i;
            int y = 0;
            g.setColor(waterPalette.getColorByIndex(i));
            g.fillRect(x, y, width, height);
        }

        int x = 0;
        int y = height;
        width = imageSize.width/5;
        g.setColor(landPalette.getEarthColor(random));
        g.fillRect(x, y, width, height);

        x += width;
        g.setColor(landPalette.getHillColor(random));
        g.fillRect(x, y, width, height);

        x += width;
        g.setColor(landPalette.getMountainColor(random));
        g.fillRect(x, y, width, height);

        x += width;
        g.setColor(landPalette.getGrasslandColor(random));
        g.fillRect(x, y, width, height);

        x += width;
        g.setColor(landPalette.getForestColor(random));
        g.fillRect(x, y, width, height);
    }
}
