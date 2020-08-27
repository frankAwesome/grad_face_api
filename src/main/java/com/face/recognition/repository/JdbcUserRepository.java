package com.face.recognition.repository;

import com.face.recognition.models.usermanagement.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from [User]", Integer.class);
    }

    @Override
    public int save(UserDetails user) {
        return jdbcTemplate.update(
                "INSERT INTO [User] (UserName, Password) VALUES (?, ?)",
               user.getUsername(), user.getPassword());
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                "delete [User] where UserID = ?",
                id);
    }

    @Override
    public List<UserDetails> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM [User]",
                (rs, rowNum) ->
                        User.builder()
                                .userID(rs.getLong("UserID"))
                                .username(rs.getString("UserName"))
                                .password(rs.getString("Password"))
                                .build()
        );
    }

    @Override
    public Optional<UserDetails> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM [User] where UserID = ?",
                new Object[]{id},
                (rs, rowNum) ->
                        Optional.of(
                                User.builder()
                                        .userID(rs.getLong("UserID"))
                                        .username(rs.getString("UserName"))
                                        .password(rs.getString("Password"))
                                        .build()
                        )
        );
    }

    @Override
    public Optional<UserDetails> findByUserName(String userName) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM [User] where UserName = ?",
                new Object[]{userName},
                (rs, rowNum) ->
                        Optional.of(
                                User.builder()
                                        .userID(rs.getLong("UserID"))
                                        .username(rs.getString("UserName"))
                                        .password(rs.getString("Password"))
                                        .build()
                        )
        );
    }
}