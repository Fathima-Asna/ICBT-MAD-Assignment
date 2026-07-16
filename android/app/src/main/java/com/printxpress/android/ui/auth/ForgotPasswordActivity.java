package com.printxpress.android.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.printxpress.android.R;
import com.printxpress.android.util.ValidationUtils;
import com.printxpress.android.viewmodel.AuthViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText etEmail;
    private Button btnSendReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        btnSendReset = findViewById(R.id.btnSendReset);

        btnSendReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!ValidationUtils.isValidEmail(email)) {
                Toast.makeText(this, "Valid email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.sendPasswordReset(email);
        });

        authViewModel.getResetResult().observe(this, response -> {
            if (response == null) return;
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            if (response.isSuccess()) {
                finish();
            }
        });
    }
}
