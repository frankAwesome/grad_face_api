package com.face.recognition.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.face.recognition.models.facialRecognition.CreatePersonResponse;
import com.face.recognition.models.facialRecognition.DetectFaceResponse;
import com.face.recognition.models.facialRecognition.PersonDetailsResponse;
import com.fasterxml.jackson.core.JsonParser;
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
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class FaceIdentifyService {

    public String addPerson(String name) throws URISyntaxException, ClientProtocolException, IOException {

        URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons");

        //builder.addParameter("name", name);

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
        //HttpEntity entityone = response.getEntity();

        //HttpEntity entity = response.getEntity();

        ObjectMapper mapper = new ObjectMapper();
        CreatePersonResponse res =  mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<CreatePersonResponse>() {});
        
        return res.personId;
    }


    public String addFace(MultipartFile multipartFile, String personId) throws URISyntaxException, ClientProtocolException, IOException {

        URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons/"+ personId + "/persistedFaces");

        //builder.addParameter("name", name);

        URI uri = builder.build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/octet-stream");
        request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        
        FileEntity fileEntity = new FileEntity(convertToFile(multipartFile));

        request.setEntity(fileEntity);

        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = httpclient.execute(request);
        
        System.out.println(response);


        URIBuilder builder1 = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/train");

        URI uri1 = builder1.build();
        HttpPost request1 = new HttpPost(uri1);
        request1.setHeader("Content-Type", "application/json");
        request1.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        HttpClient httpclient1 = HttpClients.createDefault();
        HttpResponse response1 = httpclient1.execute(request1);

        System.out.println(response1);

        return "Success";
    }

    public String getPersonDetails(String personId) throws URISyntaxException, ClientProtocolException, IOException 
    {

        URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons/350185f0-bfa9-4815-9f38-285385ccb870");

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = httpclient.execute(request);

        System.out.println(personId);
        System.out.println(response);

        ObjectMapper mapper = new ObjectMapper();
        PersonDetailsResponse res =  mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<PersonDetailsResponse>() {});

        return res.name;
    }

    public String faceDetect(MultipartFile multipartFile) throws URISyntaxException, ClientProtocolException, IOException {

        URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/detect");

        URI uri = builder.build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/octet-stream");
        request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        FileEntity fileEntity = new FileEntity(convertToFile(multipartFile));

        request.setEntity(fileEntity);

        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = httpclient.execute(request);

        System.out.println(response);
        System.out.println(response);
        
        // ObjectMapper mapper = new ObjectMapper();
        // DetectFaceResponse res =  mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<DetectFaceResponse>() {});





        //RestTemplate restTemplate = new RestTemplate();

        //String token = restTemplate.postForObject(uri, response.getEntity(), String.class);


        return "";
    }

    private String identifyFace()
    {
        return "true";
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