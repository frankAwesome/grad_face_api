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

    
    public double calcDistance(double x1, double y1, double x2, double y2) {
                
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public Face normalizeFaceLandMarks(Face face){

        Face normalizedFace = new Face();

        return normalizedFace;
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

            //System.out.println(entityone);


            ByteArrayEntity reqEntityTwo = new ByteArrayEntity(decodedString2, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntityTwo);

            HttpResponse responsetwo = httpclient.execute(request);
            HttpEntity entitytwo = responsetwo.getEntity();

            //System.out.println(entitytwo);

            ObjectMapper mapper = new ObjectMapper();
            List<Face> faceone =
                    mapper.readValue(EntityUtils.toString(entityone), new TypeReference<List<Face>>() {});

            List<Face> facetwo =
                    mapper.readValue(EntityUtils.toString(entitytwo), new TypeReference<List<Face>>() {});

            Face faceoneone = faceone.get(0);
            Face facetwotwo = facetwo.get(0);

            List<Face> faces = new ArrayList<>();
            faces.add(faceoneone);
            faces.add(facetwotwo);

            double eyebrowDistance1 = this.calcDistance(faceoneone.faceLandmarks.eyebrowLeftOuter.x, faceoneone.faceLandmarks.eyebrowLeftOuter.y, faceoneone.faceLandmarks.eyebrowRightOuter.x, faceoneone.faceLandmarks.eyebrowRightOuter.y);

            double avgEyeWidth1 = (this.calcDistance(faceoneone.faceLandmarks.eyeLeftOuter.x, faceoneone.faceLandmarks.eyeLeftOuter.y, faceoneone.faceLandmarks.eyeLeftInner.x, faceoneone.faceLandmarks.eyeLeftInner.y) + this.calcDistance(faceoneone.faceLandmarks.eyeRightOuter.x, faceoneone.faceLandmarks.eyeRightOuter.y, faceoneone.faceLandmarks.eyeRightInner.x, faceoneone.faceLandmarks.eyeRightInner.y))/2;

            double mouthWidth1 = this.calcDistance(faceoneone.faceLandmarks.mouthLeft.x, faceoneone.faceLandmarks.mouthLeft.y, faceoneone.faceLandmarks.mouthRight.x, faceoneone.faceLandmarks.mouthRight.y);

            double noseBottomWidth1 = this.calcDistance(faceoneone.faceLandmarks.noseLeftAlarOutTip.x, faceoneone.faceLandmarks.noseLeftAlarOutTip.y, faceoneone.faceLandmarks.noseRightAlarOutTip.x, faceoneone.faceLandmarks.noseRightAlarOutTip.y);
            
            double noseMiddleWidth1 = this.calcDistance(faceoneone.faceLandmarks.noseLeftAlarTop.x, faceoneone.faceLandmarks.noseLeftAlarTop.y, faceoneone.faceLandmarks.noseRightAlarTop.x, faceoneone.faceLandmarks.noseRightAlarTop.y);

            double eyeDistance1 = this.calcDistance(faceoneone.faceLandmarks.eyeLeftOuter.x, faceoneone.faceLandmarks.eyeLeftOuter.y, faceoneone.faceLandmarks.eyeRightOuter.x, faceoneone.faceLandmarks.eyeRightOuter.y);

            double leftEyebrowToMiddleNose1 = this.calcDistance(faceoneone.faceLandmarks.pupilLeft.x, faceoneone.faceLandmarks.pupilLeft.y, faceoneone.faceLandmarks.noseTip.x, faceoneone.faceLandmarks.noseTip.y);

            double rigthEyebrowToMiddleNose1 = this.calcDistance(faceoneone.faceLandmarks.pupilRight.x, faceoneone.faceLandmarks.pupilRight.y, faceoneone.faceLandmarks.noseTip.x, faceoneone.faceLandmarks.noseTip.y);


            // System.out.println("eyeDistance1/eyebrowDistance1: " + eyeDistance1/eyebrowDistance1);
            
            // System.out.println("mouthWidth1/eyeDistance1: " + mouthWidth1/eyeDistance1);

            // System.out.println("eyeWidth/eyeDistance1: " + avgEyeWidth1/eyeDistance1);

            // System.out.println("noseBottomWidth1/eyeDistance1: " + noseBottomWidth1/eyeDistance1);

            // System.out.println("leftEyebrowToMiddleNose1/eyeDistance1: " + leftEyebrowToMiddleNose1/eyeDistance1);

            // System.out.println("rigthEyebrowToMiddleNose1/eyeDistance1: " + rigthEyebrowToMiddleNose1/eyeDistance1);

            // System.out.println("noseBottomMiddleWidth1/eyeDistance1: " + noseMiddleWidth1/eyeDistance1);


            double eyebrowDistance2 = this.calcDistance(facetwotwo.faceLandmarks.eyebrowLeftOuter.x, facetwotwo.faceLandmarks.eyebrowLeftOuter.y, facetwotwo.faceLandmarks.eyebrowRightOuter.x, facetwotwo.faceLandmarks.eyebrowRightOuter.y);

            double eyeDistance2 = this.calcDistance(facetwotwo.faceLandmarks.eyeLeftOuter.x, facetwotwo.faceLandmarks.eyeLeftOuter.y, facetwotwo.faceLandmarks.eyeRightOuter.x, facetwotwo.faceLandmarks.eyeRightOuter.y);

            double mouthWidth2 = this.calcDistance(facetwotwo.faceLandmarks.mouthLeft.x, facetwotwo.faceLandmarks.mouthLeft.y, facetwotwo.faceLandmarks.mouthRight.x, facetwotwo.faceLandmarks.mouthRight.y);

            double avgEyeWidth2 = (this.calcDistance(facetwotwo.faceLandmarks.eyeLeftOuter.x, facetwotwo.faceLandmarks.eyeLeftOuter.y, facetwotwo.faceLandmarks.eyeLeftInner.x, facetwotwo.faceLandmarks.eyeLeftInner.y) + this.calcDistance(facetwotwo.faceLandmarks.eyeRightOuter.x, facetwotwo.faceLandmarks.eyeRightOuter.y, facetwotwo.faceLandmarks.eyeRightInner.x, facetwotwo.faceLandmarks.eyeRightInner.y))/2;

            double noseBottomWidth2 = this.calcDistance(facetwotwo.faceLandmarks.noseLeftAlarOutTip.x, facetwotwo.faceLandmarks.noseLeftAlarOutTip.y, facetwotwo.faceLandmarks.noseRightAlarOutTip.x, facetwotwo.faceLandmarks.noseRightAlarOutTip.y);

            double noseMiddleWidth2 = this.calcDistance(facetwotwo.faceLandmarks.noseLeftAlarTop.x, facetwotwo.faceLandmarks.noseLeftAlarTop.y, facetwotwo.faceLandmarks.noseRightAlarTop.x, facetwotwo.faceLandmarks.noseRightAlarTop.y);

            double leftEyebrowToMiddleNose2 = this.calcDistance(facetwotwo.faceLandmarks.pupilLeft.x, facetwotwo.faceLandmarks.pupilLeft.y, facetwotwo.faceLandmarks.noseTip.x, facetwotwo.faceLandmarks.noseTip.y);

            double rigthEyebrowToMiddleNose2 = this.calcDistance(facetwotwo.faceLandmarks.pupilRight.x, facetwotwo.faceLandmarks.pupilRight.y, facetwotwo.faceLandmarks.noseTip.x, facetwotwo.faceLandmarks.noseTip.y);
            

            // System.out.println("eyeDistance2/eyebrowDistance2: " + eyeDistance2/eyebrowDistance2);

            // System.out.println("mouthWidth2/eyeDistance2: " + mouthWidth2/eyeDistance2);

            // System.out.println("eyeWidth/eyeDistance2: " + avgEyeWidth2/eyeDistance2);

            // System.out.println("noseBottomWith/eyeDistance2: " + noseBottomWidth2/eyeDistance2);

            // System.out.println("leftEyebrowToMiddleNose2/eyeDistance2: " + leftEyebrowToMiddleNose2/eyeDistance2);

            // System.out.println("rigthEyebrowToMiddleNose2/eyeDistance2: " + rigthEyebrowToMiddleNose2/eyeDistance2);

            // System.out.println("noseBottomMiddleWidth1/eyeDistance2: " + noseMiddleWidth2/eyeDistance2);

            double difference = Math.abs(eyeDistance1/eyebrowDistance1 - eyeDistance2/eyebrowDistance2);
            difference += Math.abs(mouthWidth1/eyeDistance1 - mouthWidth2/eyeDistance2);
            difference += Math.abs(avgEyeWidth1/eyeDistance1 - avgEyeWidth2/eyeDistance2);
            difference += Math.abs(noseBottomWidth1/eyeDistance1 - noseBottomWidth2/eyeDistance2);
            difference += 1.3*Math.abs(leftEyebrowToMiddleNose1/eyeDistance1 - leftEyebrowToMiddleNose2/eyeDistance2);
            difference += 1.3*Math.abs(rigthEyebrowToMiddleNose1/eyeDistance1 - rigthEyebrowToMiddleNose2/eyeDistance2);
            difference += Math.abs(noseMiddleWidth1/eyeDistance1 - noseMiddleWidth2/eyeDistance2);

            System.out.println("gender1:" + faceoneone.faceAttributes.gender);
            System.out.println("gender2:" + facetwotwo.faceAttributes.gender);

            fr = new FaceResponse();
            fr.faces = faces;

            double genderDifference = 0;

            faceoneone.faceAttributes.gender.equals(facetwotwo.faceAttributes.gender);

            if (!faceoneone.faceAttributes.gender.equals(facetwotwo.faceAttributes.gender))
            {
                genderDifference = 50;
            }
                
            fr.confidence = 100 - genderDifference - Math.pow(difference * 15, 2);
            
            //System.out.println(facetwotwo);


            System.out.println("difference " + difference);
            //we should still do the algorithm for this
            //fr.confidence = 92.70;

            //weight1

            //get angles between pupils and nosetip
            float anglepupilsone = (float) Math.toDegrees(Math.atan2(faceoneone.faceLandmarks.pupilRight.y - faceoneone.faceLandmarks.pupilLeft.y, faceoneone.faceLandmarks.pupilRight.x - faceoneone.faceLandmarks.pupilLeft.x));
            float anglepupilstwo = (float) Math.toDegrees(Math.atan2(facetwotwo.faceLandmarks.pupilRight.y - facetwotwo.faceLandmarks.pupilLeft.y, facetwotwo.faceLandmarks.pupilRight.x - facetwotwo.faceLandmarks.pupilLeft.x));

            float angleleftone = (float) Math.toDegrees(Math.atan2(faceoneone.faceLandmarks.noseTip.y - faceoneone.faceLandmarks.pupilLeft.y, faceoneone.faceLandmarks.noseTip.x - faceoneone.faceLandmarks.pupilLeft.x));
            float anglelefttwo = (float) Math.toDegrees(Math.atan2(facetwotwo.faceLandmarks.noseTip.y - facetwotwo.faceLandmarks.pupilLeft.y, facetwotwo.faceLandmarks.noseTip.x - facetwotwo.faceLandmarks.pupilLeft.x));

            float anglerightone = (float) Math.toDegrees(Math.atan2(faceoneone.faceLandmarks.noseTip.y - faceoneone.faceLandmarks.pupilRight.y, faceoneone.faceLandmarks.noseTip.x - faceoneone.faceLandmarks.pupilRight.x));
            float anglerighttwo = (float) Math.toDegrees(Math.atan2(facetwotwo.faceLandmarks.noseTip.y - facetwotwo.faceLandmarks.pupilRight.y, facetwotwo.faceLandmarks.noseTip.x - facetwotwo.faceLandmarks.pupilRight.x));
            float trilefttangleone = (float)angleleftone + anglepupilsone;
            float trirightangleone = (float) 360 - anglerightone - 180 + anglepupilsone;
            float tribottomangleone = (float) 180 - (trilefttangleone + trirightangleone);

            float trilefttangletwo = (float)anglelefttwo + anglepupilstwo;
            float trirightangletwo = (float) 360 - anglerighttwo - 180 + anglepupilstwo;
            float tribottomangletwo = (float) 180 - (trilefttangletwo + trirightangletwo);

            float weight1 = (float)tribottomangleone / tribottomangletwo;

            if (weight1 > 1)
                weight1 = 1 - (weight1 -1);

            // fr.confidence = (double)weight1;


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
