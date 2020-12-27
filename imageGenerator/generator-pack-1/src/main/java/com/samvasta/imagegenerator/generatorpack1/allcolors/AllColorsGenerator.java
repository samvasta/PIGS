package com.samvasta.imagegenerator.generatorpack1.allcolors;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.grids.SquareGridCoordinate;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sam on 1/25/2017.
 */
public class AllColorsGenerator implements IGenerator {
    private static Logger logger = Logger.getLogger(AllColorsGenerator.class);

    private List<ISnapshotListener> snapshotListeners;

    private final int factor = 4;

    private int width;
    private int height;
    private boolean looping = false;
    private boolean isCompletelyFilledIn;

    List<Color> unusedColors;
    MyColor[] pixels;
    Color[] averages;
    List<SquareGridCoordinate> readyToSet;


    @Override
    public boolean isOnByDefault() {
        return false;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return false;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    private void takeSnapshot(){
        for(ISnapshotListener listener : snapshotListeners){
            listener.takeSnapshot();
        }
    }

    public AllColorsGenerator() {
        snapshotListeners = new ArrayList<>();
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        long startTime = System.currentTimeMillis();
        width = (int) imageSize.getWidth() / factor;
        height = (int) imageSize.getHeight() / factor;

        unusedColors = new LinkedList<Color>();
        pixels = new MyColor[width * height];
        averages = new Color[width * height];
        readyToSet = new ArrayList<SquareGridCoordinate>();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = new MyColor(ColorState.NOT_SET, Color.black);
        }
        Arrays.fill(averages, null);

        populateColorList(random);

        logger.log(Level.INFO, "Num Colors in list:" + unusedColors.size() + ", num pixels:" + pixels.length);

        int numStartColors = random.nextBoolean() ? 1 : (int) (random.nextGaussian() * 5 + random.nextDouble() * random.nextDouble() * 5);
        Color[] startColors = new Color[numStartColors];
        for (int i = 0; i < numStartColors; i++) {
            Color startColor = unusedColors.remove(random.nextInt(unusedColors.size()));
            int startX = random.nextInt((int) (width * 0.75)) + (int) (width * 0.125);
            int startY = random.nextInt((int) (height * 0.75)) + (int) (height * 0.125);
            setPixel(new SquareGridCoordinate(startX, startY), startColor);
            startColors[i] = startColor;
        }

        sortColors(startColors);

        placeColors();

        fillInHoles();

        drawImage(g);

        logger.log(Level.INFO, "That took " + ((System.currentTimeMillis() - startTime) / 1000d) + "sec!");
    }

    private void populateColorList(RandomGenerator random) {
        //when true it will take longer to generate images
        isCompletelyFilledIn = false;

        double step;
        if (isCompletelyFilledIn) {
            step = Math.ceil(Math.pow((double) width * height, (1f / 3f)));
        } else {
            step = Math.floor(Math.pow((double) width * height * (0.4 + random.nextDouble() * 0.2), (1f / 3f)));
        }

        for (double r = 255; r >= 0; r -= 256f / (step)) {
            for (double gr = 255; gr >= 0; gr -= 256f / (step)) {
                for (double b = 255; b >= 0; b -= 256f / (step)) {
                    Color col = new Color((int) r, (int) gr, (int) b);
                    unusedColors.add(col);
                }
            }
        }
    }

    private void sortColors(final Color[] base) {
        Collections.sort(unusedColors, new Comparator<Color>() {

            public int compare(Color o1, Color o2) {
                double weightedTotal1 = getColorCloseness(o1, base);
                double weightedTotal2 = getColorCloseness(o2, base);

                if (weightedTotal1 == weightedTotal2) return 0;
                if (weightedTotal1 > weightedTotal2) return 1;
                if (weightedTotal1 < weightedTotal2) return -1;

                //This code will never execute, but the compiler really wants something here

                // Dear Compiler,
                //Here you go.
                return 0;
                //With love ♥♥♥,
                //	-Sam
            }
        });
    }

