package com.example.pokedam.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApiService {

    // Pide un pokemon por su ID (ej: /pokemon/25 para Pikachu)
    @GET("pokemon/{id}")
    Call<PokemonApiResponse> getPokemon(@Path("id") int id);
}