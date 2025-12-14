package com.example.pokedam.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index; // Importar Index
import androidx.room.Ignore; // Importar Ignore
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

// AÑADIDO: indices = {@Index("owner")} para quitar el aviso de la Foreign Key
@Entity(tableName = "pokemons",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "username",
                childColumns = "owner",
                onDelete = CASCADE),
        indices = {@Index("owner")})
public class PokemonEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int apiId;
    public String name;
    public int baseStat;
    public String imageUrl;
    public String owner;
    public String type;

    public double weightKg;
    public double heightM;

    public PokemonEntity() {
    }

    @Ignore
    public PokemonEntity(int apiId, String name, int baseStat, String imageUrl, String owner, String type, double weightKg, double heightM) {
        this.apiId = apiId;
        this.name = name;
        this.baseStat = baseStat;
        this.imageUrl = imageUrl;
        this.owner = owner;
        this.type = type;
        this.weightKg = weightKg;
        this.heightM = heightM;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightM() {
        return heightM;
    }

    public void setHeightM(double heightM) {
        this.heightM = heightM;
    }

    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApiId() { return apiId; }
    public void setApiId(int apiId) { this.apiId = apiId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getBaseStat() { return baseStat; }
    public void setBaseStat(int baseStat) { this.baseStat = baseStat; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
}