package com.example.movieApi.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.movieApi.dto.MovieDto;
import com.example.movieApi.entities.Movie;
import com.example.movieApi.repositories.MovieRepository;


@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new RuntimeException("File already exists! Please enter another filename.");
        }

        // 1. upload file
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. set value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);

        // 3. map dto to movie object
        Movie movie = new Movie(
                    null,
                    movieDto.getTitle(),
                    movieDto.getDirector(),
                    movieDto.getStudio(),
                    movieDto.getMovieCast(),
                    movieDto.getReleaseYear(),
                    movieDto.getPoster()
        );

        // 4. save movie object -> saved movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. generate poster url
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        // 6. map movie object to dto object and return it
        MovieDto response = new MovieDto(
                    savedMovie.getMovieId(),
                    savedMovie.getTitle(),
                    savedMovie.getDirector(),
                    savedMovie.getStudio(),
                    savedMovie.getMovieCast(),
                    savedMovie.getReleaseYear(),
                    savedMovie.getPoster(),
                    posterUrl
        );

        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. check the data in db and if exists, return movie with given id
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found!"));

        // 2. generate poster url
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 3. map to movieDto object and return it
        MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. fetch all data from db
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> moviesDto = new ArrayList<>();

        // 2. iterate through the list, generate posterUrl for all and map to movieDto
        for(Movie movie: movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();

            MovieDto movieDto = new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPoster(),
                        posterUrl
            );

            moviesDto.add(movieDto);
        }

        return moviesDto;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. check if movie obj exists with given id
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found!"));

        // 2. if file is null, do nothing
        //    if file is not null, delete existing file associated with record and upload new file
        String fileName = movie.getPoster();
        if(file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }
        
        // 3. set movieDto's poster value
        movieDto.setPoster(fileName);

        // 4. map to movie object
        Movie newMovieObj = new Movie(
                    movie.getMovieId(),
                    movieDto.getTitle(),
                    movieDto.getDirector(),
                    movieDto.getStudio(),
                    movieDto.getMovieCast(),
                    movieDto.getReleaseYear(),
                    movieDto.getPoster()
        );

        // 5. save movie object -> return saved movie object
        Movie updatedMovie = movieRepository.save(newMovieObj);

        // 6. generate posterUrl
        String posterUrl = baseUrl + "/file/" + fileName;

        // 7. map to movieDto and return it
        MovieDto updatedMovieDto = new MovieDto(
                        updatedMovie.getMovieId(),
                        updatedMovie.getTitle(),
                        updatedMovie.getDirector(),
                        updatedMovie.getStudio(),
                        updatedMovie.getMovieCast(),
                        updatedMovie.getReleaseYear(),
                        updatedMovie.getPoster(),
                        posterUrl
            );

        return updatedMovieDto;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. check if movie exists in db
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found!"));

        // 2. delete the file associated with movie object
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        // 3. delete movie object
        movieRepository.delete(movie);

        return "Movie deleted with id: " + movie.getMovieId();
    }


}
