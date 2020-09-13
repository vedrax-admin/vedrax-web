package com.vedrax.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Objects;

import static org.springframework.http.HttpStatus.FORBIDDEN;
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

    @Value("${base.service.path}")
    private String basePath;

    private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/um/public/**"),
            new AntPathRequestMatcher("/_ah/warmup")
    );

    private final RequestMatcher API_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/um/api/**"),
            new AntPathRequestMatcher("/assessment/api/**"),
            new AntPathRequestMatcher("/pdt/api/**"),
            new AntPathRequestMatcher("/ab/api/**"),
            new AntPathRequestMatcher("/um/admin/**"),
            new AntPathRequestMatcher("/assessment/admin/**"),
            new AntPathRequestMatcher("/pdt/admin/**"),
            new AntPathRequestMatcher("/ab/admin/**"));

    /*
    private final RequestMatcher API_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/um/api/**"),
            new AntPathRequestMatcher("/assessment/api/**"),
            new AntPathRequestMatcher("/pdt/api/**"),
            new AntPathRequestMatcher("/ab/api/**"));

    private final RequestMatcher ADMIN_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/um/admin/**"),
            new AntPathRequestMatcher("/assessment/admin/**"),
            new AntPathRequestMatcher("/pdt/admin/**"),
            new AntPathRequestMatcher("/ab/admin/**"));

     */

    private AuthenticationProvider provider;

    public SecurityConfig(final AuthenticationProvider provider) {
        super();
        this.provider = Objects.requireNonNull(provider);
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
    protected void configure(final HttpSecurity http) throws Exception {
        http
                // use stateless session
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                // when you request a protected resource and you are not yet authenticated
                .exceptionHandling().defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), API_URLS)
                .and()
                // Add our custom JWT authenticate provider
                .authenticationProvider(provider)
                // Add our custom security filter
                .addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter.class)
                // authorization requests config
                .authorizeRequests()
                // Set Admin URL
                //.requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                // Set other Urls
                .requestMatchers(API_URLS)
                .authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }

    @Bean
    public AuthenticationEntryPoint forbiddenEntryPoint() {

        return new HttpStatusEntryPoint(FORBIDDEN);
    }

    @Bean
    public AuthenticationFilter authenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(API_URLS);
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

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> disableAutoRegistration(final AuthenticationFilter filter) {
        final FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

}