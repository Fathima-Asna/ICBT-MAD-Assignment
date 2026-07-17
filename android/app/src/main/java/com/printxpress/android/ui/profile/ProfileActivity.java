package com.printxpress.android.ui.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.printxpress.android.R;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.User;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;
import com.printxpress.android.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etName, etPhone, etAddress, etRole;
    private Button btnSaveProfile;
    private SupabaseDataApi dataApi;
    private User currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etRole = findViewById(R.id.etRole);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        dataApi = SupabaseClient.getDataApi();

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        AuthUser authUser = SessionManager.getInstance().getCurrentUser();
        if (authUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etEmail.setText(authUser.getEmail());

        dataApi.findProfiles(null, "eq." + authUser.getUid(), "*").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    currentUserProfile = response.body().get(0);
                    etUsername.setText(currentUserProfile.getUsername());
                    etName.setText(currentUserProfile.getName());
                    etPhone.setText(currentUserProfile.getPhone());
                    etAddress.setText(currentUserProfile.getAddress());
                    etRole.setText(currentUserProfile.getRole() != null ? currentUserProfile.getRole() : "customer");
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentUserProfile == null) {
            AuthUser authUser = SessionManager.getInstance().getCurrentUser();
            if (authUser == null) return;
            currentUserProfile = new User();
            currentUserProfile.setId(authUser.getUid());
            currentUserProfile.setEmail(authUser.getEmail());
        }

        currentUserProfile.setName(etName.getText().toString().trim());
        currentUserProfile.setPhone(etPhone.getText().toString().trim());
        currentUserProfile.setAddress(etAddress.getText().toString().trim());
        currentUserProfile.setRole(etRole.getText().toString().trim().toLowerCase());

        dataApi.upsertProfile("id", currentUserProfile).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error saving: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
