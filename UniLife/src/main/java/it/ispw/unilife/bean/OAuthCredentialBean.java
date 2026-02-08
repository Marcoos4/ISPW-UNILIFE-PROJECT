package it.ispw.unilife.bean;

/**
 * Bean (DTO) per trasferire le credenziali OAuth tra Boundary e Controller.
 * Non contiene logica di business, solo dati.
 */
public class OAuthCredentialBean {

    private String accessToken;
    private String refreshToken;
    private long expirationTimeMillis;
    private String provider;  // "GOOGLE" o "GITHUB"
    private String externalUserId;  // ID utente sul provider esterno

    public OAuthCredentialBean() {
    }

    public OAuthCredentialBean(String provider, String accessToken) {
        this.provider = provider;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    public void setExpirationTimeMillis(long expirationTimeMillis) {
        this.expirationTimeMillis = expirationTimeMillis;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTimeMillis;
    }
}
