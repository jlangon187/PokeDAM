package com.example.pokedam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        com.google.android.material.floatingactionbutton.FloatingActionButton btnAdmin = findViewById(R.id.btnAdminSettings);

        // 1. Recibir el usuario
        currentUsername = getIntent().getStringExtra("USERNAME");

        // 2. Configurar el saludo
        TextView tvWelcome = findViewById(R.id.tvWelcomeUser);
        if (currentUsername != null) {
            tvWelcome.setText("Entrenador: " + currentUsername.toUpperCase());
        }

        // 3. Botón Inventario
        Button btnInventory = findViewById(R.id.btnMyPokemons);
        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, PokemonListActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        // 4. Botón Buscar (Zona Salvaje)
        Button btnSearch = findViewById(R.id.btnSearchWild);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CaptureActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, UserListActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });
    }
}