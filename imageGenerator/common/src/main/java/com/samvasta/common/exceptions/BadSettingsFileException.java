package com.samvasta.common.exceptions;

public class BadSettingsFileException extends Exception
{
    public BadSettingsFileException(String errorMsgIn){
        super(errorMsgIn);
    }
}
