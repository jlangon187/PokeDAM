package com.example.pokedam.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokedam.model.PokemonEntity;
import com.example.pokedam.model.User;

import java.util.List;

@Dao
public interface GameDao {

    // --- ZONA USUARIOS ---

    @Insert
    void insertUser(User user);

    // Para el Login: Busca si existe esa combinación usuario/pass
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username")
    User checkUserExists(String username);


    // --- ZONA POKEMONS ---

    @Insert
    void insertPokemon(PokemonEntity pokemon);

    @Query("SELECT * FROM pokemons WHERE owner = :username")
    List<PokemonEntity> getUserPokemons(String username);

    @Query("SELECT SUM(baseStat) FROM pokemons WHERE owner = :username")
    int getUserTotalPower(String username);

     @Query("SELECT COUNT(*) FROM pokemons WHERE owner = :username")
    int getUserPokemonCount(String username);

    @Query("SELECT COUNT(*) FROM pokemons WHERE owner = :username AND apiId = :pokeApiId")
    int hasPokemon(String username, int pokeApiId);

    @Query("SELECT * FROM pokemons WHERE apiId = :apiId LIMIT 1")
    PokemonEntity getPokemonByApiId(int apiId);
}