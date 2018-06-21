package com.samvasta.common.models;

public class IniSchemaOption<T>
{
    private final String optionName;
    private final Class valueType;
    private final T defaultValue;

    public IniSchemaOption(String optionName, T defaultValue, Class<T> valueType){
        this.optionName = optionName;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
    }

    public String getOptionName()
    {
        return optionName;
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }

    public Class getValueType(){
        return valueType;
    }
}
