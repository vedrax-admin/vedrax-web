package com.vedrax.security;

import org.apache.commons.lang3.Validate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * The authentication filter is responsible of retrieving the security token
 * (JWT) from the <code>Authorization</code> header
 *
 * @author remypenchenat
 */
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Filter enables only for a given set of URLs.
     *
     * @param requiresAuth the request matcher
     */
    AuthenticationFilter(final RequestMatcher requiresAuth) {

        super(requiresAuth);
    }

    /**
     * Method for attempting authentication
     *
     * @param request  the servlet request
     * @param response the servlet response
     * @return the authentication object
     * @throws AuthenticationException throws exception if authentication fails
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        String token = safeGetSecurityToken(request);
        //The security token will be available both in principal and credentials attributes
        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
        return getAuthenticationManager().authenticate(auth);
    }

    /**
     * Retrieves the security token from the <code>Authorization</code> header
     * if any, otherwise throws {@link BadCredentialsException}
     *
     * @param request the servlet request
     * @return the security token if any otherwise throws exception if not
     */
    private String safeGetSecurityToken(HttpServletRequest request) {
        Optional<String> headerOpt = getAuthorizationHeader(request);
        return headerOpt
                .map(this::extractSecurityToken)
                .orElseThrow(() -> new BadCredentialsException("Missing Authentication token"));
    }

    /**
     * Utility method for getting the authorization header
     *
     * @param request the servlet request
     * @return an optional of the authorization header
     */
    private Optional<String> getAuthorizationHeader(HttpServletRequest request) {
        Validate.notNull(request, "A HttpServletRequest must be provided");

        String header = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header);
    }

    /**
     * Utility method for extracting the security token
     *
     * @param header the full authorization header
     * @return the security token
     */
    private String extractSecurityToken(String header) {
        //Thrown exception if the token has only bearer or if it does not start with the prefix
        if (header.length() <= 7 || !header.startsWith(TOKEN_PREFIX)) {
            throw new BadCredentialsException("Authorization header is not valid");
        }
        return header.substring(7);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

}
