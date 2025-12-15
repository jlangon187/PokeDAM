package com.example.pokedam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedam.adapter.UserAdapter;
import com.example.pokedam.callbacks.LoginCallback;
import com.example.pokedam.model.User;
import com.example.pokedam.repository.GameRepository;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private GameRepository repository;
    private String currentUsername; // Para saber quién soy yo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        // Recuperamos quién está logueado actualmente (pasado desde el menú anterior)
        // IMPORTANTE: Asegúrate de pasar "USERNAME" en el Intent al abrir esta pantalla desde MainMenu
        currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) currentUsername = "";

        findViewById(android.R.id.content).setBackgroundColor(0xFFEEEEEE);

        // Botón Volver
        findViewById(R.id.btnBackFromList).setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewPokemons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = new GameRepository(getApplication());

        adapter = new UserAdapter(new ArrayList<>(), new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                showEditDialog(user);
            }

            @Override
            public void onDelete(User user) {
                showDeleteConfirmation(user);
            }
        });

        recyclerView.setAdapter(adapter);
        loadUsers();
    }

    private void loadUsers() {
        repository.getAllUsers(users -> adapter.setUsers(users));
    }

    // --- 1. EDICIÓN AVANZADA (NOMBRE Y PASS) ---
    private void showEditDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        builder.setView(view);

        android.widget.TextView title = view.findViewById(R.id.tvDialogTitle);
        com.google.android.material.textfield.TextInputEditText etUser = view.findViewById(R.id.etEditUser);
        com.google.android.material.textfield.TextInputEditText etPass = view.findViewById(R.id.etEditPassword);

        title.setText("Editar a: " + user.getUsername());
        etUser.setText(user.getUsername());
        etPass.setText(user.getPassword());

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = etUser.getText().toString().trim();
            String newPass = etPass.getText().toString().trim();

            if (!newName.isEmpty() && !newPass.isEmpty()) {
                String oldName = user.getUsername();

                updateFullUser(oldName, newName, newPass);
            } else {
                Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialog.show();
    }
    private void updateFullUser(String oldName, String newName, String newPass) {
        repository.updateUserProfile(oldName, newName, newPass, new LoginCallback() {
            @Override
            public void onSuccess(User u) {
                Toast.makeText(UserListActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();

                // SI CAMBIÉ MI PROPIO NOMBRE: Actualizar la variable local
                if (oldName.equals(currentUsername)) {
                    currentUsername = newName;
                }
                loadUsers();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(UserListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- 2. BORRADO CON LOGOUT ---
    private void showDeleteConfirmation(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Eliminar a " + user.getUsername() + "?\nSe perderán todos sus Pokémon.")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("ELIMINAR", (dialog, which) -> {
                    deleteUserFromDb(user);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteUserFromDb(User user) {
        // Verificar si me estoy borrando a mí mismo
        boolean isSelfDelete = user.getUsername().equals(currentUsername);

        repository.deleteUser(user, new LoginCallback() {
            @Override
            public void onSuccess(User u) {
                if (isSelfDelete) {
                    // CASO ESPECIAL: ME HE BORRADO
                    forceLogout();
                } else {
                    // Caso normal: He borrado a otro
                    Toast.makeText(UserListActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    loadUsers();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(UserListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LÓGICA DE LOGOUT FORZOSO ---
    private void forceLogout() {
        Toast.makeText(this, "Tu cuenta ha sido eliminada. Cerrando sesión...", Toast.LENGTH_LONG).show();

        // 1. Crear Intent para ir al Login
        Intent intent = new Intent(UserListActivity.this, LoginActivity.class);

        // 2. Limpiar la pila de actividades (Borra todo el historial de pantallas)
        // Esto evita que el usuario pueda dar "Atrás" y volver al menú
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // 3. Iniciar
        startActivity(intent);
        finish();
    }
}