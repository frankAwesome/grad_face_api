package com.face.recognition;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import com.face.recognition.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import com.face.recognition.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1")
public class CompareController {

    BufferedImage imgA;
    BufferedImage imgB;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

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


//    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }


        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest)
    {
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(user.toString());
        System.out.println();
        System.out.println();
        System.out.println();
        userDetailsService.registerUser(user);
        return null;
    }
}