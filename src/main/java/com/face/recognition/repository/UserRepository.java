package com.face.recognition.repository;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    int count();

    int save(UserDetails user);

    int deleteById(Long id);

    List<UserDetails> findAll();

    Optional<UserDetails> findById(Long id);

    Optional<UserDetails> findByUserName(String userName);
}