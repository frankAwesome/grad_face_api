package com.face.recognition.models.facialrecognition;

import lombok.Data;

@Data
public class DetectFaceResponse {
    public String faceId;
    public FaceRectangle faceRectangle;
}
