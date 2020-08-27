package com.face.recognition.models.facialrecognition;

import java.util.List;

import lombok.Data;

@Data
public class FaceIdentifyRequestBody 
{
    private List<String> faceIds;
    private String personGroupId;


    public FaceIdentifyRequestBody(List<String> faceIds, String personGroupId)
    {
        faceIds = this.faceIds;
        personGroupId = this.personGroupId;
    }

}