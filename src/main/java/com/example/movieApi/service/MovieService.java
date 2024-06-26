package com.example.movieApi.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.movieApi.dto.MovieDto;
import com.example.movieApi.dto.MoviePageResponse;

public interface MovieService {
    
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Integer movieId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Integer movieId) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNum, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNum, Integer pageSize, 
                                                           String sortBy, String sortDir);
}
