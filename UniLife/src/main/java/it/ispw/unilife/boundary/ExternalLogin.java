package it.ispw.unilife.boundary;

import it.ispw.unilife.bean.ExternalAuthResultBean;
import it.ispw.unilife.exception.ExternalAuthenticationException;

/**
 * Interface per l'autenticazione esterna (Pattern Adapter).
 *
 * Ogni implementazione (Google, GitHub) adatta l'API specifica
 * del provider a un'interfaccia comune.
 *
 * IMPORTANTE: La Boundary NON gestisce la persistenza dei token.
 * Restituisce i token al Controller che li salverà tramite DAO.
 */
public interface ExternalLogin {

    /**
     * Esegue l'autenticazione con il provider esterno.
     *
     * @return ExternalAuthResultBean contenente UserBean + OAuthCredentialBean
     * @throws ExternalAuthenticationException se l'autenticazione fallisce
     */
    ExternalAuthResultBean authenticate() throws ExternalAuthenticationException, InterruptedException;

    /**
     * Restituisce il nome del provider.
     */
    String getProviderName();
}
