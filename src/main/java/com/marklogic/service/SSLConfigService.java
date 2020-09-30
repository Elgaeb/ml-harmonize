package com.marklogic.service;

import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class SSLConfigService {

    private final boolean ssl;
    private final SSLContext sslContext;
    private final X509TrustManager trustManager;

    public SSLConfigService(
            @Value("${marklogic.truststore:#{null}}") Resource truststore,
            @Value("${marklogic.truststoreFormat:PKCS12}") String truststoreFormat,
            @Value("${marklogic.truststorePassword:#{null}}") String truststorePassword,
            @Value("${marklogic.ssl:false}") boolean ssl,
            @Value("${marklogic.verifyTrust:true}") boolean verifyTrust,
            @Value("${marklogic.verifyHostname:true}") boolean verifyHostname
    ) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, KeyManagementException {
        this.ssl = ssl;

        if (this.ssl) {
            this.trustManager =
                    verifyTrust ?
                            (truststore == null ?
                                    makeDefaultTrustManager() :
                                    makeTrustManager(truststore, truststoreFormat, truststorePassword)) :
                            new SimpleX509TrustManager();

            TrustManager[] trustManagers = {trustManager};

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } else {
            this.sslContext = SSLContext.getDefault();
            this.trustManager = makeDefaultTrustManager();
        }
    }

    private X509TrustManager makeDefaultTrustManager() throws NoSuchAlgorithmException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private X509TrustManager makeTrustManager(Resource truststoreLocation, String truststoreFormat, String truststorePassword) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        KeyStore truststore = KeyStore.getInstance(truststoreFormat);
        try (InputStream truststoreInput = truststoreLocation.getInputStream()) {
            truststore.load(truststoreInput, truststorePassword.toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststore);

        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }

        return null;
    }

    public X509TrustManager trustManager() {
        return this.trustManager;
    }

    public SSLContext sslContext() {
        return this.sslContext;
    }

    public boolean isSsl() {
        return this.ssl;
    }
}
