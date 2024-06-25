package com.example.movieApi.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer movieId;

    @NotBlank(message = "Please provide the movie's title!")
    private String title;

    @NotBlank(message = "Please provide the movie's directior!")
    private String director;

    @NotBlank(message = "Please provide the movie's studio!")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Please provide the movie's poster!")
    private String poster;

    @NotBlank(message = "Please provide the movie poster's url!")
    private String posterUrl;
}
