package com.example.movieApi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movieApi.entities.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

}
