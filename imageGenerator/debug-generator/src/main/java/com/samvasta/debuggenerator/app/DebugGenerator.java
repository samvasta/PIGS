//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.debuggenerator.app.DebugGenerator
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.debuggenerator.app;

import com.samvasta.common.interfaces.IGenerator;
import com.samvasta.debuggenerator.ui.GeneratorWindow;
import com.samvasta.generatorpack1.samplegenerator.SimpleGenerator;

import javax.swing.*;
import java.awt.*;

public class DebugGenerator
{

    public static void main(String...args){
        IGenerator generator = new SimpleGenerator();

        GeneratorWindow window = new GeneratorWindow(generator, new Dimension(1920, 1080));

        window.setSize(1600, 900);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
