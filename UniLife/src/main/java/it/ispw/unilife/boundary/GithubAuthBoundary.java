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

public class GithubAuthBoundary {

    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";

    private static final String REDIRECT_URI = "http://localhost:8080/callback";

    private static final String AUTH_URL = "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID
            + "&redirect_uri=" + REDIRECT_URI + "&scope=user:email";


    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private String capturedCode = null;

    public String getAuthorizationCode() {

        CookieHandler.setDefault(new CookieManager());

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Login con GitHub");
        stage.setScene(new Scene(webView, 600, 700));


        engine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl != null && newUrl.contains("code=")) {
                String[] parts = newUrl.split("code=");
                if (parts.length > 1) {
                    this.capturedCode = parts[1].split("&")[0]; 
                    stage.close();
                }
            }
        });

        engine.load(AUTH_URL);
        stage.showAndWait();

        return this.capturedCode;
    }


    public String exchangeCodeForToken(String code) throws IOException, InterruptedException, ExternalAuthenticationException {

        String params = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&code=" + code;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://github.com/login/oauth/access_token"))
                .header("Accept", "application/json") 
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ExternalAuthenticationException("GitHub ha risposto con errore: " + response.statusCode());
        }

        String accessToken = extractJsonValue(response.body(), "access_token");

        if (accessToken == null) {

            throw new ExternalAuthenticationException("Impossibile ottenere il token. Risposta GitHub: " + response.body());
        }

        return accessToken;
    }

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


    private String extractJsonValue(String json, String key) {
        if (json == null) return null;
        Matcher matcher = Pattern.compile("\"" + key + "\":\\s*\"([^\"]+)\"").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
