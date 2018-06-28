package com.samvasta.imagegenerator.debuggenerator.it;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.debuggenerator.GeneratorWindow;

import javax.swing.*;
import java.awt.*;

public class CommonLibraryTest
{


    public static void main(String...args){
        IGenerator generator = new ColorPaletteTestGenerator();

        GeneratorWindow window = new GeneratorWindow(generator, new Dimension(2560, 1440));

        window.setSize(1920, 1080);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
