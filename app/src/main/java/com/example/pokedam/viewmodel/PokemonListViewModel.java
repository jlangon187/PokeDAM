package com.example.pokedam.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pokedam.model.PokemonEntity;
import com.example.pokedam.repository.GameRepository;

import java.util.List;

public class PokemonListViewModel extends AndroidViewModel {

    private GameRepository repository;
    private MutableLiveData<List<PokemonEntity>> pokemonList = new MutableLiveData<>();

    public PokemonListViewModel(@NonNull Application application) {
        super(application);
        repository = new GameRepository(application);
    }

    public LiveData<List<PokemonEntity>> getPokemonList() {
        return pokemonList;
    }

    public void loadPokemons(String username) {
        repository.getUserPokemons(username, new GameRepository.PokemonListCallback() {
            @Override
            public void onResult(List<PokemonEntity> pokemons) {
                pokemonList.setValue(pokemons);
            }
        });
    }
}