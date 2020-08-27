package com.face.recognition.models.facialrecognition;

import java.util.List;
import lombok.Data;

@Data

public class FaceIdentifyResponseBody 
{
    public String faceId;
    public List<CandidateResponse> candidates;
}