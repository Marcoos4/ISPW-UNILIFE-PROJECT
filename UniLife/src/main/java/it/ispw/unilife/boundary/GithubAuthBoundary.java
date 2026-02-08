package it.ispw.unilife.boundary;

import it.ispw.unilife.exception.ExternalAuthenticationException;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Boundary per l'interazione con l'API OAuth di GitHub.
 */
public class GithubAuthBoundary {

    private static final String CLIENT_ID = "ID";
    private static final String CLIENT_SECRET = "secret";

    // Assicurati che questo URL sia identico a quello registrato su GitHub Developer Settings
    private static final String REDIRECT_URI = "http://localhost:8080/callback";

    private static final String AUTH_URL = "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID
            + "&redirect_uri=" + REDIRECT_URI + "&scope=user:email";

    // Unica istanza condivisa (Thread-safe e ottimizzata)
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private String capturedCode = null;

    /**
     * Apre la finestra di login GitHub e cattura il codice di autorizzazione.
     *
     * @return il codice di autorizzazione, o null se l'utente chiude la finestra senza login
     */
    public String getAuthorizationCode() {
        // Pulisce i cookie precedenti per forzare il ri-login se necessario
        CookieHandler.setDefault(new CookieManager());

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Login con GitHub");
        stage.setScene(new Scene(webView, 600, 700));

        // Listener per intercettare il redirect contenente il codice
        engine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl != null && newUrl.contains("code=")) {
                // Estrarre il codice dall'URL (es: http://localhost/callback?code=XYZ&...)
                String[] parts = newUrl.split("code=");
                if (parts.length > 1) {
                    this.capturedCode = parts[1].split("&")[0]; // Prende tutto prima dell'eventuale altro parametro
                    stage.close();
                }
            }
        });

        engine.load(AUTH_URL);
        stage.showAndWait();

        return this.capturedCode;
    }

    /**
     * Scambia il codice di autorizzazione per un access token.
     */
    public String exchangeCodeForToken(String code) throws IOException, InterruptedException, ExternalAuthenticationException {

        String params = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&code=" + code;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://github.com/login/oauth/access_token"))
                .header("Accept", "application/json") // Chiediamo esplicitamente JSON
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ExternalAuthenticationException("GitHub ha risposto con errore: " + response.statusCode());
        }

        String accessToken = extractJsonValue(response.body(), "access_token");

        if (accessToken == null) {
            // Se GitHub restituisce un JSON di errore (es. "error": "bad_verification_code")
            throw new ExternalAuthenticationException("Impossibile ottenere il token. Risposta GitHub: " + response.body());
        }

        return accessToken;
    }

    /**
     * Recupera il profilo utente da GitHub usando il token.
     */
    public String fetchUserProfile(String accessToken) throws IOException, InterruptedException, ExternalAuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ExternalAuthenticationException("Errore nel recupero profilo: " + response.statusCode());
        }

        return response.body();
    }

    /**
     * Utility per estrarre valori da JSON semplice senza librerie esterne.
     * Nota: Per progetti reali complessi, meglio usare Jackson o Gson.
     */
    private String extractJsonValue(String json, String key) {
        if (json == null) return null;
        // Regex semplice per trovare "chiave":"valore"
        Matcher matcher = Pattern.compile("\"" + key + "\":\\s*\"([^\"]+)\"").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}