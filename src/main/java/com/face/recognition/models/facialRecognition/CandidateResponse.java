package com.face.recognition.models.facialRecognition;

import lombok.Data;

@Data
public class CandidateResponse
{
    public String personId;
    public double confidence;    
}