package com.printxpress.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.printxpress.android.R;
import com.printxpress.android.data.model.User;
import com.printxpress.android.util.ValidationUtils;
import com.printxpress.android.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText etUsername, etName, etPhone, etEmail, etAddress, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        authViewModel.getAuthResult().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess()) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString();

        String error = ValidationUtils.validateRegisterInput(name, phone, email, address, password, username);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);

        authViewModel.register(user, password);
    }
}
