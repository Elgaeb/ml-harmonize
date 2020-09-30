package com.marklogic.spring.security;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.dhf.service.HubConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    private final HubConfigService hubConfigService;

    public CustomAuthenticationProvider(
            @Autowired HubConfigService hubConfigService
    ) {
        this.hubConfigService = hubConfigService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        DatabaseClient appServicesClient = hubConfigService.hubConfig(username, password).newStagingClient();
        ServerEvaluationCall serverEval = appServicesClient.newServerEval();

        try {
            String roles = serverEval
                    .javascript("xdmp.getCurrentRoles().toArray().map(roleNum => xdmp.roleName(roleNum)).join(',')")
                    .evalAs(String.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(roles)
            );
            return token;
        } catch (ForbiddenUserException | FailedRequestException ex) {
            throw new BadCredentialsException("Unauthorized");
        } catch (Throwable t) {
            logger.error("Caught unexpected Throwable during authentication", t);
            throw t;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
