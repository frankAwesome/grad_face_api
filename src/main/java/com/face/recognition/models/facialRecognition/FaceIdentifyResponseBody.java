package com.face.recognition.models.facialRecognition;

import java.util.List;
import lombok.Data;

@Data

public class FaceIdentifyResponseBody 
{
    public String faceId;
    public List<CandidateResponse> candidates;
}