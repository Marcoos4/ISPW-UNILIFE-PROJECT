package it.ispw.unilife.boundary;

import com.google.api.services.oauth2.model.Userinfo;
import it.ispw.unilife.bean.ExternalAuthResultBean;
import it.ispw.unilife.bean.OAuthCredentialBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.exception.ExternalAuthenticationException;

import java.util.logging.Logger;

/**
 * Adapter che adatta l'API Google all'interfaccia ExternalLogin.
 *
 * RESPONSABILITÀ:
 * - Coordinare il flusso OAuth con GoogleAuthBoundary
 * - Convertire Userinfo di Google in UserBean
 * - Restituire UserBean + OAuthCredentialBean al Controller
 *
 * NON SALVA I TOKEN - li passa al Controller che userà il DAO.
 */
public class GoogleAuthAdapter implements ExternalLogin {

    private static final Logger LOGGER = Logger.getLogger(GoogleAuthAdapter.class.getName());
    private static final String PROVIDER_NAME = "GOOGLE";

    private final GoogleAuthBoundary googleBoundary;

    public GoogleAuthAdapter() {
        this.googleBoundary = new GoogleAuthBoundary();
    }

    @Override
    public ExternalAuthResultBean authenticate() throws ExternalAuthenticationException {
        // 1. Esegui login Google (apre browser, ottiene token e profilo)
        GoogleAuthBoundary.GoogleAuthResult authResult = googleBoundary.executeLoginAndFetchProfile();

        Userinfo googleUserinfo = authResult.getUserinfo();
        OAuthCredentialBean credentialBean = authResult.getCredentialBean();

        if (googleUserinfo == null) {
            return ExternalAuthResultBean.failure("Impossibile recuperare profilo Google");
        }

        // 2. Converti Userinfo Google in UserBean
        UserBean userBean = convertToUserBean(googleUserinfo);

        LOGGER.info("Autenticazione Google completata per: " + userBean.getUserName());

        // 3. Restituisci tutto al Controller
        return ExternalAuthResultBean.success(userBean, credentialBean);

    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /**
     * Converte Userinfo di Google in UserBean.
     */
    private UserBean convertToUserBean(Userinfo googleUserinfo) {
        UserBean user = new UserBean();

        // Email come username (identificativo univoco Google)
        user.setUserName(googleUserinfo.getEmail());

        // Nome e cognome
        user.setName(googleUserinfo.getGivenName());
        user.setSurname(googleUserinfo.getFamilyName());

        return user;
    }
}
