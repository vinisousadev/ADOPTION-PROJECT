package br.com.adoption.service;

import br.com.adoption.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User save(User user);
}