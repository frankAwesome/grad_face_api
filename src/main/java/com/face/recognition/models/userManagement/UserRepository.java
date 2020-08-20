package com.face.recognition.models.userManagement;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    int count();

    int save(UserDetails user);
//
//    int update(UserDetails user);

    int deleteById(Long id);

    List<UserDetails> findAll();

    Optional<UserDetails> findById(Long id);

    Optional<UserDetails> findByUserName(String userName);
}