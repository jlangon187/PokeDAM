package com.example.pokedam.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokedam.R;
import com.example.pokedam.model.PokemonEntity;

import java.util.List;
import java.util.Locale;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private List<PokemonEntity> pokemonList;
    private Context context;

    public PokemonAdapter(Context context, List<PokemonEntity> pokemonList) {
        this.context = context;
        this.pokemonList = pokemonList;
    }

    public void setPokemonList(List<PokemonEntity> list) {
        this.pokemonList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        PokemonEntity pokemon = pokemonList.get(position);

        // 1. Número formateado (#025)
        // "%03d" significa: número decimal con 3 dígitos, rellenando con ceros a la izquierda
        holder.tvNumber.setText(String.format(Locale.getDefault(), "#%03d", pokemon.apiId));

        // 2. Nombre y Poder
        holder.tvName.setText(pokemon.name.toUpperCase());
        holder.tvPower.setText("Poder: " + pokemon.baseStat);

        // 3. Imagen HD
        Glide.with(context)
                .load(pokemon.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .fitCenter()
                .into(holder.ivImage);

        // 4. LÓGICA DEL TIPO (Traducción + Color)
        String englishType = pokemon.type != null ? pokemon.type : "normal";

        // A) Texto en Español
        holder.tvType.setText(getSpanishType(englishType));

        // B) Color (usamos el inglés para buscar el color)
        int colorHex = getTypeColor(englishType);
        holder.tvType.setBackgroundTintList(ColorStateList.valueOf(colorHex));

        // Click
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.pokedam.DetailActivity.class);
            intent.putExtra("POKEMON_API_ID", pokemon.apiId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList != null ? pokemonList.size() : 0;
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPower, tvType, tvNumber;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivPokemonImage);
            tvName = itemView.findViewById(R.id.tvPokemonName);
            tvPower = itemView.findViewById(R.id.tvPokemonPower);
            tvType = itemView.findViewById(R.id.tvPokemonType);
            tvNumber = itemView.findViewById(R.id.tvPokemonNumber);
        }
    }

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
            case "normal":
            default: return Color.parseColor("#A8A878");
        }
    }
}