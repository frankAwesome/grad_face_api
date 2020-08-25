package com.face.recognition.service;

import com.face.recognition.models.Face;
import com.face.recognition.models.FaceResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class CompareService {

    private double calcDistance(double x1, double y1, double x2, double y2) {
                
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public Face normalizeFaceLandMarks(Face face){

        Face normalizedFace = new Face();

        return normalizedFace;
    }

    // gets the relative distance difference between different face's features
    private double calculateDifferenceRatio(double referenceDistance1, double referenceDistance2, double distance1, double distance2) {

        double ratio = (referenceDistance1/distance1)/(referenceDistance2/distance2);
        
        return Math.abs(1-ratio);
    }

    // gets distances between certain facial features
    private ArrayList<Double> calculateDistanceList(Face face) {

        ArrayList<Double> distanceList = new ArrayList<Double>();

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

    public FaceResponse compare(String body) {
        BufferedImage imgA;
        BufferedImage imgB;

        FaceResponse fr;

        String[] image = body.split("split",2);
        byte[] one = image[0].getBytes();
        byte[] two = image[1].getBytes();
        byte[] decodedString1 = Base64.getDecoder().decode(one);
        byte[] decodedString2 = Base64.getDecoder().decode(two);

        try {
            imgA = ImageIO.read(new ByteArrayInputStream(decodedString1));
            log.trace("{}", imgA.toString());
            imgB = ImageIO.read(new ByteArrayInputStream(decodedString2));
            log.trace("{}", imgB.toString());
        } catch(IOException e) {
            log.debug("{}", e.toString());
        }

        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://bitzerfacetest.cognitiveservices.azure.com/face/v1.0/detect");

            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "true");
            builder.setParameter("returnFaceAttributes", "age,gender,emotion,makeup,glasses,facialHair");
            builder.setParameter("recognitionModel", "recognition_01");
            builder.setParameter("returnRecognitionModel", "false");
            builder.setParameter("detectionModel", "detection_01");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "3ae481e8934c4231af64d040cbf8e3c3");

            ByteArrayEntity reqEntity = new ByteArrayEntity(decodedString1, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);
              
            HttpResponse response = httpclient.execute(request);
            HttpEntity entityone = response.getEntity();


            ByteArrayEntity reqEntityTwo = new ByteArrayEntity(decodedString2, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntityTwo);

            HttpResponse responsetwo = httpclient.execute(request);
            HttpEntity entitytwo = responsetwo.getEntity();

            ObjectMapper mapper = new ObjectMapper();
            List<Face> faceOneList =
                    mapper.readValue(EntityUtils.toString(entityone), new TypeReference<List<Face>>() {});

            List<Face> faceTwoList =
                    mapper.readValue(EntityUtils.toString(entitytwo), new TypeReference<List<Face>>() {});

            Face faceOne = faceOneList.get(0);
            Face faceTwo = faceTwoList.get(0);

            List<Face> faces = new ArrayList<>();
            faces.add(faceOne);
            faces.add(faceTwo);

            ArrayList<Double> distances1 = this.calculateDistanceList(faceOne);

            ArrayList<Double> distances2 = this.calculateDistanceList(faceTwo);

            double difference = 0;

            for (int i = 1; i < distances1.size(); i++) {

                difference += this.calculateDifferenceRatio(distances1.get(0), distances2.get(0), distances1.get(i), distances2.get(i));
            }

            difference = Math.pow(difference * 8, 2);

            System.out.println("gender1:" + faceOne.faceAttributes.gender);
            System.out.println("gender2:" + faceTwo.faceAttributes.gender);

            fr = new FaceResponse();
            fr.faces = faces;

            double genderDifference = 0;

            faceOne.faceAttributes.gender.equals(faceTwo.faceAttributes.gender);

            if (!faceOne.faceAttributes.gender.equals(faceTwo.faceAttributes.gender))
            {
                genderDifference = 50;
            }

            System.out.println("difference " + difference);

            //get angles between pupils and nosetip
            float anglepupilsone = (float) Math.toDegrees(Math.atan2(faceOne.faceLandmarks.pupilRight.y - faceOne.faceLandmarks.pupilLeft.y, faceOne.faceLandmarks.pupilRight.x - faceOne.faceLandmarks.pupilLeft.x));
            float anglepupilstwo = (float) Math.toDegrees(Math.atan2(faceTwo.faceLandmarks.pupilRight.y - faceTwo.faceLandmarks.pupilLeft.y, faceTwo.faceLandmarks.pupilRight.x - faceTwo.faceLandmarks.pupilLeft.x));

            float angleleftone = (float) Math.toDegrees(Math.atan2(faceOne.faceLandmarks.noseTip.y - faceOne.faceLandmarks.pupilLeft.y, faceOne.faceLandmarks.noseTip.x - faceOne.faceLandmarks.pupilLeft.x));
            float anglelefttwo = (float) Math.toDegrees(Math.atan2(faceTwo.faceLandmarks.noseTip.y - faceTwo.faceLandmarks.pupilLeft.y, faceTwo.faceLandmarks.noseTip.x - faceTwo.faceLandmarks.pupilLeft.x));

            float anglerightone = (float) Math.toDegrees(Math.atan2(faceOne.faceLandmarks.noseTip.y - faceOne.faceLandmarks.pupilRight.y, faceOne.faceLandmarks.noseTip.x - faceOne.faceLandmarks.pupilRight.x));
            float anglerighttwo = (float) Math.toDegrees(Math.atan2(faceTwo.faceLandmarks.noseTip.y - faceTwo.faceLandmarks.pupilRight.y, faceTwo.faceLandmarks.noseTip.x - faceTwo.faceLandmarks.pupilRight.x));
            float trilefttangleone = (float)angleleftone + anglepupilsone;
            float trirightangleone = (float) 360 - anglerightone - 180 + anglepupilsone;
            float tribottomangleone = (float) 180 - (trilefttangleone + trirightangleone);

            float trilefttangletwo = (float)anglelefttwo + anglepupilstwo;
            float trirightangletwo = (float) 360 - anglerighttwo - 180 + anglepupilstwo;
            float tribottomangletwo = (float) 180 - (trilefttangletwo + trirightangletwo);

            float weight1 = (float)tribottomangleone / tribottomangletwo;

            if (weight1 > 1)
                weight1 = 1 - (weight1 -1);

            if (difference > 100) {
                difference = 100;
            }

            double comparisonRatio = Math.abs((100-difference)/100);
            fr.confidence =  (double) ((weight1*100 / 4 ) +  ((comparisonRatio*100) / 4 * 3));

            System.out.println("weight1:" + weight1);
            System.out.println("differenceRatio:" + comparisonRatio);

            if (isNull(entityone)) {
                log.debug("{}", EntityUtils.toString(entityone));
            }
            if (isNull(entitytwo)) {
                log.debug("{}", EntityUtils.toString(entitytwo));
            }
            return fr;
        } catch (Exception e) {
            log.debug("{}", e.getMessage());
        }
        return null;
    }
}
