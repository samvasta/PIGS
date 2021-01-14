package com.samvasta.imagegenerator.generatorpack1.samplegenerator;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.PaletteFactory;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPalette;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.LinearLchPaletteBuilder;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.MonochromePalette;
import com.samvasta.imageGenerator.common.graphics.images.BlendMode;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.samvasta.imageGenerator.common.graphics.stamps.IStamp;
import com.samvasta.imageGenerator.common.graphics.stamps.StampInfo;
import com.samvasta.imageGenerator.common.graphics.stamps.StampInfoBuilder;
import com.samvasta.imageGenerator.common.graphics.textures.CompositeTexture;
import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import com.samvasta.imageGenerator.common.graphics.textures.TextureUtil;
import com.samvasta.imageGenerator.common.graphics.vertexplacers.PoissonVertexPlacer;
import com.samvasta.imageGenerator.common.helpers.GeomHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.PolarVector;
import com.samvasta.imageGenerator.common.models.curves.HermiteSpline;
import com.samvasta.imageGenerator.common.noise.NoiseHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import com.samvasta.imageGenerator.common.shapes.BadCircle;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class SampleGenerator implements IGenerator {
    private Set<ISnapshotListener> snapshotListeners = new HashSet<>();

    public boolean isOnByDefault() {
        return true;
    }

    public List<IniSchemaOption<?>> getIniSettings() {
        List<IniSchemaOption<?>> schemaOptions = new ArrayList<>();
        schemaOptions.add(new IniSchemaOption<>("text", "Hello World!", String.class));

        return schemaOptions;
    }

    public boolean isMultiThreadEnabled() {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        if (!snapshotListeners.contains(listener)) {
            snapshotListeners.add(listener);
        }
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        if (snapshotListeners.contains(listener)) {
            snapshotListeners.remove(listener);
        }
    }

    public void generateImage(final Map<String, Object> settings, final Graphics2D g, final Dimension imageSize, final MersenneTwister random) {
        final ColorPalette palette = PaletteFactory.getRandomPalette(random);

        ITexture fadeUp = new ITexture() {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                double baseAlpha = 0.75 + (random.nextDouble() * 0.25);

                for (int x = 0; x < textureSize.width; x++) {
                    for (int y = 0; y < textureSize.height; y++) {
                        pixels[x + y * textureSize.width] = baseAlpha * (x + y * textureSize.width) / (pixels.length);
                    }
                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette1, RandomGenerator random) {
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };
        ITexture fadeLeft = new ITexture() {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                double baseAlpha = 0.75 + (random.nextDouble() * 0.25);

                for (int i = 0; i < pixels.length; i++) {
                    int x = i % textureSize.width;
                    int y = i / textureSize.width;
                    pixels[i] = baseAlpha * (y + x * textureSize.height) / (pixels.length);

                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette1, RandomGenerator random) {
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };

        ITexture noise = new ITexture() {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                FastNoise noiseGen = NoiseHelper.getFractalSimplex(random, 5);

                for (int x = 0; x < textureSize.width; x++) {
                    for (int y = 0; y < textureSize.height; y++) {
                        pixels[x + y * textureSize.width] = (noiseGen.getValueFractal(x, y) + 1.0) * 0.5;
                    }
                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, RandomGenerator random) {
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };

        ITexture vignette = new ITexture() {
            @Override
            public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {
                ProtoTexture protoTexture = new ProtoTexture(textureSize);

                double[] pixels = new double[textureSize.width * textureSize.height];

                int horizontalRadius = (int) (((textureSize.width) * random.nextDouble() * 0.3) + (textureSize.width * 0.2));
                int verticalRadius = (int) (((textureSize.height) * random.nextDouble() * 0.3) + (textureSize.height * 0.2));

                double hrSquared = horizontalRadius * horizontalRadius;
                double vrSquared = verticalRadius * verticalRadius;

                for (int x = 0; x < textureSize.width; x++) {
                    for (int y = 0; y < textureSize.height; y++) {

                        double xSquared = (textureSize.width / 2) - x;

                        xSquared = xSquared * xSquared;

                        double ySquared = (textureSize.height / 2) - y;

                        ySquared = ySquared * ySquared;

                        double radiusRatio = (xSquared) / (hrSquared) + (ySquared) / (vrSquared);

                        double threshold = random.nextDouble();
                        if (radiusRatio <= 1) {
                            pixels[x + y * textureSize.width] = radiusRatio * threshold;
                        } else if (radiusRatio * threshold * threshold > 1) {
                            pixels[x + y * textureSize.width] = threshold;
                        } else {
                            pixels[x + y * textureSize.width] = radiusRatio * threshold;
                        }
                    }
                }

                protoTexture.setPixels(0, 0, textureSize.width, textureSize.height, pixels);

                return protoTexture;
            }

            @Override
            public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, RandomGenerator random) {
                return TextureUtil.colorizeSingleColor(protoTexture, palette.getSmallestColor());
            }
        };

//        g.setColor(palette.getBiggestColor());
//        g.fillRect(0, 0, imageSize.width, imageSize.height);

        CompositeTexture backgroundTexture = new CompositeTexture(noise);
        backgroundTexture.addTexture(vignette, BlendMode.SUBTRACT);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        g.drawImage(TextureUtil.colorizeSmooth(backgroundTexture.getTexture(imageSize, random), palette), 0, 0, null);

        final CompositeTexture compositeTexture = new CompositeTexture(noise);
        compositeTexture.addTexture(fadeLeft, BlendMode.SCREEN);
        compositeTexture.addTexture(fadeUp, BlendMode.SCREEN);
        compositeTexture.addTexture(noise, BlendMode.LIGHTEN_ONLY);
        compositeTexture.addTexture(noise, BlendMode.LIGHTEN_ONLY);
        compositeTexture.addTexture(noise, BlendMode.MULTIPLY);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageSize.width, imageSize.height);

        IStamp texturedCircleStamp = new IStamp() {
            @Override
            public void stamp(Graphics2D g, StampInfo stampInfo, RandomGenerator random) {
                Color col = palette.getColor(random.nextDouble());

                g.setClip(new Ellipse2D.Double(stampInfo.getX(), stampInfo.getY(), stampInfo.getWidth(), stampInfo.getHeight()));

                g.setColor(col);
                g.fillOval(stampInfo.getXInt(), stampInfo.getYInt(), stampInfo.getWidthInt(), stampInfo.getHeightInt());

                BufferedImage textureImg = TextureUtil.colorizeSingleColor(compositeTexture.getTexture(stampInfo.getDimension(), random), palette.getColor(random.nextDouble()));
                g.drawImage(textureImg, stampInfo.getXInt(), stampInfo.getYInt(), null);
                g.setClip(null);
            }
        };

        final double blobRadius = 15 + random.nextInt(100);
        IStamp badCircleStamp = new IStamp() {
            private final ColorPalette palette = PaletteFactory.getRandomPalette(random);

            private void drawEyes(Graphics2D g, int numEyes, Path2D blob, int strokeWidth, StampInfo stampInfo) {
                Point2D.Double eyeCenter;
                double eyeRadius = blobRadius / (3 + random.nextInt(7));
                int pupilRadius = (int) (eyeRadius / 4 + random.nextInt((int) eyeRadius / 2));

                List<Point2D.Double> placedEyes = new ArrayList<>();

                for(int i = 0; i < numEyes; i++) {
                    int numTries = 0;
                    boolean intersectsPlacedEye = false;
                    do {
                        eyeCenter = new Point2D.Double(random.nextDouble() * stampInfo.getWidth() + stampInfo.getX() - stampInfo.getWidth()/2, random.nextDouble() * stampInfo.getHeight() + stampInfo.getY() - stampInfo.getHeight()/2);
                        ++numTries;

                        for(Point2D.Double placedEye : placedEyes){
                            // *4 because we want them to be an entire diameter away
                            // so it should be  = (eyeRadius*2) * (eyeRadius*2)
                            //                  = (eyeRadius * eyeRadius) * (2 * 2)
                            //                  = eyeRadius * eyeRadius * 4
                            intersectsPlacedEye = eyeCenter.distanceSq(placedEye) < eyeRadius*eyeRadius*4;
                            if(intersectsPlacedEye){
                                break;
                            }
                        }
                    } while (numTries < 50 && (intersectsPlacedEye || !blob.contains(eyeCenter.x - eyeRadius, eyeCenter.y - eyeRadius, eyeRadius * 2, eyeRadius * 2)));

                    placedEyes.add(eyeCenter);

                    if(numTries >= 50) {
                        //failed to place an eye so skip to next eye
                        continue;
                    }

                    BadCircle eyeShape = new BadCircle(eyeRadius, eyeCenter, 0.2, random);

                    g.setColor(Color.WHITE);
                    g.fill(eyeShape);

                    g.setColor(Color.BLACK);

                    double pupilEyeDiff = eyeRadius - pupilRadius;
                    double offsetX = pupilEyeDiff * random.nextDouble() - pupilEyeDiff/2;
                    double offsetY = pupilEyeDiff * random.nextDouble() - pupilEyeDiff/2;
                    BadCircle pupilShape = new BadCircle(pupilRadius, eyeCenter.x + offsetX, eyeCenter.y + offsetY, 0.2, random);
                    g.fill(pupilShape);

                    g.setStroke(new BasicStroke(strokeWidth / 2));
                    g.draw(eyeShape);
                }

            }

            @Override
            public void stamp(Graphics2D g, StampInfo stampInfo, RandomGenerator random) {
                BadCircle blob = new BadCircle(blobRadius, stampInfo.getX(), stampInfo.getY(), random.nextDouble() * 0.3 + 0.6, 150, random);

                int strokeWidth = (int) Math.max(2, blobRadius / 10) + random.nextInt((int)Math.max(1,(blobRadius/20)));

                g.setColor(palette.getColorSmooth(random.nextDouble()));
                g.fill(blob);

                int numEyes = random.nextInt(4) + 1;
                drawEyes(g, numEyes, blob.getPath(), strokeWidth/2, stampInfo);

                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(strokeWidth));
                g.draw(blob);
            }
        };

        PoissonVertexPlacer placer = new PoissonVertexPlacer(blobRadius * 1.9);
        List<Point2D.Double> stampPoints = new ArrayList<>();
        placer.placeVertices(stampPoints, new Rectangle(0, 0, imageSize.width, imageSize.height), random);

        for (Point2D.Double point : stampPoints) {
            StampInfo info = new StampInfoBuilder()
                    .x(point.x)
                    .y(point.y)
                    .width(100)
                    .height(100)
                    .build();
            badCircleStamp.stamp(g, info, random);
        }

//        for(int i = 0; i < 17; i++){
//            StampInfo info = new StampInfoBuilder()
//                                .x(random.nextInt((int)(imageSize.width * 1.5)) - imageSize.width/4)
//                                .y(random.nextInt((int)(imageSize.height * 1.5)) - imageSize.height/4)
//                                .width((int)(imageSize.width * (random.nextDouble() * 0.15 + 0.01)))
//                                .height((int)(imageSize.width * (random.nextDouble() * 0.15 + 0.01)))
//                                .build();
//
//            texturedCircleStamp.stamp(g, info, random);
//        }
    }
}
