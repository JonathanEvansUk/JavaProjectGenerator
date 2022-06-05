package com.evans.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.evans.repository.User;
import com.evans.repository.UserRepository;
import java.util.UUID;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public Optional<User> delete(UUID id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      userRepository.delete(user.get());
    }

    return user;
  }
}
