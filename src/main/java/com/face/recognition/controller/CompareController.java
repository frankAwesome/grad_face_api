package com.face.recognition.controller;

import com.face.recognition.exceptions.ValidationException;
import com.face.recognition.models.FaceResponse;
import com.face.recognition.models.facialRecognition.Person;
import com.face.recognition.models.facialRecognition.ReturnResponse;
import com.face.recognition.service.CompareService;
import com.face.recognition.service.FaceIdentifyService;
import com.face.recognition.models.usermanagement.AuthenticationRequest;
import com.face.recognition.models.usermanagement.AuthenticationResponse;
import com.face.recognition.models.usermanagement.RegisterRequest;
import com.face.recognition.models.usermanagement.User;
import com.face.recognition.service.CompareService;
import com.face.recognition.service.MyUserDetailsService;
import com.face.recognition.service.TextService;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.codec.Base64.InputStream;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Api(tags = "APEye", produces = APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value ="/api/v1", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompareController {

    private final CompareService compareService;
    private final TextService textService;
    private final FaceIdentifyService faceIdentifyService;
    private final MyUserDetailsService userDetailsService;

    @ApiOperation(value = "Compares two faces", response = FaceResponse.class, authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/compare")
    public ResponseEntity<FaceResponse> postImages(@RequestBody String body) {
        return new ResponseEntity<>(compareService.compare(body), HttpStatus.OK);
    }

    @ApiOperation(value = "Reads text from an image", response = String.class, consumes = "multipart/form-data", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping(value = "/text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "plain/text")
    public ResponseEntity<String> readText(@RequestPart(value = "file", required = true) MultipartFile file) {
        return new ResponseEntity<>(textService.detectText(file), HttpStatus.OK);
    }


    @ApiOperation(value = "Add person to the AzureFaceDatabase", response = String.class, consumes = "application/json")
    @PostMapping(value = "/person", consumes = APPLICATION_JSON_VALUE, produces = "plain/text")
    public ResponseEntity<String> addPerson(@RequestBody Person person) throws ClientProtocolException, URISyntaxException, IOException {
        log.debug("adding person");
        return new ResponseEntity<>(faceIdentifyService.addPerson(person.name), HttpStatus.OK);
    }


    @ApiOperation(value = "Add face to the AzureFaceDatabase and train model", response = String.class, consumes = "application/json")
    @PostMapping(value = "/face/{personId:.*}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "plain/text")
    public ResponseEntity<String> addFace(@RequestPart(value = "file", required = true) MultipartFile multipartFile, @PathVariable(required = true) String personId) throws ClientProtocolException, URISyntaxException, IOException {
        log.debug("adding face");
        return new ResponseEntity<>(faceIdentifyService.addFace(multipartFile, personId), HttpStatus.OK);
    }


    @ApiOperation(value = "Get person details with person ID", response = String.class, consumes = "application/json")
    @GetMapping(value = "/personDetails/{personId:.*}", produces = "plain/text")
    public ResponseEntity<String> getPersonDetails(@PathVariable(required = true) String personId) throws ClientProtocolException, URISyntaxException, IOException {
        log.debug("getting person details");
        return new ResponseEntity<>(faceIdentifyService.getPersonName(personId), HttpStatus.OK);
    }


    @ApiOperation(value = "Add face for detection", response = String.class, consumes = "application/json")
    @PostMapping(value = "/faceDetect",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "plain/text")
    public ResponseEntity<String> faceDetect(@RequestPart(value = "file", required = true) MultipartFile multipartFile) throws ClientProtocolException, URISyntaxException, IOException {
        log.debug("adding face");
        return new ResponseEntity<>(faceIdentifyService.faceDetect(multipartFile), HttpStatus.OK);
    }

    @ApiOperation(value = "Logs a user into the api", response = AuthenticationResponse.class)
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws ValidationException {
        return ResponseEntity.ok(userDetailsService.loginUser(authenticationRequest));
    }

    @ApiOperation(value = "Registers a user for the api", response = String.class)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        userDetailsService.registerUser(User.builder().username(registerRequest.getUsername()).password(registerRequest.getPassword()).build());
        return ResponseEntity.ok("User registered");
    }
}