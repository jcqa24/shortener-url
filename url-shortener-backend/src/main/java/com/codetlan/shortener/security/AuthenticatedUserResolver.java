package com.codetlan.shortener.security;

import com.codetlan.shortener.entity.User;
import com.codetlan.shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class AuthenticatedUserResolver {
    private final UserRepository userRepository;

    public  User resolve(Authentication authentication){
        if(authentication == null
            || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }
}

