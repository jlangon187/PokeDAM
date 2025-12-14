package com.example.pokedam;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pokedam.database.AppDatabase; // Importamos tu base de datos
import com.example.pokedam.model.PokemonEntity;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private int pokemonId;
    private AppDatabase db;

    // UI Components
    private TextView tvNumber, tvName, tvType, tvPower, tvWeight, tvHeight;
    private ImageView ivImage;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Obtener ID
        pokemonId = getIntent().getIntExtra("POKEMON_API_ID", -1);
        if (pokemonId == -1) {
            Toast.makeText(this, "Error al cargar Pokémon", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        // 2. Cargar datos
        db = AppDatabase.getDatabase(this);

        loadPokemonData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvNumber = findViewById(R.id.tvDetailNumber);
        tvName = findViewById(R.id.tvDetailName);
        tvType = findViewById(R.id.tvDetailType);
        tvPower = findViewById(R.id.tvDetailPower);
        tvWeight = findViewById(R.id.tvDetailWeight);
        tvHeight = findViewById(R.id.tvDetailHeight);
        ivImage = findViewById(R.id.ivDetailImage);
        btnBack = findViewById(R.id.btnBackDetail);
    }

    private void loadPokemonData() {
        // Hilo secundario para consultar la BD
        new Thread(() -> {
            // Usamos gameDao() y el método getPokemonByApiId
            PokemonEntity pokemon = db.gameDao().getPokemonByApiId(pokemonId);

            // Volvemos al hilo principal para pintar la pantalla
            runOnUiThread(() -> {
                if (pokemon != null) {
                    displayData(pokemon);
                } else {
                    Toast.makeText(this, "Pokémon no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void displayData(PokemonEntity pokemon) {
        tvNumber.setText(String.format(Locale.getDefault(), "#%03d", pokemon.apiId));
        tvName.setText(pokemon.name.toUpperCase());
        tvPower.setText(String.valueOf(pokemon.baseStat));
        tvWeight.setText(pokemon.weightKg + " KG");
        tvHeight.setText(pokemon.heightM + " M");

        Glide.with(this)
                .load(pokemon.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .fitCenter()
                .into(ivImage);

        // Lógica de colores y traducción
        String englishType = pokemon.type != null ? pokemon.type : "normal";
        tvType.setText(getSpanishType(englishType));

        int color = getTypeColor(englishType);
        tvType.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    // --- DICCIONARIOS DE TIPO Y COLOR ---

    private String getSpanishType(String englishType) {
        switch (englishType.toLowerCase()) {
            case "fire": return "FUEGO";
            case "water": return "AGUA";
            case "grass": return "PLANTA";
            case "electric": return "ELÉCTRICO";
            case "ice": return "HIELO";
            case "fighting": return "LUCHA";
            case "poison": return "VENENO";
            case "ground": return "TIERRA";
            case "flying": return "VOLADOR";
            case "psychic": return "PSÍQUICO";
            case "bug": return "BICHO";
            case "rock": return "ROCA";
            case "ghost": return "FANTASMA";
            case "dragon": return "DRAGÓN";
            case "steel": return "ACERO";
            case "fairy": return "HADA";
            case "dark": return "SINIESTRO";
            default: return "NORMAL";
        }
    }

    private int getTypeColor(String typeName) {
        switch (typeName.toLowerCase()) {
            case "fire": return Color.parseColor("#F08030");
            case "water": return Color.parseColor("#6890F0");
            case "grass": return Color.parseColor("#78C850");
            case "electric": return Color.parseColor("#F8D030");
            case "ice": return Color.parseColor("#98D8D8");
            case "fighting": return Color.parseColor("#C03028");
            case "poison": return Color.parseColor("#A040A0");
            case "ground": return Color.parseColor("#E0C068");
            case "flying": return Color.parseColor("#A890F0");
            case "psychic": return Color.parseColor("#F85888");
            case "bug": return Color.parseColor("#A8B820");
            case "rock": return Color.parseColor("#B8A038");
            case "ghost": return Color.parseColor("#705898");
            case "dragon": return Color.parseColor("#7038F8");
            case "steel": return Color.parseColor("#B8B8D0");
            case "fairy": return Color.parseColor("#EE99AC");
            case "dark": return Color.parseColor("#705848");
            default: return Color.parseColor("#A8A878");
        }
    }
}