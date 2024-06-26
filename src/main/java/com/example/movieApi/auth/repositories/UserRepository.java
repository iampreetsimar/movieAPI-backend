package com.example.movieApi.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movieApi.auth.entities.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
