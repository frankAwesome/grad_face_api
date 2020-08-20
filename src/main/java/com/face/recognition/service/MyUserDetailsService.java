package com.face.recognition.service;

import com.face.recognition.models.userManagement.JdbcUserRepository;
import com.face.recognition.models.userManagement.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<UserDetails> user;
        try
        {
             user = userRepository.findByUserName(userName);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Incorrect username or password", e);
        }

        if (user.isPresent())
        {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("User: " + user.get().toString() + " loaded from the DB and exists. Still to authenticated.");
            System.out.println();
            System.out.println();
            System.out.println();
            return user.get();
        }
        else
        {
            return null;
        }
    }
}