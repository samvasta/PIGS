package com.samvasta.imagegenerator.generatorpack1.bezier;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.PaletteFactory;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BezierGenerator implements IGenerator {
    private ArrayList<ISnapshotListener> snapshotListeners;

    @Override
    public boolean isOnByDefault() {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    public BezierGenerator() {
        snapshotListeners = new ArrayList<>();
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        ColorPalette palette = PaletteFactory.getRandomPalette(random);
        int paletteSize = (int) (Math.max(imageSize.width, imageSize.height) * 0.0125);

        boolean lightTheme = ColorUtil.useDarkBackground(palette);
        if (lightTheme) {
            g.setColor(new Color(230, 230, 230));
        } else {
            g.setColor(new Color(25, 25, 25));
        }

        g.fill(new Rectangle(0, 0, imageSize.width, imageSize.height));

        int x0, y0, x1, y1;
        int size;


        if (imageSize.width > imageSize.height) {
            size = (int) (imageSize.height * 2d / 3d);

        } else {
            size = (int) (imageSize.width * 2d / 3d);
        }

        Color fontCol;
        if (lightTheme) {
            fontCol = Color.white;
        } else {
            fontCol = Color.black;
        }
        for (Color c : palette.getAllColors()) {
            if (lightTheme) {
                if (ColorUtil.getHsvValue(c) < ColorUtil.getHsvValue(fontCol)) {
                    fontCol = c;
                }
            } else {
                if (ColorUtil.getHsvValue(c) > ColorUtil.getHsvValue(fontCol)) {
                    fontCol = c;
                }
            }
        }
        g.setColor(fontCol);
        x0 = (int) ((imageSize.width - size) / 2d);
        y0 = (int) ((imageSize.height - size) / 2d);

        x1 = x0 + size;
        y1 = y0;

        double dx = (random.nextDouble() * size / 5d);
        double dy = (random.nextDouble() * size / 5d);
        if (imageSize.width > imageSize.height) {
            dy = (imageSize.height - size) / 2d;
        } else {
            dx = (imageSize.width - size) / 2d;
        }
        Rectangle controlPointBounds = new Rectangle((int) (x0 - dx), (int) (y0 - dy), (int) (size + 2 * dx), (int) (size + 2 * dy));

        int[] array = new int[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        shuffleArray(array, random);

        if (random.nextDouble() > 0.7) {
            if (random.nextDouble() > 0.7) {
                g.setStroke(new BasicStroke(2));
            } else {
                g.setStroke(new BasicStroke(3));
            }
        }

        boolean blendColors = random.nextDouble() > 0.3;
        for (int i : array) {
            Point2D start = new Point2D.Double(x0, y0 + i);
            double weight = random.nextDouble();
            Point2D end = new Point2D.Double(x1, y1 + random.nextDouble() * size);

            double controlX1 = 0, controlY1 = 0, controlX2 = 0, controlY2 = 0;

            double temp = (random.nextDouble() * controlPointBounds.width * 0.5) + (controlPointBounds.width * 0.5) + controlPointBounds.x;
            double temp2 = (random.nextDouble() * controlPointBounds.width * 0.5) + controlPointBounds.x;

            controlX1 = Math.min(temp, temp2);
            controlX2 = Math.max(temp, temp2);

            controlY1 = random.nextDouble() * controlPointBounds.height + controlPointBounds.y;
            controlY2 = random.nextDouble() * controlPointBounds.height + controlPointBounds.y;

            CubicCurve2D curve = new CubicCurve2D.Double(start.getX(), start.getY(), controlX1, controlY1, controlX2, controlY2, end.getX(), end.getY());


            double percent = ((double) i / (double) size);

            Color col;
            if (blendColors) {
                col = palette.getColorSmooth(percent);
            } else {
                col = palette.getColor(percent);
            }

            g.setColor(col);
            g.draw(curve);
        }

        double percent = 0;
        for (int i = 0; i < palette.getNumColors(); i++) {
            int xi;
            int yi;
            int xf;
            int yf;

            xi = paletteSize + (int) Math.floor(imageSize.width * 0.2 * percent);

            percent += palette.getNormalizedWeightByIndex(i);

            yi = (int) (paletteSize * 2);
            xf = paletteSize + (int) Math.ceil(imageSize.width * 0.2 * percent);
            yf = (int) (paletteSize * 2.25);

            g.setColor(palette.getColorByIndex(i));
            g.fillRect(xi, yi, xf - xi, yf - yi);
        }

    }

    // Implementing Fisher–Yates shuffle
    private void shuffleArray(int[] ar, RandomGenerator rnd) {
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
