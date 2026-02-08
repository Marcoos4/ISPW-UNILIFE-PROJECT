package it.ispw.unilife.boundary;

import it.ispw.unilife.bean.ExternalAuthResultBean;
import it.ispw.unilife.exception.ExternalAuthenticationException;


public interface ExternalLogin {


    ExternalAuthResultBean authenticate() throws ExternalAuthenticationException, InterruptedException;

    String getProviderName();
}
