package com.face.recognition.models.facialRecognition;

import lombok.Data;

@Data
public class DetectFaceResponse {
    public String faceId;
    public FaceRectangle faceRectangle;
}
