package com.face.recognition.models.facialrecognition;

import lombok.Data;

@Data
public class CandidateResponse
{
    public String personId;
    public double confidence;    
}