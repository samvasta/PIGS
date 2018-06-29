package com.samvasta.imageGenerator.common.interfaces;

import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.util.List;
import java.util.Map;


public interface IGenerator
{
    boolean isOnByDefault();

    /**
     * @return a map of options where the key is the option name and the value is the default option value
     */
    List<IniSchemaOption<?>> getIniSettings();

    boolean isMultiThreadEnabled();

    void addSnapshotListener(ISnapshotListener listener);

    void removeSnapshotListener(ISnapshotListener listener);

    void generateImage(final Map<String, Object> settings, final Graphics2D g, final Dimension imageSize, final MersenneTwister random);
}
