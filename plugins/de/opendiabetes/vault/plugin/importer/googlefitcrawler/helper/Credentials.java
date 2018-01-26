package de.opendiabetes.vault.plugin.importer.googlefitcrawler.helper;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.fitness.FitnessScopes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class Credentials {

    private static Credentials instance;
    private static final String APPLICATION_NAME =
            "BachelorArbeit";
    private static HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/contacts.readonly", "https://www.googleapis.com/auth/plus.login", FitnessScopes.FITNESS_ACTIVITY_READ, FitnessScopes.FITNESS_BODY_READ, FitnessScopes.FITNESS_LOCATION_READ);
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/googleapis.de-nkpyck-googledatagatherer");
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static Credential credential;

    private static String APIKey;


    private Credentials() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Credentials getInstance() {
        if (Credentials.instance == null) {
            Credentials.instance = new Credentials();
        }
        return Credentials.instance;
    }


    public void authorize(String path) throws IOException {
        File file = new File(Paths.get(path).toAbsolutePath().toString());
        // Load client secrets.
        Reader reader = new FileReader(file);

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, reader);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

    }

    public HttpTransport getHttpTransport() {
        return HTTP_TRANSPORT;
    }

    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    public String getApplicationName() {
        return APPLICATION_NAME;
    }

    public Credential getCredential() {
        return credential;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIkey(String APIkey) {
        Credentials.APIKey = APIkey;
    }
}

