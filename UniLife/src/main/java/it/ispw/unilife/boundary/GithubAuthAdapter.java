package it.ispw.unilife.boundary;

import it.ispw.unilife.bean.ExternalAuthResultBean;
import it.ispw.unilife.bean.OAuthCredentialBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.exception.ExternalAuthenticationException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter che adatta l'API GitHub all'interfaccia ExternalLogin.
 *
 * RESPONSABILITÀ:
 * - Coordinare il flusso OAuth con GithubAuthBoundary
 * - Convertire la risposta GitHub in Bean (UserBean + OAuthCredentialBean)
 * - Restituire i dati al Controller
 *
 * NON SALVA I TOKEN - li passa al Controller che userà il DAO.
 */
public class GithubAuthAdapter implements ExternalLogin {

    private static final Logger LOGGER = Logger.getLogger(GithubAuthAdapter.class.getName());
    private static final String PROVIDER_NAME = "GITHUB";

    // Token validity: GitHub tokens don't expire by default, but we set a reasonable time
    private static final long TOKEN_VALIDITY_MILLIS = 365L * 24 * 60 * 60 * 1000; // 1 anno

    private final GithubAuthBoundary githubBoundary;

    public GithubAuthAdapter() {
        this.githubBoundary = new GithubAuthBoundary();
    }

    @Override
    public ExternalAuthResultBean authenticate() throws ExternalAuthenticationException, InterruptedException {
        try {
            // 1. Ottieni codice di autorizzazione (apre browser)
            String code = githubBoundary.getAuthorizationCode();
            if (code == null) {
                return ExternalAuthResultBean.failure("Login annullato dall'utente");
            }

            // 2. Scambia codice per access token
            String accessToken = githubBoundary.exchangeCodeForToken(code);
            if (accessToken == null) {
                return ExternalAuthResultBean.failure("Impossibile ottenere token da GitHub");
            }

            // 3. Recupera profilo utente
            String profileJson = githubBoundary.fetchUserProfile(accessToken);
            if (profileJson == null) {
                return ExternalAuthResultBean.failure("Impossibile recuperare profilo GitHub");
            }

            // 4. Crea UserBean dal profilo
            UserBean userBean = parseUserProfile(profileJson);

            // 5. Crea OAuthCredentialBean con il token
            OAuthCredentialBean credentialBean = new OAuthCredentialBean();
            credentialBean.setProvider(PROVIDER_NAME);
            credentialBean.setAccessToken(accessToken);
            credentialBean.setExpirationTimeMillis(System.currentTimeMillis() + TOKEN_VALIDITY_MILLIS);
            credentialBean.setExternalUserId(parseJson(profileJson, "id"));
            // GitHub non fornisce refresh token di default
            credentialBean.setRefreshToken(null);

            LOGGER.info("Autenticazione GitHub completata per utente: " + userBean.getUserName());

            // 6. Restituisci tutto al Controller
            return ExternalAuthResultBean.success(userBean, credentialBean);

        } catch (IOException e) {
            throw new ExternalAuthenticationException("Errore di comunicazione con GitHub: " + e.getMessage());
        }catch (InterruptedException e){
            LOGGER.log(Level.SEVERE, "Errore di comunicazione con GitHub", e);
            throw new InterruptedException("Errore di comunicazione con GitHub");
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /**
     * Converte il JSON del profilo GitHub in UserBean.
     */
    private UserBean parseUserProfile(String profileJson) {
        UserBean user = new UserBean();

        // Username GitHub
        user.setUserName(parseJson(profileJson, "login"));

        // Nome completo (può essere null)
        String name = parseJson(profileJson, "name");
        if (name != null && name.contains(" ")) {
            user.setName(name.split(" ")[0]);
            user.setSurname(name.substring(name.indexOf(" ") + 1));
        } else {
            user.setName(name != null ? name : user.getUserName());
            user.setSurname("");
        }

        return user;
    }

    /**
     * Estrae un valore stringa da JSON.
     */
    private String parseJson(String json, String key) {
        // Per "id" che è numerico
        Matcher numericMatcher = Pattern.compile("\"" + key + "\":\\s*(\\d+)").matcher(json);
        if (numericMatcher.find()) {
            return numericMatcher.group(1);
        }

        // Per valori stringa
        Matcher stringMatcher = Pattern.compile("\"" + key + "\":\\s*\"([^\"]+)\"").matcher(json);
        return stringMatcher.find() ? stringMatcher.group(1) : null;
    }
}
