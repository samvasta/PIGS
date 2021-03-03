package com.samvasta.imagegenerator.microservice.actions;

public class ActionResponse {
    public final int httpResponseStatus;
    public final String resultJson;

    public ActionResponse(final int httpResponseStatusIn, final String resultJsonIn){
        httpResponseStatus = httpResponseStatusIn;
        resultJson = resultJsonIn;
    }
}
