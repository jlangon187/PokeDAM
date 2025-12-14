package com.example.pokedam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pokedam.model.User;
import com.example.pokedam.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private EditText etUser, etPass;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MusicManager.start(this);

        // 1. Inicializar Vistas
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // 2. Inicializar ViewModel (Conexión MVVM)
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 3. OBSERVAR cambios (Aquí ocurre la magia)

        // Si el login/registro es exitoso:
        viewModel.getUserSuccess().observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Bienvenido " + user.getUsername(), Toast.LENGTH_SHORT).show();
                irAListado(user.getUsername());
            }
        });

        // Si hay error:
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Configurar Botones
        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();
            viewModel.login(user, pass);
        });

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();
            viewModel.register(user, pass);
        });
    }

    private void irAListado(String username) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }
}