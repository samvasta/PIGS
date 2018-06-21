package com.samvasta.common.models;

import java.util.ArrayList;
import java.util.List;

public class IniSchemaSection
{
    private final String sectionName;
    private final String sectionComment;
    private final List<IniSchemaOption<?>> options;

    IniSchemaSection(String sectionName){
        options = new ArrayList<>();
        this.sectionName = sectionName;
        sectionComment = "";
    }

    IniSchemaSection(String sectionName, String sectionComment){
        options = new ArrayList<>();
        this.sectionName = sectionName;
        this.sectionComment = sectionComment;
    }

    public String getSectionName(){
        return sectionName;
    }

    public String getSectionComment()
    {
        return sectionComment;
    }

    public List<IniSchemaOption<?>> getOptions()
    {
        return options;
    }

    public <T> IniSchemaOption addOption(String optionName, T defaultValue){
        IniSchemaOption option = new IniSchemaOption<T>(optionName, defaultValue, (Class<T>)defaultValue.getClass());
        options.add(option);
        return option;
    }
}
