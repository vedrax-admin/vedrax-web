package com.vedrax.security;

import org.jboss.logging.Logger;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 *
 * @author remypenchenat
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final Logger LOGGER = Logger.getLogger(AuditorAwareImpl.class.getName());

    @Override
    public Optional<String> getCurrentAuditor() {

        //get the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //when no authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        //when authenticated
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        if (userPrincipal == null) {
            return Optional.empty();
        }

        return Optional.of(userPrincipal.getUsername());

    }

}
