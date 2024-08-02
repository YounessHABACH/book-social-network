package com.ynshb.config;

import com.ynshb.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        User principalUser = (User) authentication.getPrincipal();
        return Optional.ofNullable(principalUser.getId());
    }
}
