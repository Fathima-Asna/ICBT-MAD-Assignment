package com.printxpress.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.printxpress.android.R;
import com.printxpress.android.data.model.User;
import com.printxpress.android.ui.product.ProductListActivity;
import com.printxpress.android.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText etIdentifier, etPassword;
    private Button btnLogin, btnGoogleSignIn;
    private TextView tvForgot, tvGoToRegister;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                .getResult(ApiException.class);
                        if (account != null && account.getIdToken() != null) {
                            firebaseAuthWithGoogle(account.getIdToken(), account);
                        } else {
                            Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, getGoogleSignInErrorMessage(e.getStatusCode()), Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        etIdentifier = findViewById(R.id.etIdentifier);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvForgot = findViewById(R.id.tvForgotPassword);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        tvForgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        authViewModel.getAuthResult().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess()) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                navigateToMainApp();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getProfileResult().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess()) {
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                navigateToMainApp();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMainApp() {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        User user = new User();
                        user.setId(firebaseUser.getUid());
                        user.setEmail(firebaseUser.getEmail());
                        user.setName(firebaseUser.getDisplayName());
                        if (account.getPhotoUrl() != null) {
                            user.setProfile(account.getPhotoUrl().toString());
                        }
                        authViewModel.createUserProfile(user);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String getGoogleSignInErrorMessage(int statusCode) {
        switch (statusCode) {
            case com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                return "Google sign-in cancelled";
            case com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_FAILED:
                return "Google sign-in failed. Add SHA-1 in Firebase Console and re-download google-services.json.";
            case com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.DEVELOPER_ERROR:
                return "Google Sign-In config error. Check SHA-1 and google-services.json.";
            default:
                return "Google sign-in failed (code " + statusCode + ")";
        }
    }

    private void attemptLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String password = etPassword.getText().toString();
        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email/username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }
        authViewModel.login(identifier, password);
    }
}
