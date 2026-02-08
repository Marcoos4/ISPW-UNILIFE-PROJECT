package it.ispw.unilife.bean;

/**
 * Bean che contiene il risultato completo dell'autenticazione esterna.
 * Combina i dati utente e le credenziali OAuth.
 */
public class ExternalAuthResultBean {

    private UserBean userBean;
    private OAuthCredentialBean credentialBean;
    private boolean success;
    private String errorMessage;

    public ExternalAuthResultBean() {
        this.success = false;
    }

    public static ExternalAuthResultBean success(UserBean user, OAuthCredentialBean credential) {
        ExternalAuthResultBean result = new ExternalAuthResultBean();
        result.setSuccess(true);
        result.setUserBean(user);
        result.setCredentialBean(credential);
        return result;
    }

    public static ExternalAuthResultBean failure(String errorMessage) {
        ExternalAuthResultBean result = new ExternalAuthResultBean();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public OAuthCredentialBean getCredentialBean() {
        return credentialBean;
    }

    public void setCredentialBean(OAuthCredentialBean credentialBean) {
        this.credentialBean = credentialBean;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
