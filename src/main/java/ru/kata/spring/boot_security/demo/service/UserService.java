package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entityes.User;

import java.util.List;

public interface UserService {
    void save(User user);

    User findById(Long userId);

    List<User> findAll();

    void update(User user);

    void delete(Long userId);


}