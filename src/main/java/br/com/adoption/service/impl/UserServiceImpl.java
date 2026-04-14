package br.com.adoption.service.impl;

import br.com.adoption.entity.User;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import br.com.adoption.entity.UserType;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("id"));
    }

    @Override
    public User save(User user) {
        user.setUserType(UserType.COMMON);
        return userRepository.save(user);
    }
}