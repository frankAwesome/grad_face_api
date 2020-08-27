package com.face.recognition.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.face.recognition.models.facialRecognition.CandidateResponse;
import com.face.recognition.models.facialRecognition.CreatePersonResponse;
import com.face.recognition.models.facialRecognition.DetectFaceResponse;
import com.face.recognition.models.facialRecognition.FaceIdentifyResponseBody;
import com.face.recognition.models.facialRecognition.PersonDetailsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class FaceIdentifyService {

    public String addPerson(String name) throws URISyntaxException, ClientProtocolException, IOException {

        try {
    
            URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            JSONObject json = new JSONObject();
            json.put("name", name);

            StringEntity reqEntity = new StringEntity(json.toString());

            request.setEntity(reqEntity);
            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            CreatePersonResponse res = mapper.readValue(EntityUtils.toString(response.getEntity()),new TypeReference<CreatePersonResponse>() {});
            
            return res.personId;

        }catch(Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }

    }

    public String addFace(MultipartFile multipartFile, String personId)
            throws URISyntaxException, ClientProtocolException, IOException {
        
        try {
            URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons/" + personId + "/persistedFaces");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            FileEntity fileEntity = new FileEntity(convertToFile(multipartFile));

            request.setEntity(fileEntity);

            HttpClient httpclient = HttpClients.createDefault();
            httpclient.execute(request);

            URIBuilder builder1 = new URIBuilder(
                    "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/train");

            URI uri1 = builder1.build();
            HttpPost request1 = new HttpPost(uri1);
            request1.setHeader("Content-Type", "application/json");
            request1.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            HttpClient httpclient1 = HttpClients.createDefault();
            httpclient1.execute(request1);

            return "Success";

        }catch(Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }

    }

    public String getPersonName(String personId) throws URISyntaxException, ClientProtocolException, IOException {

        try {
            URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons/" + personId);

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);

            request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            PersonDetailsResponse res =  mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<PersonDetailsResponse>() {});
            return res.name;

        }catch(Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }
    }

    public String faceDetect(MultipartFile multipartFile)
            throws URISyntaxException, ClientProtocolException, IOException {
        
        try {

            URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/detect");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            FileEntity fileEntity = new FileEntity(convertToFile(multipartFile));

            request.setEntity(fileEntity);

            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            List<DetectFaceResponse> res = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<DetectFaceResponse>>() {});

            String faceID = res.get(0).faceId;

            CandidateResponse candidateResponse = identifyFace(faceID);
                
            return "Person: " + getPersonName(candidateResponse.personId) +  ", Confidence: " + candidateResponse.confidence*100 + "%";

        }catch(Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }
    }

    private CandidateResponse identifyFace(String faceID) throws URISyntaxException, ClientProtocolException, IOException
    {
        try {

            URIBuilder builder1 = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/identify");
            URI uri1 = builder1.build();
            HttpPost request1 = new HttpPost(uri1);

            request1.setHeader("Content-Type", "application/json");
            request1.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");


            String json = "{\"faceIds\":[\"" + faceID + "\"],\"personGroupId\":\"2\", \"confidenceThreshold\": 0.01}";
            StringEntity entity1 = new StringEntity(json);
            request1.setEntity(entity1);



            HttpClient httpclient1 = HttpClients.createDefault();
            HttpResponse response1 = httpclient1.execute(request1);

            ObjectMapper mapper1 = new ObjectMapper();
            List<FaceIdentifyResponseBody> res1 =  mapper1.readValue(EntityUtils.toString(response1.getEntity()), new TypeReference<List<FaceIdentifyResponseBody>>() {});

            return res1.get(0).candidates.get(0);

        }catch(Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }
    }

    private static File convertToFile(MultipartFile file) {
        try {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile;
        } catch (Exception e) {
            log.info("Could not convert file: ", e);
            return null;
        }
    }

}