package com.face.recognition.service;

import com.face.recognition.exceptions.ValidationException;
import com.face.recognition.models.usermanagement.AuthenticationRequest;
import com.face.recognition.models.usermanagement.AuthenticationResponse;
import com.face.recognition.repository.JdbcUserRepository;
import com.face.recognition.models.usermanagement.User;
import com.face.recognition.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    public void registerUser(User user) {
        Optional<UserDetails> dbUser;
        try
        {
            dbUser = userRepository.findByUserName(user.getUsername());
            if (dbUser.isPresent())
            {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "User with username " + user.getUsername() +  " already exists. Please Register with another user.");
            }
        }
        catch (EmptyResultDataAccessException e)
        {
            System.out.println();
        }
        catch (ResponseStatusException e)
        {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User with username " + user.getUsername() +  " already exists. Please Register with another user.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        Optional<UserDetails> user;
        try {
             user = userRepository.findByUserName(userName);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Incorrect username or password", e);
        }

        if (user.isPresent()) {
            log.info("User: {} loaded from the DB and exists. Still to authenticated.", user.get());
            return user.get();
        } else {
            return null;
        }
    }

    public AuthenticationResponse loginUser(AuthenticationRequest request) throws ValidationException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.info("Incorrect username or password ", e);
            throw new ValidationException("Incorrect username or password");
        }

        final String jwt = jwtTokenUtil.generateToken(loadUserByUsername(request.getUsername()));

        return new AuthenticationResponse(jwt);
    }
}