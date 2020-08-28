package com.face.recognition.service;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;

import com.face.recognition.models.facialrecognition.CandidateResponse;
import com.face.recognition.models.facialrecognition.CreatePersonRequest;
import com.face.recognition.models.facialrecognition.CreatePersonResponse;
import com.face.recognition.models.facialrecognition.DetectFaceResponse;
import com.face.recognition.models.facialrecognition.FaceIdentifyResponseBody;
import com.face.recognition.models.facialrecognition.IdentifyFaceRequest;
import com.face.recognition.models.facialrecognition.PersistedFace;
import com.face.recognition.models.facialrecognition.PersonDetailsResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class FaceIdentifyService {

    public CreatePersonResponse addPerson(String name) {
        final String url = "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        HttpEntity<CreatePersonRequest> request = new HttpEntity<>(new CreatePersonRequest(name), headers);

        CreatePersonResponse result = restTemplate.postForObject(url, request, CreatePersonResponse.class);
        if (isNull(result)) {
            log.info("Could not create face");
            return null;
        }
        return result;
    }

    public String addFace(MultipartFile multipartFile, String personId) {
        final String url = "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons/" + personId + "/persistedFaces";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/octet-stream");
        headers.set("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        byte[] image;
        try {
            image = multipartFile.getBytes();
        } catch (IOException e) {
            log.info("Could not encode image", e);
            return null;
        }
        String encodedImage = new String(Base64.getEncoder().encode(image));
        byte[] decodedString = Base64.getDecoder().decode(encodedImage);

        HttpEntity<byte[]> request = new HttpEntity<>(decodedString, headers);

        PersistedFace result = restTemplate.postForObject(url, request, PersistedFace.class);
        if (isNull(result)) {
            log.info("Could not create face");
            return null;
        }

        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/train");


            URI uri = builder.build();
            HttpPost request2 = new HttpPost(uri);
            request2.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            HttpResponse response2 = httpclient.execute(request2);

            if (isNull(response2)) {
                log.info("Could not train data");
                return null;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
        return "Success";
    }

    public PersonDetailsResponse getPersonName(String personId) {
        final String url = "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/persongroups/2/persons/" + personId;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        ResponseEntity<PersonDetailsResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, PersonDetailsResponse.class);

        if (isNull(result) || isNull(result.getBody())) {
            log.info("Could not retrieve person");
            return null;
        }
        return result.getBody();
    }

    public CandidateResponse faceDetect(MultipartFile multipartFile) {
        final String url = "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/detect";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/octet-stream");
        headers.set("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        byte[] image;
        try {
            image = multipartFile.getBytes();
        } catch (IOException e) {
            log.info("Could not encode image", e);
            return null;
        }
        String encodedImage = new String(Base64.getEncoder().encode(image));
        byte[] decodedString = Base64.getDecoder().decode(encodedImage);

        HttpEntity<byte[]> request = new HttpEntity<>(decodedString, headers);

        DetectFaceResponse[] result = restTemplate.postForObject(url, request, DetectFaceResponse[].class);
        if (isNull(result)) {
            log.info("Could not retrieve faces");
            return null;
        }
        return identifyFace(Arrays.asList(result).get(0).getFaceId());
    }

    private CandidateResponse identifyFace(String faceId) {
        final String url = "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/identify";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        String[] faceIds = {faceId};
        HttpEntity<IdentifyFaceRequest> request = new HttpEntity<>(new IdentifyFaceRequest(faceIds, "2", 0.01), headers);

        FaceIdentifyResponseBody[] result = restTemplate.postForObject(url, request, FaceIdentifyResponseBody[].class);
        if (isNull(result)) {
            log.info("Could not identify face");
            return null;
        }
        return Arrays.asList(result).get(0).getCandidates().get(0);
    }
}
