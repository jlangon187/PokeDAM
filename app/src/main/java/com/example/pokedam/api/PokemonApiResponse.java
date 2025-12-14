package com.example.pokedam.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonApiResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("sprites")
    private Sprites sprites;

    @SerializedName("types")
    private List<TypeSlot> types;

    @SerializedName("stats")
    private List<StatEntry> stats;

    @SerializedName("weight")
    private int weight;

    @SerializedName("height")
    private int height;

    // --- GETTERS ---
    public int getId() { return id; }
    public String getName() { return name; }
    public Sprites getSprites() { return sprites; }
    public List<TypeSlot> getTypes() { return types; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    public void setTypes(List<TypeSlot> types) {
        this.types = types;
    }

    public void setStats(List<StatEntry> stats) {
        this.stats = stats;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<StatEntry> getStats() { return stats; }

    // --- CLASES INTERNAS ---

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;

        @SerializedName("other")
        private Other other;

        public String getFrontDefault() { return frontDefault; }

        public String getImage() {
            if (other != null && other.officialArtwork != null && other.officialArtwork.frontDefault != null) {
                return other.officialArtwork.frontDefault;
            }
            return frontDefault;
        }
    }

    public static class Other {
        @SerializedName("official-artwork") // Ojo al guion
        private OfficialArtwork officialArtwork;
    }

    public static class OfficialArtwork {
        @SerializedName("front_default")
        public String frontDefault;
    }

    public static class TypeSlot {
        @SerializedName("type")
        private TypeInfo type;
        public TypeInfo getType() { return type; }
    }

    public static class TypeInfo {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }

    public String getFirstType() {
        if (types != null && !types.isEmpty() && types.get(0).getType() != null) {
            return types.get(0).getType().getName();
        }
        return "unknown";
    }

    public static class StatEntry {
        @SerializedName("base_stat")
        private int baseStat;
        @SerializedName("stat")
        private StatInfo stat;
        public int getBaseStat() { return baseStat; }
        public StatInfo getStat() { return stat; }
    }

    public static class StatInfo {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }
}