//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.debuggenerator.ui.GeneratorWindow
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.debuggenerator.ui;

import com.samvasta.common.helpers.IniHelper;
import com.samvasta.common.interfaces.IGenerator;
import com.samvasta.common.models.ImageBundle;
import com.samvasta.common.models.ImageCreator;
import com.samvasta.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;


public class GeneratorWindow extends JFrame
{
    private long seed;
    private BufferedImage image;
    private long generateStartTime;
    private long generateEndTime;

    private IGenerator generator;
    private Dimension imageSize;
    private Ini settings;
    private MersenneTwister random;
    private boolean isInfoVisible;

    private ExecutorService generatorExecutor;

    public GeneratorWindow(IGenerator generatorIn, Dimension imageSizeIn){
        this.generator = generatorIn;
        this.setTitle("Testing Generator \"" + generator.getClass().getSimpleName() + "\"");

        this.imageSize = imageSizeIn;

        random = new MersenneTwister();

        isInfoVisible = true;

        initSettings();

        generatorExecutor = Executors.newSingleThreadExecutor();

        this.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                int keyCode = e.getKeyChar();
                if(keyCode == 's'){
                    //todo: save image
                }
                else if(keyCode == KeyEvent.VK_SPACE){
                    generateImage();
                }
                else if(keyCode == 'i'){
                    isInfoVisible = !isInfoVisible;
                    repaint();
                }
                else if(keyCode == 'c'){
                    StringSelection stringSelection = new StringSelection(Long.toString(seed));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                }
                else if(keyCode == KeyEvent.VK_ESCAPE){
                    //todo: quit
                }
            }
        });

        generateImage();
    }

    private void initSettings(){
        settings = new Ini();
        String sectionName = generator.getClass().getSimpleName();
        IniHelper.addSection(settings, sectionName);
        List<IniSchemaOption<?>> options = generator.getIniSettings();

        for(IniSchemaOption<?> option : options){
            IniHelper.addOption(settings, sectionName, option.getOptionName(), option.getDefaultValue().toString());
        }
    }

    private void generateImage(){
        long seed = random.nextLong();
        ImageCreator creator = new ImageCreator(imageSize, generator, seed, settings);
        generateStartTime = System.currentTimeMillis();
        Future<ImageBundle> imageBundleFuture = generatorExecutor.submit(creator);
        try
        {
            ImageBundle bundle = imageBundleFuture.get(10, TimeUnit.MINUTES);
            generateEndTime = System.currentTimeMillis();
            image = (BufferedImage)bundle.image;
            this.seed = bundle.seed;
            repaint();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            //todo: stop execution
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g){
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);

        if(isInfoVisible){
            StringBuilder sb = new StringBuilder();
            sb.append("Generator        :   ").append(generator.getClass().getSimpleName()).append("\n");
            sb.append("Seed             :   ").append(seed).append("\n");
            sb.append("Time to generate :   ").append((generateEndTime - generateStartTime)).append(" ms").append("\n");

            sb.append("Commands:\n");
            sb.append("     S           :   Save current image\n");
            sb.append("     C           :   Copy seed to clipboard\n");
            sb.append("     [SPACE]     :   Generate new image\n");
            sb.append("     I           :   Toggle this image panel");

            String[] infoTexts = sb.toString().split("\n");

            g.setFont(new Font("Monospaced", Font.PLAIN, 14));
            FontMetrics fontMetrics = g.getFontMetrics();
            int height = 0;
            int maxWidth = 0;
            for(String text : infoTexts){
                Rectangle2D infoBounds = fontMetrics.getStringBounds(text, g);
                height += fontMetrics.getHeight();
                maxWidth = Math.max(maxWidth, (int)infoBounds.getWidth());
            }
            g.setColor(new Color(255, 255, 255, 128));

            g.fillRect(20, 40, maxWidth + 20, height + 20);

            g.setColor(Color.BLACK);
            int y = 40 + fontMetrics.getHeight();
            for(String text : infoTexts){
                g.drawString(text, 30, y);
                y += fontMetrics.getHeight();
            }
        }
    }
}
