package com.face.recognition.service;

import com.face.recognition.models.Face;
import com.face.recognition.models.FaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class CompareService {

    public FaceResponse compare(String body) {
        FaceResponse fr = new FaceResponse();

        String[] image = body.split("split",2);
        byte[] decodedString1 = Base64.getDecoder().decode(image[0].getBytes());
        byte[] decodedString2 = Base64.getDecoder().decode(image[1].getBytes());

        Face faceOne = getFace(decodedString1);
        Face faceTwo = getFace(decodedString2);

        List<Face> faces = new ArrayList<>();
        faces.add(faceOne);
        faces.add(faceTwo);
        fr.faces = faces;

        double weight = getWeight(faceOne, faceTwo);
        double comparisonRatio = getComparisonRatio(faceOne, faceTwo);
        fr.confidence = ((weight*100 / 4 ) +  ((comparisonRatio*100) / 4 * 3));

        return fr;
    }

    private double calcDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    // gets the relative distance difference between different face's features
    private double calculateDifferenceRatio(double referenceDistance1, double referenceDistance2, double distance1, double distance2) {
        double ratio = (referenceDistance1/distance1)/(referenceDistance2/distance2);
        
        return Math.abs(1-ratio);
    }

    // gets distances between certain facial features
    private ArrayList<Double> calculateDistanceList(Face face) {
        ArrayList<Double> distanceList = new ArrayList<>();

        distanceList.add(this.calcDistance(face.faceLandmarks.eyeLeftOuter.x, face.faceLandmarks.eyeLeftOuter.y, face.faceLandmarks.eyeRightOuter.x, face.faceLandmarks.eyeRightOuter.y));
        distanceList.add(this.calcDistance(face.faceLandmarks.eyebrowLeftOuter.x, face.faceLandmarks.eyebrowLeftOuter.y, face.faceLandmarks.eyebrowRightOuter.x, face.faceLandmarks.eyebrowRightOuter.y));
        distanceList.add((this.calcDistance(face.faceLandmarks.eyeLeftOuter.x, face.faceLandmarks.eyeLeftOuter.y, face.faceLandmarks.eyeLeftInner.x, face.faceLandmarks.eyeLeftInner.y) + this.calcDistance(face.faceLandmarks.eyeRightOuter.x, face.faceLandmarks.eyeRightOuter.y, face.faceLandmarks.eyeRightInner.x, face.faceLandmarks.eyeRightInner.y))/2);
        distanceList.add(this.calcDistance(face.faceLandmarks.mouthLeft.x, face.faceLandmarks.mouthLeft.y, face.faceLandmarks.mouthRight.x, face.faceLandmarks.mouthRight.y));
        distanceList.add(this.calcDistance(face.faceLandmarks.noseLeftAlarOutTip.x, face.faceLandmarks.noseLeftAlarOutTip.y, face.faceLandmarks.noseRightAlarOutTip.x, face.faceLandmarks.noseRightAlarOutTip.y));
        distanceList.add(this.calcDistance(face.faceLandmarks.noseLeftAlarTop.x, face.faceLandmarks.noseLeftAlarTop.y, face.faceLandmarks.noseRightAlarTop.x, face.faceLandmarks.noseRightAlarTop.y));
        distanceList.add(this.calcDistance(face.faceLandmarks.pupilLeft.x, face.faceLandmarks.pupilLeft.y, face.faceLandmarks.noseTip.x, face.faceLandmarks.noseTip.y));
        distanceList.add(this.calcDistance(face.faceLandmarks.pupilRight.x, face.faceLandmarks.pupilRight.y, face.faceLandmarks.noseTip.x, face.faceLandmarks.noseTip.y));
        
        return distanceList;
    }

    private Face getFace(byte[] decodedString) {
        final String url = "https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/detect";

        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("returnFaceId", "true")
                .queryParam("returnFaceLandmarks", "true")
                .queryParam("returnFaceAttributes", "age,gender,emotion,makeup,glasses,facialHair")
                .queryParam("recognitionModel", "recognition_01")
                .queryParam("returnRecognitionModel", "false")
                .queryParam("detectionModel", "detection_01");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/octet-stream");
        headers.set("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

        HttpEntity<byte[]> request = new HttpEntity<>(decodedString, headers);
        Face[] result = restTemplate.postForObject(builder.toUriString(), request, Face[].class);
        if (isNull(result)) {
            log.info("Could not retrieve faces");
            return null;
        }
        return Arrays.asList(result).get(0);
    }

    private double getComparisonRatio(Face faceOne, Face faceTwo) {
        ArrayList<Double> distances1 = calculateDistanceList(faceOne);
        ArrayList<Double> distances2 = calculateDistanceList(faceTwo);
        double difference = 0;

        for (int i = 1; i < distances1.size(); i++) {
            difference += calculateDifferenceRatio(distances1.get(0), distances2.get(0), distances1.get(i), distances2.get(i));
        }

        difference = Math.pow(difference * 8, 2);

        if (difference > 100) {
            difference = 100;
        }

        return Math.abs((100-difference)/100);
    }

    private double getWeight(Face faceOne, Face faceTwo) {
        double weight = getTriBottomAngle(faceOne) / getTriBottomAngle(faceTwo);
        if (weight > 1) {
            weight = 1 - (weight - 1);
        }
        return weight;
    }

    private double getTriBottomAngle(Face face) {
        double anglepupilsone = Math.toDegrees(Math.atan2(
                face.faceLandmarks.pupilRight.y - face.faceLandmarks.pupilLeft.y,
                face.faceLandmarks.pupilRight.x - face.faceLandmarks.pupilLeft.x));

        double angleleftone = Math.toDegrees(Math.atan2(
                face.faceLandmarks.noseTip.y - face.faceLandmarks.pupilLeft.y,
                face.faceLandmarks.noseTip.x - face.faceLandmarks.pupilLeft.x));

        double anglerightone = Math.toDegrees(Math.atan2(
                face.faceLandmarks.noseTip.y - face.faceLandmarks.pupilRight.y,
                face.faceLandmarks.noseTip.x - face.faceLandmarks.pupilRight.x));

        double trilefttangleone = angleleftone + anglepupilsone;
        double trirightangleone = 360 - anglerightone - 180 + anglepupilsone;

        return 180 - (trilefttangleone + trirightangleone);
    }
}
