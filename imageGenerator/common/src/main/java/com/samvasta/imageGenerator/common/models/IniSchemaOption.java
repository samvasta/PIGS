package com.samvasta.imageGenerator.common.models;

import org.apache.commons.math3.random.MersenneTwister;

import java.lang.reflect.Array;

public class IniSchemaOption<T>
{
    private final String optionName;
    private final Class valueType;
    private final T[] defaultValues;

    @SuppressWarnings("unchecked")
    public IniSchemaOption(String optionName, T defaultValue, Class<T> valueType){
        this.optionName = optionName;
        this.defaultValues = (T[]) Array.newInstance(valueType, 1);
        this.defaultValues[0] = defaultValue;
        this.valueType = valueType;
    }

    public IniSchemaOption(String optionName, T[] defaultValues, Class<T> valueType){
        this.optionName = optionName;
        this.defaultValues = defaultValues;
        this.valueType = valueType;
    }

    public String getOptionName()
    {
        return optionName;
    }

    public T getDefaultValue() {
        return defaultValues[0];
    }

    public T[] getDefaultValues() {
        return defaultValues;
    }

    public Class getValueType(){
        return valueType;
    }
}
