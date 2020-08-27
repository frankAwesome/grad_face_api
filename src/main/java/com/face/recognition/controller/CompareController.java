package com.face.recognition.controller;

import com.face.recognition.models.FaceResponse;
import com.face.recognition.models.facialrecognition.CandidateResponse;
import com.face.recognition.models.facialrecognition.CreatePersonResponse;
import com.face.recognition.models.facialrecognition.Person;
import com.face.recognition.models.facialrecognition.PersonDetailsResponse;
import com.face.recognition.service.CompareService;
import com.face.recognition.service.FaceIdentifyService;
import com.face.recognition.service.TextService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Api(tags = "APEye", produces = APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value ="/api/v1", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompareController {

    private final CompareService compareService;
    private final TextService textService;
    private final FaceIdentifyService faceIdentifyService;

    @ApiOperation(value = "Compares two faces", response = FaceResponse.class, authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/compareFaces")
    public ResponseEntity<FaceResponse> compareFaces(@RequestBody String body) {
        return new ResponseEntity<>(compareService.compare(body), HttpStatus.OK);
    }

    @ApiOperation(value = "Reads text from an image", response = String.class, consumes = "multipart/form-data", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping(value = "/readText", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "plain/text")
    public ResponseEntity<String> readText(@RequestPart(value = "file") MultipartFile file) {
        return new ResponseEntity<>(textService.detectText(file), HttpStatus.OK);
    }

    @ApiOperation(value = "Add person to the AzureFaceDatabase", response = CreatePersonResponse.class, consumes = "application/json", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping(value = "/person", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePersonResponse> addPerson(@RequestBody Person person) {
        return new ResponseEntity<>(faceIdentifyService.addPerson(person.getName()), HttpStatus.OK);
    }

    @ApiOperation(value = "Add face to the AzureFaceDatabase and train the model", response = String.class, consumes = "application/json", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping(value = "/face",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "plain/text")
    public ResponseEntity<String> addFace(@RequestPart(value = "file") MultipartFile multipartFile, @RequestParam String personId) {
        return new ResponseEntity<>(faceIdentifyService.addFace(multipartFile, personId), HttpStatus.OK);
    }

    @ApiOperation(value = "Get person details with person ID", response = PersonDetailsResponse.class, consumes = "application/json", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping(value = "/personDetails")
    public ResponseEntity<PersonDetailsResponse> getPersonDetails(@RequestParam String personId) {
        return new ResponseEntity<>(faceIdentifyService.getPersonName(personId), HttpStatus.OK);
    }

    @ApiOperation(value = "Add face for detection", response = CandidateResponse.class, consumes = "application/json", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping(value = "/faceDetect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateResponse> faceDetect(@RequestPart(value = "file") MultipartFile multipartFile) {
        return new ResponseEntity<>(faceIdentifyService.faceDetect(multipartFile), HttpStatus.OK);
    }
}
