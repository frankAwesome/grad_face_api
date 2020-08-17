package com.face.recognition.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    // Spring Boot will create and configure DataSource and JdbcTemplate
    // To use it, just @Autowired
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

//    @Override
//    public int update(UserDetails user) {
//        return jdbcTemplate.update(
//                "update [User] set UserName = ?, Password = ? where UserID = ?",
//                user.getUsername(), user.getPassword(), user.getUserID());
//    }


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
                        new User(
                                rs.getLong("UserID"),
                                rs.getString("UserName"),
                                rs.getString("Password")
                        )
        );
    }

    // jdbcTemplate.queryForObject, populates a single object
    @Override
    public Optional<UserDetails> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM [User] where UserID = ?",
                new Object[]{id},
                (rs, rowNum) ->
                        Optional.of(new User(
                                rs.getLong("UserID"),
                                rs.getString("UserName"),
                                rs.getString("Password")
                        ))
        );
    }

    @Override
    public Optional<UserDetails> findByUserName(String userName) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM [User] where UserName = ?",
                new Object[]{userName},
                (rs, rowNum) ->
                        Optional.of(new User(
                                rs.getLong("UserID"),
                                rs.getString("UserName"),
                                rs.getString("Password")
                        ))
        );
    }
}