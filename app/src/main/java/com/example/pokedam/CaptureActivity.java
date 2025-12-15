package com.example.pokedam;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.pokedam.viewmodel.CaptureViewModel;
import com.google.android.material.button.MaterialButton;

public class CaptureActivity extends AppCompatActivity {

    private String currentUsername;
    private CaptureViewModel viewModel;

    // UI Components
    private ImageView ivPokemon;
    private TextView tvName, tvPower, tvStatus, tvSuccessOverlay;
    private MaterialButton btnCapture, btnEscape;
    private LinearLayout mainLayout;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // 1. Recibir usuario
        currentUsername = getIntent().getStringExtra("USERNAME");

        // 2. Inicializar Vistas
        ivPokemon = findViewById(R.id.ivWildPokemon);
        tvName = findViewById(R.id.tvWildName);
        tvPower = findViewById(R.id.tvWildPower);
        tvStatus = findViewById(R.id.tvStatusMessage);
        btnCapture = findViewById(R.id.btnCapture);
        btnEscape = findViewById(R.id.btnEscape);
        mainLayout = findViewById(R.id.mainLayout);
        videoView = findViewById(R.id.videoViewCapture);
        tvSuccessOverlay = findViewById(R.id.tvSuccessOverlay);

        // 3. Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(CaptureViewModel.class);

        // --- OBSERVADORES ---

        // A) Datos del Pokémon
        viewModel.getWildPokemon().observe(this, pokemon -> {
            if (pokemon != null) {
                tvName.setText(pokemon.getName());
                int power = 0;
                if(pokemon.getStats() != null && !pokemon.getStats().isEmpty()){
                    power = pokemon.getStats().get(0).getBaseStat();
                }
                tvPower.setText("Poder: " + power);

                // Carga de imagen HD
                Glide.with(this)
                        .load(pokemon.getSprites().getImage())
                        .placeholder(android.R.drawable.ic_menu_help)
                        .error(android.R.drawable.ic_delete)
                        .into(ivPokemon);
            }
        });

        // B) ¿Se puede capturar?
        viewModel.getCanCapture().observe(this, allowed -> {
            btnCapture.setEnabled(allowed);
            if (allowed) {
                btnCapture.setText("¡LANZAR POKÉBALL!");
                btnCapture.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CC0000"))); // Rojo
            } else {
                btnCapture.setText("NIVEL INSUFICIENTE");
                btnCapture.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
        });

        // C) ¿Se puede huir?
        viewModel.getCanEscape().observe(this, canEscape -> {
            if (!canEscape) {
                // Es el primer Pokémon: Bloqueamos huida
                btnEscape.setEnabled(false);
                btnEscape.setText("¡NO PUEDES HUIR!");
                btnEscape.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            } else {
                // Ya tiene equipo: Puede huir
                btnEscape.setEnabled(true);
                btnEscape.setText("HUIR");
                btnEscape.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3B4CCA"))); // Azul
            }
        });

        // D) Mensajes de estado
        viewModel.getStatusMessage().observe(this, msg -> {
            tvStatus.setText(msg);
        });

        // E) Éxito en la captura
        viewModel.getCaptureSuccess().observe(this, success -> {
            if (success) {
                tvSuccessOverlay.setVisibility(View.VISIBLE);

                // Esperar 2 segundos para leer el mensaje y luego cambiar de pantalla
                new Handler().postDelayed(() -> {

                    // 1. Preparamos el viaje al Inventario
                    Intent intent = new Intent(CaptureActivity.this, PokemonListActivity.class);

                    // 2. ¡IMPORTANTE! Hay que pasar el usuario para que cargue la lista correcta
                    intent.putExtra("USERNAME", currentUsername);

                    // 3. Evita que se acumulen pantallas si ya venías de la lista
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // 4. Iniciamos y cerramos la captura
                    startActivity(intent);
                    finish();

                }, 2000);
            }
        });

        // --- BOTONES ---

        btnCapture.setOnClickListener(v -> launchCaptureVideo());

        btnEscape.setOnClickListener(v -> {
            // IMPORTANTE: Avisar al ViewModel de que huimos para borrar el ID
            viewModel.runAway();
            MusicManager.restoreVolume();
            finish();
        });

        // --- INICIO ---
        // 1. Verificar si es el primer Pokémon
        viewModel.checkFirstTime(currentUsername);

        // 2. Iniciar encuentro (Cargar guardado o buscar nuevo)
        // CAMBIO: Usamos startEncounter en vez de searchRandomPokemon
        viewModel.startEncounter(currentUsername);
    }

    private void launchCaptureVideo() {
        btnCapture.setEnabled(false);
        btnEscape.setEnabled(false);
        mainLayout.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        MusicManager.lowerVolume();

        String path = "android.resource://" + getPackageName() + "/" + R.raw.capture_anim;
        videoView.setVideoURI(Uri.parse(path));

        videoView.setOnPreparedListener(mp -> mp.start());

        videoView.setOnCompletionListener(mp -> {
            // 4. Al terminar: Restaurar música y guardar en BD
            MusicManager.restoreVolume();
            viewModel.captureCurrentPokemon(currentUsername);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.restoreVolume();
    }
}