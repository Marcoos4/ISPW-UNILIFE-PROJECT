package it.ispw.unilife.boundary;

import it.ispw.unilife.bean.CertificateValidationBean;
import it.ispw.unilife.bean.DocumentBean;
import it.ispw.unilife.exception.InvalidCertificateException;

public class CertificateValidationBoundary {
    public CertificateValidationBean validateCertificate(DocumentBean documentBean) throws InvalidCertificateException {
        CertificateValidationBean bean = new CertificateValidationBean();
        bean.setDocument(documentBean);
        if (documentBean == null){
            bean.setValid(false);
            throw new InvalidCertificateException("Document is null");
        }else {
            bean.setValid(true);
        }

        return bean;
    }
}
