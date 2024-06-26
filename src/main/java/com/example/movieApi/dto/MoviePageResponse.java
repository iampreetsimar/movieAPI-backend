package com.example.movieApi.dto;

import java.util.List;

public record MoviePageResponse(

        List<MovieDto> moviesDto,

        Integer pageNum,

        Integer pageSize,

        long totalElements,

        int totalPages,

        boolean isLastPage
) { 

}
