package com.example.pokedam.callbacks;

import com.example.pokedam.model.User;

public interface LoginCallback {
    void onSuccess(User user);
    void onError(String message);
}
