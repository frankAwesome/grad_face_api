package com.face.recognition.controller;

import com.face.recognition.models.FaceResponse;
import com.face.recognition.service.CompareService;
import com.face.recognition.service.TextService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Api(tags = "FaceRecog", produces = APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value ="/api/v1", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompareController {

    private final CompareService compareService;
    private final TextService textService;

    @ApiOperation(value = "Compares two faces", response = FaceResponse.class)
    @PostMapping("/compare")
    public ResponseEntity<FaceResponse> postImages(@RequestBody String body) {
        return new ResponseEntity<>(compareService.compare(body), HttpStatus.OK);
    }

    @ApiOperation(value = "Reads text from an image", response = String.class, consumes = "multipart/form-data")
    @PostMapping(value = "/text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "plain/text")
    public ResponseEntity<String> readText(@RequestPart(value = "file", required = true) MultipartFile file) {
        log.debug("attempting to read text");
        return new ResponseEntity<>(textService.detectText(file), HttpStatus.OK);
    }
}