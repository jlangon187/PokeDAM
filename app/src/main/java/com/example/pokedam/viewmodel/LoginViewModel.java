package com.example.pokedam.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pokedam.model.User;
import com.example.pokedam.repository.GameRepository;
import com.example.pokedam.callbacks.LoginCallback;

public class LoginViewModel extends AndroidViewModel {

    private GameRepository repository;

    // LiveData: Son "cajas" de datos observables. La Activity mirará estas cajas.
    private MutableLiveData<User> userSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new GameRepository(application);
    }

    // --- Getters para que la Activity pueda observar ---
    public LiveData<User> getUserSuccess() {
        return userSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // --- Acciones (Lógica de Negocio) ---

    public void login(String username, String password) {
        // Validaciones básicas antes de molestar a la BD
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Por favor, rellena todos los campos");
            return;
        }

        repository.login(username, password, new LoginCallback() {
            @Override
            public void onSuccess(User user) {
                // ¡Éxito! Ponemos el usuario en la caja "userSuccess"
                userSuccess.setValue(user);
            }

            @Override
            public void onError(String message) {
                // Error. Ponemos el mensaje en la caja "errorMessage"
                errorMessage.setValue(message);
            }
        });
    }

    public void register(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Usuario y contraseña son obligatorios");
            return;
        }

        User newUser = new User(username, password);

        repository.registerUser(newUser, new LoginCallback() {
            @Override
            public void onSuccess(User user) {
                userSuccess.setValue(user); // Al registrarse, entra automáticamente
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }
}