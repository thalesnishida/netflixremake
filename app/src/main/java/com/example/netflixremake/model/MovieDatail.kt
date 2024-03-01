package com.example.netflixremake.model

data class MovieDetail(
    val movie: Movie,
    val similarMovie: List<Movie>
)
