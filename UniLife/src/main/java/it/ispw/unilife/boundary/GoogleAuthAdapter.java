package it.ispw.unilife.boundary;

import com.google.api.services.oauth2.model.Userinfo;
import it.ispw.unilife.bean.ExternalAuthResultBean;
import it.ispw.unilife.bean.OAuthCredentialBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.exception.ExternalAuthenticationException;

import java.util.logging.Logger;


public class GoogleAuthAdapter implements ExternalLogin {

    private static final Logger LOGGER = Logger.getLogger(GoogleAuthAdapter.class.getName());
    private static final String PROVIDER_NAME = "GOOGLE";

    private final GoogleAuthBoundary googleBoundary;

    public GoogleAuthAdapter() {
        this.googleBoundary = new GoogleAuthBoundary();
    }

    @Override
    public ExternalAuthResultBean authenticate() throws ExternalAuthenticationException {

        GoogleAuthBoundary.GoogleAuthResult authResult = googleBoundary.executeLoginAndFetchProfile();

        Userinfo googleUserinfo = authResult.getUserinfo();
        OAuthCredentialBean credentialBean = authResult.getCredentialBean();

        if (googleUserinfo == null) {
            return ExternalAuthResultBean.failure("Impossibile recuperare profilo Google");
        }

        UserBean userBean = convertToUserBean(googleUserinfo);

        LOGGER.info("Autenticazione Google completata per: " + userBean.getUserName());


        return ExternalAuthResultBean.success(userBean, credentialBean);

    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    private UserBean convertToUserBean(Userinfo googleUserinfo) {
        UserBean user = new UserBean();


        user.setUserName(googleUserinfo.getEmail());

        user.setName(googleUserinfo.getGivenName());
        user.setSurname(googleUserinfo.getFamilyName());

        return user;
    }
}
