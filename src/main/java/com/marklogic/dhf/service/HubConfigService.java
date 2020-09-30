package com.marklogic.dhf.service;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.hub.DatabaseKind;
import com.marklogic.hub.HubConfig;
import com.marklogic.hub.impl.HubConfigImpl;
import com.marklogic.service.SSLConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

@Component
public class HubConfigService {

    private final String host;
    private final boolean gateway;

    private final String finalDbName;
    private final Integer finalDbPort;
    private final String finalAuthMethod;

    private final String stagingDbName;
    private final Integer stagingDbPort;
    private final String stagingAuthMethod;

    private final String modulesDbName;

    private final String jobDbName;
    private final Integer jobDbPort;
    private final String jobAuthMethod;

    private final SSLConfigService sslConfig;

    public HubConfigService(
            @Autowired SSLConfigService sslConfig,
            @Value("${marklogic.host}") String host,
            @Value("${marklogic.digestAuth:true}") boolean digestAuth,
            @Value("${marklogic.isGateway:false}") boolean gateway,
            @Value("${marklogic.finalDbName:data-hub-FINAL}") String finalDbName,
            @Value("${marklogic.finalDbPort:8011}") Integer finalDbPort,
            @Value("${marklogic.finalAuthMethod:digest}") String finalAuthMethod,
            @Value("${marklogic.stagingDbName:data-hub-STAGING}") String stagingDbName,
            @Value("${marklogic.stagingDbPort:8010}") Integer stagingDbPort,
            @Value("${marklogic.stagingAuthMethod:digest}") String stagingAuthMethod,
            @Value("${marklogic.jobDbName:data-hub-JOBS}") String jobDbName,
            @Value("${marklogic.jobDbPort:8013}") Integer jobDbPort,
            @Value("${marklogic.jobAuthMethod:digest}") String jobAuthMethod,
            @Value("${marklogic.modulesDbName:data-hub-MODULES}") String modulesDbName
    ) {

        this.sslConfig = sslConfig;

        this.host = host;
        this.gateway = gateway;

        this.finalDbName = finalDbName;
        this.finalDbPort = finalDbPort;
        this.finalAuthMethod = finalAuthMethod;

        this.stagingDbName = stagingDbName;
        this.stagingDbPort = stagingDbPort;
        this.stagingAuthMethod = stagingAuthMethod;

        this.jobDbName = jobDbName;
        this.jobDbPort = jobDbPort;
        this.jobAuthMethod = jobAuthMethod;

        this.modulesDbName = modulesDbName;
    }

    public HubConfig hubConfig(String username, String password) {
        HubConfigImpl hubConfig = new HubConfigImpl();

        hubConfig.setHost(host);
        hubConfig.setMlUsername(username);
        hubConfig.setMlPassword(password);

        hubConfig.setDbName(DatabaseKind.FINAL, this.finalDbName);
        hubConfig.setPort(DatabaseKind.FINAL, this.finalDbPort);
        hubConfig.setAuthMethod(DatabaseKind.FINAL, this.finalAuthMethod);

        hubConfig.setDbName(DatabaseKind.STAGING, this.stagingDbName);
        hubConfig.setPort(DatabaseKind.STAGING, this.stagingDbPort);
        hubConfig.setAuthMethod(DatabaseKind.STAGING, this.stagingAuthMethod);

        hubConfig.setDbName(DatabaseKind.JOB, this.jobDbName);
        hubConfig.setPort(DatabaseKind.JOB, this.jobDbPort);
        hubConfig.setAuthMethod(DatabaseKind.JOB, this.jobAuthMethod);

        hubConfig.setDbName(DatabaseKind.MODULES, this.modulesDbName);

        if (sslConfig.isSsl()) {
            SSLContext sslContext = sslConfig.sslContext();
            X509TrustManager trustManager = sslConfig.trustManager();

            hubConfig.setSslContext(DatabaseKind.FINAL, sslContext);
            hubConfig.setSslHostnameVerifier(DatabaseKind.FINAL, DatabaseClientFactory.SSLHostnameVerifier.ANY);
            hubConfig.setTrustManager(DatabaseKind.FINAL, trustManager);

            hubConfig.setSslContext(DatabaseKind.STAGING, sslContext);
            hubConfig.setSslHostnameVerifier(DatabaseKind.STAGING, DatabaseClientFactory.SSLHostnameVerifier.ANY);
            hubConfig.setTrustManager(DatabaseKind.STAGING, trustManager);

            hubConfig.setSslContext(DatabaseKind.JOB, sslContext);
            hubConfig.setSslHostnameVerifier(DatabaseKind.JOB, DatabaseClientFactory.SSLHostnameVerifier.ANY);
            hubConfig.setTrustManager(DatabaseKind.JOB, trustManager);
        }

        hubConfig.applyProperties(name -> {
            switch (name) {
                case "mlIsHostLoadBalancer":
                    return this.gateway ? "true" : "false";
                default:
                    return null;
            }
        });

        return hubConfig;
    }


}
