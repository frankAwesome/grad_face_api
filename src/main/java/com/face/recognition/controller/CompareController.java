package com.face.recognition.controller;

import com.face.recognition.models.FaceResponse;
import com.face.recognition.service.CompareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Api(tags = "FaceRecog", produces = APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value ="/api/v1", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompareController {

    private final CompareService compareService;

    @ApiOperation(value = "Compares two faces", response = FaceResponse.class)
    @PostMapping("/compare")
    public ResponseEntity<FaceResponse> postImages(@RequestBody String body) {
        return new ResponseEntity<>(compareService.compare(body), HttpStatus.OK);
    }
}