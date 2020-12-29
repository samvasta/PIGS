package com.samvasta.imagegenerator.generatorpack1.amoebas;

import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.BalancedPalette;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import com.samvasta.imageGenerator.common.shapes.BadCircle;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmoebaGenerator extends SimpleGenerator {

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        int smallestDimension = Math.min(imageSize.width, imageSize.height);
        final double blobRadius = 15 + random.nextInt(smallestDimension / 4);

        final BalancedPalette palette = new BalancedPalette(random, false, "balanced");

        g.setColor(palette.getBg().toColor());
        g.fillRect(0, 0, imageSize.width, imageSize.height);

        List<BadCircle> blobs = SpaceFiller.fillSpace(new Rectangle2D.Double(0, 0, imageSize.width, imageSize.height), 25, blobRadius, random);

        for (BadCircle blob : blobs) {
            drawBlob(g, blob, palette, random);
        }

    }

    private void drawBlob(Graphics2D g, BadCircle blob, BalancedPalette palette, RandomGenerator random) {
        double blobWidth = blob.getBounds().width;
        int strokeWidth = (int) Math.max(2, blobWidth / 30) + random.nextInt((int) Math.max(1, (blobWidth / 40)));

        g.setColor(ColorUtil.getClose(palette.getRandPrimary(random).toColor(), 1));
        g.fill(blob);

        int numEyes = random.nextInt(4) + 1;
        drawEyes(g, numEyes, blob, blobWidth, strokeWidth / 2, random);

        g.setColor(palette.getFg().toColor());
        g.setStroke(new BasicStroke(strokeWidth));
        g.draw(blob);
    }

    private void drawEyes(Graphics2D g, int numEyes, BadCircle blob, double blobWidth, int strokeWidth, RandomGenerator random) {
        Point2D.Double eyeCenter;
        double eyeRadius = blobWidth/ (2 * (3 + random.nextInt(7)));
        int pupilRadius = (int) (eyeRadius / 4 + random.nextInt((int) Math.max(2, eyeRadius / 2)));

        List<Point2D.Double> placedEyes = new ArrayList<>();

        for (int i = 0; i < numEyes; i++) {
            int numTries = 0;
            boolean intersectsPlacedEye = false;
            do {
                eyeCenter = new Point2D.Double(random.nextDouble() * blobWidth + blob.getCenter().x - blobWidth / 2, random.nextDouble() * blobWidth + blob.getCenter().y - blobWidth / 2);
                ++numTries;

                for (Point2D.Double placedEye : placedEyes) {
                    // *4 because we want them to be an entire diameter away
                    // so it should be  = (eyeRadius*2) * (eyeRadius*2)
                    //                  = (eyeRadius * eyeRadius) * (2 * 2)
                    //                  = eyeRadius * eyeRadius * 4
                    intersectsPlacedEye = eyeCenter.distanceSq(placedEye) < eyeRadius * eyeRadius * 4;
                    if (intersectsPlacedEye) {
                        break;
                    }
                }
            } while (numTries < 100 && (intersectsPlacedEye || !blob.contains(eyeCenter.x - eyeRadius, eyeCenter.y - eyeRadius, eyeRadius * 2, eyeRadius * 2)));

            placedEyes.add(eyeCenter);

            if (numTries >= 100) {
                //failed to place an eye so skip to next eye
                continue;
            }

            BadCircle eyeShape = new BadCircle(eyeRadius, eyeCenter, 0.2, 50, random);

            g.setColor(Color.WHITE);
            g.fill(eyeShape);

            g.setColor(Color.BLACK);

            double pupilEyeDiff = eyeRadius - pupilRadius;
            double offsetX = pupilEyeDiff * random.nextDouble() - pupilEyeDiff / 2;
            double offsetY = pupilEyeDiff * random.nextDouble() - pupilEyeDiff / 2;
            BadCircle pupilShape = new BadCircle(pupilRadius, eyeCenter.x + offsetX, eyeCenter.y + offsetY, 0.2, random);
            g.fill(pupilShape);

            g.setStroke(new BasicStroke(strokeWidth / 2));
            g.draw(eyeShape);
        }
    }
}
