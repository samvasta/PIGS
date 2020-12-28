package com.samvasta.imagegenerator.generatorpack1.wovengrid;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.BalancedPalette;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WovenGridGenerator implements IGenerator {
    private List<ISnapshotListener> snapshotListeners;

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

    public WovenGridGenerator() {
        snapshotListeners = new ArrayList<>();
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {

        if(random.nextDouble() < 0.7){
            //Rotate graphics by a random amount (+/- 45deg)
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.PI/4d - Math.PI/2d * random.nextDouble(), imageSize.width/2, imageSize.height/2);
            g.setTransform(transform);
        }

        int smallestDimension = Math.min(imageSize.width, imageSize.height);

        int gridSize = smallestDimension / (random.nextInt(50) + 5);
        int fiberSize = (int) (gridSize * (0.25 + random.nextDouble() * 0.7));

        boolean isSymmetrical = random.nextDouble() < 0.2;
        boolean isCentered = isSymmetrical && random.nextDouble() < 0.4;

        int topMargin;
        int rightMargin;
        int bottomMargin;
        int leftMargin;

        if (isSymmetrical) {
            int baseMargin = (int) (smallestDimension / (random.nextDouble() * 5 + 1.55));
            int bigMargin = (int) (smallestDimension / (random.nextDouble() * 3 + 1.25));

            if (isCentered) {
                topMargin = baseMargin;
                bottomMargin = baseMargin;
                rightMargin = baseMargin;
                leftMargin = baseMargin;
            } else if (imageSize.width < imageSize.height) {
                rightMargin = baseMargin;
                leftMargin = baseMargin;
                topMargin = bigMargin;
                bottomMargin = bigMargin;
            } else {
                rightMargin = bigMargin;
                leftMargin = bigMargin;
                topMargin = baseMargin;
                bottomMargin = baseMargin;
            }
        } else {
            topMargin = (int) (imageSize.height / (random.nextDouble() * 5 + 1.25));
            rightMargin = (int) (imageSize.width / (random.nextDouble() * 5 + 1.25));
            bottomMargin = (int) (imageSize.height / (random.nextDouble() * 5 + 1.25));
            leftMargin = (int) (imageSize.width / (random.nextDouble() * 5 + 1.25));
        }

        // If the margins are so big that we can't fit two rows or columns, adjust the margins
        while (imageSize.height - topMargin - bottomMargin < gridSize * 2) {
            if (topMargin > bottomMargin) {
                topMargin -= gridSize * 2;
            } else {
                bottomMargin -= gridSize * 2;
            }
        }
        while (imageSize.width - leftMargin - rightMargin < gridSize * 2) {
            if (leftMargin > rightMargin) {
                leftMargin -= gridSize * 2;
            } else {
                rightMargin -= gridSize * 2;
            }
        }

        BalancedPalette palette = new BalancedPalette(random, "Woven");

        float fiberSizeAsPercent = (float) fiberSize / (float) gridSize;
        float[] shadowStops = new float[]
                {
                        Math.max(0, 0.5f - fiberSizeAsPercent),
                        0.5f - fiberSizeAsPercent/2f,
                        0.5f + fiberSizeAsPercent/2f,
                        Math.min(1, 0.5f + fiberSizeAsPercent)
                };

        Color[] shadowCols = new Color[]
                {
                        palette.getFg().toColor(0),
                        new Color(0, 0, 0, 80),
                        new Color(0, 0, 0, 80),
                        palette.getFg().toColor(0)
                };

        //We are rotating by a random amount so fill a circle big enough to cover the entire output image size at any rotation
        int circleRadius = (int)Math.sqrt(imageSize.width * imageSize.width + imageSize.height * imageSize.height);
        g.setColor(palette.getBg().toColor());
        g.fillOval(-circleRadius/2,-circleRadius/2, circleRadius*2, circleRadius*2);


        int drawAreaWidth = imageSize.width - leftMargin - rightMargin;
        int drawAreaHeight = imageSize.height - topMargin - bottomMargin;

        int numCellsX = drawAreaWidth / gridSize;
        int numCellsY = drawAreaHeight / gridSize;

        //generate colors of each row/col
        Color[] horizontalColors = getRandomColors(palette, random, Math.max(0, numCellsY));
        Color[] verticalColors = getRandomColors(palette, random, Math.max(0, numCellsX));

        //draw cells
        for (int x = 0; x < numCellsX; x++) {
            for (int y = 0; y < numCellsY; y++) {
                Rectangle cellBounds = new Rectangle(leftMargin + x * gridSize, topMargin + y * gridSize, gridSize, gridSize);
                drawCell(g, cellBounds, fiberSize, horizontalColors[y], verticalColors[x], shadowStops, shadowCols, random.nextBoolean());
            }
        }


        // extend fibers to border
        for (int x = 0; x < numCellsX; x++) {

            //shadow
            Paint origPaint = g.getPaint();
            g.setPaint(new LinearGradientPaint(leftMargin + x * gridSize, 0, leftMargin + (x + 1) * gridSize, 0, shadowStops, shadowCols));
            g.fillRect(leftMargin + x * gridSize, -circleRadius, gridSize, circleRadius+topMargin);
            g.fillRect(leftMargin + x * gridSize, topMargin + (gridSize * numCellsY), gridSize, circleRadius);

            //line
            g.setPaint(origPaint);
            g.setColor(verticalColors[x]);
            g.fillRect(leftMargin + x * gridSize + (int) (gridSize - fiberSize) / 2, -circleRadius, fiberSize, topMargin+circleRadius);
            g.fillRect(leftMargin + x * gridSize + (int) (gridSize - fiberSize) / 2, topMargin + (gridSize * numCellsY), fiberSize, circleRadius);
        }
        for (int y = 0; y < numCellsY; y++) {

            //shadow
            Paint origPaint = g.getPaint();
            g.setPaint(new LinearGradientPaint(0, topMargin + y * gridSize, 0, topMargin + (y + 1) * gridSize, shadowStops, shadowCols));
            g.fillRect(0, topMargin + y * gridSize, leftMargin, gridSize);
            g.fillRect(leftMargin + (gridSize * numCellsX), topMargin + y * gridSize, circleRadius, gridSize);

            //line
            g.setPaint(origPaint);
            g.setColor(horizontalColors[y]);
            g.fillRect(-circleRadius, topMargin + y * gridSize + (int) (gridSize - fiberSize) / 2, circleRadius+leftMargin, fiberSize);
            g.fillRect(leftMargin + (gridSize * numCellsX), topMargin + y * gridSize + (int) (gridSize - fiberSize) / 2, circleRadius, fiberSize);
        }
    }

    private Color[] getRandomColors(BalancedPalette palette, RandomGenerator random, int numColors) {
        Color[] cols = new Color[numColors];
        for (int i = 0; i < numColors; i++) {
            cols[i] = ColorUtil.getClose(palette.getRandPrimary(random).toColor(), 0.2);
        }
        return cols;
    }

    private void drawCell(Graphics2D g, Rectangle cellBounds, int fiberWidth, Color horizontalCol, Color verticalCol, float[] shadowStops, Color[] shadowCols, boolean isHorizontalOnTop) {
        Paint origPaint = g.getPaint();
        if (isHorizontalOnTop) {

            //vertical shadow
            g.setPaint(new LinearGradientPaint(cellBounds.x, 0, cellBounds.x + cellBounds.width, 0, shadowStops, shadowCols));
            g.fill(cellBounds);

            //vertical line
            g.setPaint(origPaint);
            g.setColor(verticalCol);
            g.fillRect(cellBounds.x + (cellBounds.width - fiberWidth) / 2, cellBounds.y, fiberWidth, cellBounds.height);


            //horizontal shadow
            g.setPaint(new LinearGradientPaint(0, cellBounds.y, 0, cellBounds.y + cellBounds.height, shadowStops, shadowCols));
            g.fill(cellBounds);

            //horizontal line
            g.setPaint(origPaint);
            g.setColor(horizontalCol);
            g.fillRect(cellBounds.x, cellBounds.y + (cellBounds.height - fiberWidth) / 2, cellBounds.width, fiberWidth);

        } else {

            //horizontal shadow
            g.setPaint(new LinearGradientPaint(0, cellBounds.y, 0, cellBounds.y + cellBounds.height, shadowStops, shadowCols));
            g.fill(cellBounds);

            //horizontal line
            g.setPaint(origPaint);
            g.setColor(horizontalCol);
            g.fillRect(cellBounds.x, cellBounds.y + (cellBounds.height - fiberWidth) / 2, cellBounds.width, fiberWidth);


            //vertical shadow
            g.setPaint(new LinearGradientPaint(cellBounds.x, 0, cellBounds.x + cellBounds.width, 0, shadowStops, shadowCols));
            g.fill(cellBounds);

            //vertical line
            g.setPaint(origPaint);
            g.setColor(verticalCol);
            g.fillRect(cellBounds.x + (cellBounds.width - fiberWidth) / 2, cellBounds.y, fiberWidth, cellBounds.height);
        }
        g.setPaint(origPaint);
    }
}
