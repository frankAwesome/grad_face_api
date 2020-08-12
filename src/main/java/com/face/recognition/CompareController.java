package com.face.recognition;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import com.face.recognition.models.Face;
import com.face.recognition.models.FaceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@RestController
@RequestMapping("/api/v1")
public class CompareController {

    BufferedImage imgA;
    BufferedImage imgB;

    FaceResponse fr;

    @PostMapping("/compare")
    public FaceResponse postImages(@RequestBody String body) {

        String[] image = body.split("split",2);
            
        byte[] one = image[0].getBytes();
        byte[] two = image[1].getBytes();

        byte[] decodedString1 = Base64.getDecoder().decode(one);
        byte[] decodedString2 = Base64.getDecoder().decode(two);


        try
        {
            imgA = ImageIO.read(new ByteArrayInputStream(decodedString1));
            System.out.println(imgA.toString()); 
            imgB = ImageIO.read(new ByteArrayInputStream(decodedString2));
            System.out.println(imgB.toString()); 
        }
        catch(IOException e)
        {
            System.out.println(e.toString());
        }



        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://facet.cognitiveservices.azure.com/face/v1.0/detect");

            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "true");
            builder.setParameter("returnFaceAttributes", "age,gender,emotion,makeup,glasses,facialHair");
            builder.setParameter("recognitionModel", "recognition_01");
            builder.setParameter("returnRecognitionModel", "false");
            builder.setParameter("detectionModel", "detection_01");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "");

            ByteArrayEntity reqEntity = new ByteArrayEntity(decodedString1, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entityone = response.getEntity();



            ByteArrayEntity reqEntityTwo = new ByteArrayEntity(decodedString2, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntityTwo);

            HttpResponse responsetwo = httpclient.execute(request);
            HttpEntity entitytwo = responsetwo.getEntity();


            ObjectMapper mapper = new ObjectMapper();
            List<Face> faceone =
            mapper.readValue(EntityUtils.toString(entityone), new TypeReference<List<Face>>() {});


            List<Face> facetwo =
            mapper.readValue(EntityUtils.toString(entitytwo), new TypeReference<List<Face>>() {});

            Face faceoneone = faceone.get(0);
            Face facetwotwo = facetwo.get(0);


            List<Face> faces = new ArrayList<Face>();
            faces.add(faceoneone);
            faces.add(facetwotwo);

            fr = new FaceResponse();
            fr.faces = faces;
            //we should still do the algorithm for this
            fr.confidence = 92.70;
            


            if (entityone != null) 
            {
                System.out.println(EntityUtils.toString(entityone));
            }
            if (entitytwo != null) 
            {
                System.out.println(EntityUtils.toString(entitytwo));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        return fr;
    }





    
}