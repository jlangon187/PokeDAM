package com.example.pokedam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Asegúrate de tener este import
import android.widget.Button;
// import android.widget.LinearLayout; <-- Ya no necesitas esto obligatoriamente para emptyState

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedam.adapter.PokemonAdapter;
import com.example.pokedam.viewmodel.PokemonListViewModel;

import java.util.ArrayList;

public class PokemonListActivity extends AppCompatActivity {

    private PokemonListViewModel viewModel;
    private RecyclerView recyclerView;
    private PokemonAdapter adapter;

    // --- ERROR ESTABA AQUÍ ---
    // private LinearLayout emptyState;  <-- MAL (Tu XML ahora tiene un CardView)
    private View emptyState;          // <-- BIEN (View sirve para todo)
    // -------------------------

    private Button btnSearch;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        currentUsername = getIntent().getStringExtra("USERNAME");
        if(currentUsername == null) currentUsername = "invitado";

        // Inicializar Vistas
        recyclerView = findViewById(R.id.recyclerViewPokemons);

        // Al usar 'View' arriba, esta línea ya no dará error de ClassCastException
        emptyState = findViewById(R.id.layoutEmptyState);

        btnSearch = findViewById(R.id.btnSearchPokemon);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PokemonAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PokemonListViewModel.class);

        viewModel.getPokemonList().observe(this, pokemons -> {
            if (pokemons == null || pokemons.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
                adapter.setPokemonList(pokemons);
            }
        });

        View btnBack = findViewById(R.id.btnBackFromList);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(PokemonListActivity.this, CaptureActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadPokemons(currentUsername);
    }
}