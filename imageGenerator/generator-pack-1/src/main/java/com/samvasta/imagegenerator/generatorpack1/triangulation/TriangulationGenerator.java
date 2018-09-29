//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imagegenerator.generatorpack1.triangulation.TriangulationGenerator
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imagegenerator.generatorpack1.triangulation;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TriangulationGenerator implements IGenerator {

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

    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {

    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {

    }
}
