package com.example.pokedam.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pokedam.api.PokemonApiResponse;
import com.example.pokedam.repository.GameRepository;

public class DetailViewModel extends AndroidViewModel {

    private GameRepository repository;
    private MutableLiveData<PokemonApiResponse> pokemonDetail = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = new GameRepository(application);
    }

    public LiveData<PokemonApiResponse> getPokemonDetail() { return pokemonDetail; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadPokemonData(int apiId) {
        isLoading.setValue(true);
        repository.getPokemonDetail(apiId, new GameRepository.ApiCallback() {
            @Override
            public void onSuccess(PokemonApiResponse pokemon) {
                isLoading.setValue(false);
                pokemonDetail.setValue(pokemon);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                error.setValue(message);
            }
        });
    }
}