    private void placeColors() {

        final int numColors = Math.min(unusedColors.size(), pixels.length);
        for (int i = 1; i < numColors; i++) {
            int bestIdx = 0;
            final Color color = unusedColors.remove(0);

            double bestSoFar = Double.MAX_VALUE;

            int numThreads = Math.min(2 + (readyToSet.size() / 4000), Runtime.getRuntime().availableProcessors());
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            List<MyRunnable> rowRunnables = new ArrayList<MyRunnable>();

            int numPerRunnable = (int) Math.ceil(readyToSet.size() / (double) numThreads);
            for (int x = 0; x < readyToSet.size(); x += numPerRunnable) {
                MyRunnable r = new MyRunnable(x, Math.min(x + numPerRunnable, readyToSet.size()), color);
                rowRunnables.add(r);
                executorService.execute(r);
            }
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                    executorService.shutdownNow();
                }
            } catch (Exception ex) {
            }

            for (MyRunnable r : rowRunnables) {
                if (r.bestSoFar < bestSoFar) {
                    bestIdx = r.bestIdx;
                    bestSoFar = r.bestSoFar;
                }
            }

            SquareGridCoordinate best = readyToSet.get(bestIdx);
            readyToSet.remove(bestIdx);
            setPixel(best, color);

            if (i % 1000 == 0) {
                logger.log(Level.INFO, i + " / " + Math.min(numColors, pixels.length));
                takeSnapshot();
            }
        }
    }

    private void fillInHoles() {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                MyColor average = getAverage(new SquareGridCoordinate(x, y));
                if (average != null) {
                    double closeness = getColorCloseness(pixels[x + y * width].color, average.color);
                    if (closeness > 0.15) {
                        pixels[x + y * width].color = average.color;
                    }
                }
            }
        }
    }

    private void drawImage(Graphics2D g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                g.setColor(pixels[i + j * width].color);
                g.fillRect(i * factor, j * factor, factor, factor);
            }
        }
    }

    private boolean setPixel(SquareGridCoordinate xy, Color color) {
        SquareGridCoordinate above = getInBoundsPoint(xy.top().x, xy.top().y);
        SquareGridCoordinate below = getInBoundsPoint(xy.bottom().x, xy.bottom().y);
        SquareGridCoordinate left = getInBoundsPoint(xy.left().x, xy.left().y);
        SquareGridCoordinate right = getInBoundsPoint(xy.right().x, xy.right().y);
        MyColor pixel = pixels[xy.x + xy.y * width];
        if (pixel.state != ColorState.ASSIGNED) {
            pixel.color = color;
            pixel.state = ColorState.ASSIGNED;
            tryMakeAvailable(above);
            tryMakeAvailable(below);
            tryMakeAvailable(left);
            tryMakeAvailable(right);
            updateAverages(above);
            updateAverages(below);
            updateAverages(left);
            updateAverages(right);
        } else {
            return false;
        }
        return true;
    }

    private void updateAverages(SquareGridCoordinate p) {
        MyColor avg = getAverage(p);
        if (avg != null) {
            averages[p.x + p.y * width] = avg.color;
        }
    }

    private MyColor getAverage(SquareGridCoordinate xy) {
        SquareGridCoordinate above = getInBoundsPoint(xy.top().x, xy.top().y);
        SquareGridCoordinate below = getInBoundsPoint(xy.bottom().x, xy.bottom().y);
        SquareGridCoordinate left = getInBoundsPoint(xy.left().x, xy.left().y);
        SquareGridCoordinate right = getInBoundsPoint(xy.right().x, xy.right().y);
        double r = 0, g = 0, b = 0;
        int numSet = 0;

        if (pixels[above.x + above.y * width].state == ColorState.ASSIGNED) {
            r += pixels[above.x + above.y * width].color.getRed();
            g += pixels[above.x + above.y * width].color.getGreen();
            b += pixels[above.x + above.y * width].color.getBlue();
            numSet++;
        }

        if (pixels[below.x + below.y * width].state == ColorState.ASSIGNED) {
            r += pixels[below.x + below.y * width].color.getRed();
            g += pixels[below.x + below.y * width].color.getGreen();
            b += pixels[below.x + below.y * width].color.getBlue();
            numSet++;
        }

        if (pixels[left.x + left.y * width].state == ColorState.ASSIGNED) {
            r += pixels[left.x + left.y * width].color.getRed();
            g += pixels[left.x + left.y * width].color.getGreen();
            b += pixels[left.x + left.y * width].color.getBlue();
            numSet++;
        }

        if (pixels[right.x + right.y * width].state == ColorState.ASSIGNED) {
            r += pixels[right.x + right.y * width].color.getRed();
            g += pixels[right.x + right.y * width].color.getGreen();
            b += pixels[right.x + right.y * width].color.getBlue();
            numSet++;
        }
        if (numSet == 0) return null;
        r /= numSet;
        g /= numSet;
        b /= numSet;
        return new MyColor(ColorState.ASSIGNED, new Color((int) r, (int) g, (int) b));
    }

    private void tryMakeAvailable(SquareGridCoordinate point) {
        if (pixels[point.x + point.y * width].state != ColorState.ASSIGNED) {
            pixels[point.x + point.y * width].state = ColorState.READY_TO_BE_SET;
            if (!readyToSet.contains(point)) {
                readyToSet.add(point);
            }
        }
    }

    private double getColorCloseness(Color o1, Color... list) {
        double minValue = Double.MAX_VALUE;
        for (Color o2 : list) {
            float[] hsb1 = Color.RGBtoHSB(o1.getRed(), o1.getGreen(), o1.getBlue(), new float[3]);
            float[] hsb2 = Color.RGBtoHSB(o2.getRed(), o2.getGreen(), o2.getBlue(), new float[3]);

            float hueDiff1 = (hsb1[0] - hsb2[0]);
            if (hueDiff1 < -0.5) {
                hueDiff1 = (1 + hsb1[0]) - hsb2[0];
            } else if (hueDiff1 > 0.5) {
                hueDiff1 = hsb1[0] - (1 + hsb2[0]);
            }
            hueDiff1 = Math.abs(hueDiff1);
            float saturationDiff1 = hsb1[1] - hsb2[1];
            float brightnessDiff1 = hsb1[2] - hsb2[2];

            double value = hueDiff1 + saturationDiff1 * saturationDiff1 + brightnessDiff1 * brightnessDiff1;
            if (minValue > value) {
                minValue = value;
            }
        }

        return minValue;
    }

    private SquareGridCoordinate getInBoundsPoint(int x, int y) {
        if (x < 0) {
            if (looping) x += width;
            else x = 0;
        }
        if (x >= width) {
            if (looping) x -= width;
            else x = width - 1;
        }
        if (y < 0) {
            if (looping) y += height;
            else y = 0;
        }
        if (y >= height) {
            if (looping) y -= height;
            else y = height - 1;
        }
        return new SquareGridCoordinate(x, y);
    }

    class MyColor {
        ColorState state;
        Color color;

        MyColor(ColorState s, Color c) {
            state = s;
            color = c;
        }
    }

    enum ColorState {
        NOT_SET,
        READY_TO_BE_SET,
        ASSIGNED
    }

    class MyRunnable implements Runnable {

        int start;
        int end;
        double bestSoFar;
        Color color;
        int bestIdx;

        MyRunnable(int startIdx, int endIdx, Color color) {
            this.start = startIdx;
            this.end = endIdx;
            this.color = color;
            bestSoFar = Double.MAX_VALUE;
        }

        public void run() {
            for (int i = start; i < end; i++) {
                SquareGridCoordinate coord = readyToSet.get(i);
                Point p = new Point(coord.x, coord.y);
                double closeness = getColorCloseness(averages[p.x + p.y * width], color);
                if (closeness < bestSoFar) {
                    bestIdx = i;
                    bestSoFar = closeness;
                }
            }
        }
    }
}
