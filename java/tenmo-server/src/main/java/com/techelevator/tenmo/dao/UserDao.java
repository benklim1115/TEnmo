package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAllWithHash();

    public List<User> findAllWithoutHash(int userId);

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
}
