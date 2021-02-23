package com.marklogic;

import com.marklogic.spring.security.CustomAuthenticationProvider;
import com.marklogic.spring.security.PlainTextPasswordEncoder;
import com.marklogic.spring.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final BasicAuthenticationEntryPoint authEntryPoint;

    public SpringWebSecurityConfiguration(@Autowired BasicAuthenticationEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            CustomAuthenticationProvider authenticationProvider,
            PasswordEncoder passwordEncoder
    ) throws Exception {
        auth
                // We want to retain the password for calls to MarkLogic
                .eraseCredentials(false)
                .authenticationProvider(authenticationProvider)
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/css/**", "/images/**", "/js/**", "/webjars/**", "/**/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic().realmName(SecurityConstants.REALM).authenticationEntryPoint(this.authEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need sessions to be created.
    }

    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder();
    }
}
