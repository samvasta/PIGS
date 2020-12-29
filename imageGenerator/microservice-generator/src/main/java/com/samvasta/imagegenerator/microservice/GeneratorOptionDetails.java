package com.samvasta.imagegenerator.microservice;

import com.samvasta.imageGenerator.common.models.IniSchemaOption;

public class GeneratorOptionDetails {
    public final String key;
    public final String defaultValue;
    public final String type;

    public GeneratorOptionDetails(IniSchemaOption<?> option) {
        key = getUrlSafeOptionName(option);
        defaultValue = option.getDefaultValue().toString();
        type = option.getValueType().getSimpleName();
    }

    public static final String getUrlSafeOptionName(IniSchemaOption<?> option) {
        return option.getOptionName().replace(' ', '-').toLowerCase();
    }
}
