package com.face.recognition.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1")
public class CompareController {

    BufferedImage imgA;
    BufferedImage imgB;

    @PostMapping("/compare")
    public String postImages(@RequestBody String body) {
    
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
        

        

        return "Yoza";
    }
}