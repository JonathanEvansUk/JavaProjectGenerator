package com.evans.repository;

import org.springframework.stereotype.Repository;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

import com.evans.repository.User;
import java.util.UUID;

//@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

  List<User> findAll();
}