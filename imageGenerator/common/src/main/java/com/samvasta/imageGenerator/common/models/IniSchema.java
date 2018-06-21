package com.samvasta.imageGenerator.common.models;

import java.util.ArrayList;
import java.util.List;

public class IniSchema
{
    private final List<IniSchemaSection> sections;

    public IniSchema(){
        sections = new ArrayList<>();
    }

    public List<IniSchemaSection> getSections()
    {
        return sections;
    }

    public IniSchemaSection addSection(String sectionName){
        IniSchemaSection section = new IniSchemaSection(sectionName);
        sections.add(section);
        return section;
    }

    public IniSchemaSection addSection(String sectionName, String sectionComment){
        IniSchemaSection section = new IniSchemaSection(sectionName, sectionComment);
        sections.add(section);
        return section;
    }
}
