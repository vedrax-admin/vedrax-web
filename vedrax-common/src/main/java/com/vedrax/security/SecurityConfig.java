package com.vedrax.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * The Spring Boot Security configuration
 *
 * @author remypenchenat
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("com.vedrax.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/public/**")
    );

    private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);

    private final AuthenticationProvider provider;

    public SecurityConfig(AuthenticationProvider provider) {
        super();
        this.provider = Objects.requireNonNull(provider);
    }

    @Bean
    public AuthenticationFilter authenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(PROTECTED_URLS);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successHandler());
        return filter;
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {

        auth.authenticationProvider(provider);
    }

    @Override
    public void configure(final WebSecurity web) {

        web.ignoring().requestMatchers(PUBLIC_URLS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Disable CSRF
        http = http.csrf().disable();

        // set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and();

        //set unauthorized requests exception handler
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        ))
                .and();

        // Set permissions on endpoints
        http.authorizeRequests()
                // Our public endpoints
                .requestMatchers(PUBLIC_URLS).permitAll()
                // Our private endpoints
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        // Add JWT token filter
        http.addFilterBefore(
                authenticationFilter(),
                AnonymousAuthenticationFilter.class);

    }

}
