package com.printxpress.backend.config;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    /**
     * Local dev convenience: if a service account key file exists at this path, use it directly.
     * On Cloud Run / Firebase App Hosting no such file exists, so this falls back to the
     * environment's Application Default Credentials (the attached service account identity).
     */
    @Bean
    public Firestore firestore(@Value("${app.firebase-credentials-file:secrets/serviceAccountKey.json}") String credentialsFile) throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials credentials;
            if (Files.exists(Paths.get(credentialsFile))) {
                try (FileInputStream serviceAccount = new FileInputStream(credentialsFile)) {
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                }
            } else {
                credentials = GoogleCredentials.getApplicationDefault();
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);
        }
        return FirestoreClient.getFirestore();
    }
}
