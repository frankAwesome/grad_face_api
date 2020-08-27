package com.face.recognition.models.facialrecognition;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdentifyFaceRequest {
    private String[] faceIds;
    private String personGroupId;
    private Double confidenceThreshold;
}
