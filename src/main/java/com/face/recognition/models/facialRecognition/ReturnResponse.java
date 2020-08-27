package com.face.recognition.models.facialRecognition;

import lombok.Data;

@Data
public class ReturnResponse 
{
    private String name;
    private float confidence;

    public ReturnResponse(String name, float confidence)
    {
        name = this.name;
        confidence = this.confidence;
    }
}