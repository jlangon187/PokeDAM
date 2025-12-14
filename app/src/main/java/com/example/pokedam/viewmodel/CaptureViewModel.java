package com.example.pokedam.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pokedam.api.PokemonApiResponse;
import com.example.pokedam.callbacks.LoginCallback;
import com.example.pokedam.model.PokemonEntity;
import com.example.pokedam.model.User;
import com.example.pokedam.repository.GameRepository;

public class CaptureViewModel extends AndroidViewModel {

    private GameRepository repository;

    // LiveData para la UI
    private MutableLiveData<PokemonApiResponse> wildPokemon = new MutableLiveData<>();
    private MutableLiveData<Boolean> canCapture = new MutableLiveData<>();
    private MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> captureSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> canEscape = new MutableLiveData<>(true);

    public CaptureViewModel(@NonNull Application application) {
        super(application);
        repository = new GameRepository(application);
    }

    // Getters
    public LiveData<PokemonApiResponse> getWildPokemon() { return wildPokemon; }
    public LiveData<Boolean> getCanCapture() { return canCapture; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> getCaptureSuccess() { return captureSuccess; }
    public LiveData<Boolean> getCanEscape() { return canEscape; }

    // --- MÉTODOS DE LÓGICA ---

    // 1. INICIAR ENCUENTRO (Persistencia)
    // Sustituye a searchRandomPokemon
    public void startEncounter(String username) {
        statusMessage.setValue("Rastreando zona...");

        // A) Miramos si hay uno pendiente en memoria
        int savedId = repository.getSavedWildId();

        if (savedId != -1) {
            // CASO 1: YA HABÍA UNO (El usuario volvió atrás y regresó)
            statusMessage.setValue("¡El Pokémon sigue aquí!");
            repository.getPokemonById(savedId, new GameRepository.ApiCallback() {
                @Override
                public void onSuccess(PokemonApiResponse pokemon) {
                    processPokemonResponse(pokemon, username);
                }
                @Override
                public void onError(String error) {
                    statusMessage.setValue("Error recuperando: " + error);
                }
            });

        } else {
            // CASO 2: NO HAY NADIE, GENERAMOS UNO NUEVO
            repository.getRandomPokemon(new GameRepository.ApiCallback() {
                @Override
                public void onSuccess(PokemonApiResponse pokemon) {
                    // ¡IMPORTANTE! Guardamos este ID para que no se pierda si sale
                    repository.saveWildId(pokemon.getId());

                    processPokemonResponse(pokemon, username);
                }
                @Override
                public void onError(String error) {
                    statusMessage.setValue("Error API: " + error);
                }
            });
        }
    }

    // Auxiliar para procesar la respuesta (sea nueva o cargada de memoria)
    private void processPokemonResponse(PokemonApiResponse pokemon, String username) {
        wildPokemon.setValue(pokemon);
        int power = 0;
        if (pokemon.getStats() != null && !pokemon.getStats().isEmpty()) {
            power = pokemon.getStats().get(0).getBaseStat();
        }
        checkRules(username, pokemon.getId(), power);
    }

    // 2. VERIFICAR REGLAS (BD)
    private void checkRules(String username, int apiId, int power) {
        repository.checkCaptureRules(username, apiId, power, (allowed, reason) -> {
            canCapture.setValue(allowed);
            statusMessage.setValue(reason);
        });
    }

    // 3. CAPTURAR (BD)
    public void captureCurrentPokemon(String username) {
        PokemonApiResponse apiData = wildPokemon.getValue();
        if (apiData == null) return;

        int power = 0;
        if (apiData.getStats() != null && !apiData.getStats().isEmpty()) {
            power = apiData.getStats().get(0).getBaseStat();
        }

        // Cálculos de Peso y Altura
        double weightKg = apiData.getWeight() / 10.0;
        double heightM = apiData.getHeight() / 10.0;

        // Crear la entidad para Room
        PokemonEntity entity = new PokemonEntity(
                apiData.getId(),
                apiData.getName(),
                power,
                apiData.getSprites().getImage(),
                username,
                apiData.getFirstType(),
                weightKg,
                heightM
        );

        repository.capturePokemon(entity, new LoginCallback() {
            @Override
            public void onSuccess(User user) {
                // ÉXITO: YA NO NECESITAMOS GUARDARLO, LO BORRAMOS DE PREFERENCIAS
                repository.clearWildId();
                captureSuccess.setValue(true);
            }
            @Override
            public void onError(String message) {
                statusMessage.setValue("Error al guardar: " + message);
            }
        });
    }

    // 4. VERIFICAR PRIMERA VEZ (Para bloquear botón huir)
    public void checkFirstTime(String username) {
        repository.checkPokemonCount(username, count -> {
            if (count == 0) {
                // Si tiene 0, NO puede escapar
                canEscape.postValue(false);
            } else {
                // Si ya tiene alguno, SÍ puede escapar
                canEscape.postValue(true);
            }
        });
    }

    // 5. HUIR (Borrar ID explícitamente)
    public void runAway() {
        // Si huye voluntariamente, limpiamos el ID para que salga uno nuevo la próxima vez
        repository.clearWildId();
    }
}