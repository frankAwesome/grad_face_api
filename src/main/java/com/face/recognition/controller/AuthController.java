package com.face.recognition.controller;

import com.face.recognition.exceptions.ValidationException;
import com.face.recognition.models.usermanagement.AuthenticationRequest;
import com.face.recognition.models.usermanagement.AuthenticationResponse;
import com.face.recognition.models.usermanagement.RegisterRequest;
import com.face.recognition.models.usermanagement.User;
import com.face.recognition.service.MyUserDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Api(tags = "Auth", produces = APPLICATION_JSON_VALUE)
@RestController
@RequestMapping(value ="/api/v1", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final MyUserDetailsService userDetailsService;

    @ApiOperation(value = "Logs a user into the api", response = AuthenticationResponse.class)
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws ValidationException {
        return ResponseEntity.ok(userDetailsService.loginUser(authenticationRequest));
    }

    @ApiOperation(value = "Registers a user for the api", response = String.class)
    @PostMapping(value = "/register", produces = "plain/text")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        userDetailsService.registerUser(User.builder().username(registerRequest.getUsername()).password(registerRequest.getPassword()).build());
        return ResponseEntity.ok("User registered");
    }
}
