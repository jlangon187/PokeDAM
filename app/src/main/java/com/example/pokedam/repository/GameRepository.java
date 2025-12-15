package com.example.pokedam.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.example.pokedam.callbacks.LoginCallback;
import com.example.pokedam.database.AppDatabase;
import com.example.pokedam.database.GameDao;
import com.example.pokedam.model.User;
import com.example.pokedam.model.PokemonEntity;
import com.example.pokedam.api.PokeApiClient;
import com.example.pokedam.api.PokemonApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class GameRepository {

    private GameDao gameDao;
    private SharedPreferences prefs;

    public GameRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        gameDao = db.gameDao();
        prefs = application.getSharedPreferences("poke_dam_prefs", Context.MODE_PRIVATE);
    }

    // --- MÉTODOS PARA EL POKÉMON ACTIVO ---

    // 1. ¿Hay un Pokémon esperando? (Devuelve -1 si no hay ninguno)
    public int getSavedWildId() {
        return prefs.getInt("ACTIVE_WILD_ID", -1);
    }

    // 2. Guardar el ID del Pokémon que acaba de salir
    public void saveWildId(int id) {
        prefs.edit().putInt("ACTIVE_WILD_ID", id).apply();
    }

    // 3. Borrar el ID (Cuando se captura o se huye)
    public void clearWildId() {
        prefs.edit().remove("ACTIVE_WILD_ID").apply();
    }

    // --- OPERACIONES DE USUARIO ---

    // Metodo para REGISTRO
    public void registerUser(User user, LoginCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Verificamos si existe en segundo plano
            User existing = gameDao.checkUserExists(user.getUsername());

            if (existing != null) {
                // Volvemos al hilo principal para avisar del error
                postResult(() -> callback.onError("El usuario ya existe"));
            } else {
                // 2. Insertamos
                gameDao.insertUser(user);
                postResult(() -> callback.onSuccess(user));
            }
        });
    }

    // Metodo para LOGIN
    public void login(String username, String password, LoginCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = gameDao.login(username, password);
            if (user != null) {
                postResult(() -> callback.onSuccess(user));
            } else {
                postResult(() -> callback.onError("Credenciales incorrectas"));
            }
        });
    }

    // --- OPERACIONES DE POKEMON (Juego) ---

    // Interface para devolver el resultado de la lista de Pokemons
    public interface PokemonListCallback {
        void onResult(List<PokemonEntity> pokemons);
    }

    // Metodo para OBTENER la lista de Pokemons del usuario
    public void getUserPokemons(String username, PokemonListCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<PokemonEntity> lista = gameDao.getUserPokemons(username);
            postResult(() -> callback.onResult(lista));
        });
    }

    // --- UTILIDAD PARA VOLVER AL HILO PRINCIPAL ---
    private void postResult(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    // Interface callback para devolver el resultado de la API a la pantalla
    public interface ApiCallback {
        void onSuccess(PokemonApiResponse pokemon);
        void onError(String error);
    }

    // --- MÉTODO CORREGIDO ---
    public void getPokemonById(int id, ApiCallback callback) {
        // CORRECCIÓN: Usamos PokeApiClient.getService() y el nombre correcto del método .getPokemon(id)
        PokeApiClient.getService().getPokemon(id).enqueue(new Callback<PokemonApiResponse>() {
            @Override
            public void onResponse(Call<PokemonApiResponse> call, Response<PokemonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error API");
                }
            }
            @Override
            public void onFailure(Call<PokemonApiResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Metodo para obtener un Pokemon aleatorio
    public void getRandomPokemon(ApiCallback callback) {
        // Generar ID random entre 1 y 1000
        int randomId = (int) (Math.random() * 898) + 1;

        PokeApiClient.getService().getPokemon(randomId).enqueue(new Callback<PokemonApiResponse>() {
            @Override
            public void onResponse(Call<PokemonApiResponse> call, Response<PokemonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error en la respuesta API");
                }
            }

            @Override
            public void onFailure(Call<PokemonApiResponse> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    // Interface para devolver el resultado de las reglas
    public interface CaptureRulesCallback {
        void onResult(boolean canCapture, String message);
    }

    // Metodo para verificar las reglas de captura
    public void checkCaptureRules(String username, int wildApiId, int wildPower, CaptureRulesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Verificar Duplicados
            int existingCount = gameDao.hasPokemon(username, wildApiId);
            if (existingCount > 0) {
                postResult(() -> callback.onResult(false, "¡Ya tienes este Pokémon!"));
                return;
            }

            // 2. Verificar Inventario Vacío
            int totalPokemons = gameDao.getUserPokemonCount(username);
            if (totalPokemons == 0) {
                postResult(() -> callback.onResult(true, "¡Es tu primer Pokémon! Puedes capturarlo."));
                return;
            }

            // 3. Comparación de Poder
            int myTotalPower = gameDao.getUserTotalPower(username);
            if (myTotalPower > wildPower) {
                postResult(() -> callback.onResult(true, "¡Tu equipo es fuerte! (" + myTotalPower + " vs " + wildPower + ")"));
            } else {
                postResult(() -> callback.onResult(false, "Tu equipo es muy débil (" + myTotalPower + " vs " + wildPower + ")"));
            }
        });
    }

    // Metodo para GUARDAR el Pokémon capturado
    public void capturePokemon(PokemonEntity pokemon, LoginCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            gameDao.insertPokemon(pokemon);
            postResult(() -> callback.onSuccess(null));
        });
    }

    // Metodo para OBTENER los detalles de un Pokémon
    public void getPokemonDetail(int apiId, ApiCallback callback) {
        PokeApiClient.getService().getPokemon(apiId).enqueue(new retrofit2.Callback<PokemonApiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<PokemonApiResponse> call, retrofit2.Response<PokemonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al recuperar detalles");
                }
            }

            @Override
            public void onFailure(Call<PokemonApiResponse> call, Throwable t) {
                if (t instanceof java.io.IOException) {
                    callback.onError("¡No tienes conexión a internet!");
                } else {
                    callback.onError("Error extraño: " + t.getMessage());
                }
            }
        });
    }

    public interface CountCallback {
        void onCountReceived(int count);
    }

    public void checkPokemonCount(String username, CountCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int count = gameDao.getUserPokemonCount(username);

            callback.onCountReceived(count);
        });
    }

    public void deleteUser(User user, LoginCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            gameDao.deleteUser(user);
            // Devolvemos éxito (pasamos null porque el usuario ya no existe)
            postResult(() -> callback.onSuccess(null));
        });
    }

    public interface UserListCallback {
        void onResult(List<User> users);
    }

    public void getAllUsers(UserListCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<User> users = gameDao.getAllUsers();
            postResult(() -> callback.onResult(users));
        });
    }

    public void updateUser(User user, LoginCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            gameDao.updateUser(user);
            postResult(() -> callback.onSuccess(user));
        });
    }

    // Metodo para ACTUALIZAR USUARIO COMPLETO (Con cambio de nombre)
    public void updateUserProfile(String oldName, String newName, String newPass, LoginCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Comprobar si el nuevo nombre ya existe (si es diferente al actual)
            if (!oldName.equals(newName)) {
                User exists = gameDao.checkUserExists(newName);
                if (exists != null) {
                    postResult(() -> callback.onError("¡Ese nombre de usuario ya existe!"));
                    return;
                }
            }

            try {
                gameDao.updateProfile(oldName, newName, newPass);
                gameDao.updatePokemonOwner(oldName, newName);

                // Devolvemos un objeto usuario con los datos nuevos
                User updatedUser = new User(newName, newPass);
                postResult(() -> callback.onSuccess(updatedUser));

            } catch (Exception e) {
                postResult(() -> callback.onError("Error al actualizar: " + e.getMessage()));
            }
        });
    }
}