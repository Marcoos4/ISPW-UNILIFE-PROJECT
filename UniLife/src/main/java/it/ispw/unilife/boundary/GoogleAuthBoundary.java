package it.ispw.unilife.boundary;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import it.ispw.unilife.bean.OAuthCredentialBean;
import it.ispw.unilife.exception.ExternalAuthenticationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleAuthBoundary {

    private static final Logger LOGGER = Logger.getLogger(GoogleAuthBoundary.class.getName());

    private static final List<String> SCOPES = Arrays.asList("email", "profile", "openid");
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String APPLICATION_NAME = "UniLIFE";

    // Classe per restituire i risultati
    public static class GoogleAuthResult {
        private final Userinfo userinfo;
        private final OAuthCredentialBean credentialBean;

        public GoogleAuthResult(Userinfo userinfo, OAuthCredentialBean credentialBean) {
            this.userinfo = userinfo;
            this.credentialBean = credentialBean;
        }

        public Userinfo getUserinfo() { return userinfo; }
        public OAuthCredentialBean getCredentialBean() { return credentialBean; }
    }

    public GoogleAuthResult executeLoginAndFetchProfile() throws ExternalAuthenticationException {
        try {
            // ====================================================================
            // CONFIGURAZIONE MANUALE (SENZA FILE JSON)
            // ====================================================================
            String clientId = "ID";
            String clientSecret = "secret";

            // Creiamo l'oggetto Secrets manualmente
            GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
            details.setClientId(clientId);
            details.setClientSecret(clientSecret);
            details.setAuthUri("https://accounts.google.com/o/oauth2/auth");
            details.setTokenUri("https://oauth2.googleapis.com/token");

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setInstalled(details);

            // ====================================================================
            // FLUSSO DI AUTENTICAZIONE
            // ====================================================================
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setAccessType("online") // "online" basta se non salviamo nulla
                    .build();

            // Apre il browser locale sulla porta 8888
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(9090).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            // Recupera le info dell'utente
            Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            Userinfo userinfo = oauth2.userinfo().get().execute();

            // Creiamo un bean temporaneo (anche se poi lo butteremo nel controller)
            OAuthCredentialBean credentialBean = new OAuthCredentialBean();
            credentialBean.setProvider("GOOGLE");
            credentialBean.setAccessToken(credential.getAccessToken());
            credentialBean.setExternalUserId(userinfo.getId());

            LOGGER.info("Google Auth: Profilo recuperato per " + userinfo.getEmail());

            return new GoogleAuthResult(userinfo, credentialBean);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore IO Google", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore Generico Google", e);
        }
        return null;
    }
}