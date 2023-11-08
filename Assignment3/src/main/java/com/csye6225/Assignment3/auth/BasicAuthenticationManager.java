package com.csye6225.Assignment3.auth;

import com.csye6225.Assignment3.controller.AssignmentController;
import com.csye6225.Assignment3.repository.UserRepository;
import com.csye6225.Assignment3.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BasicAuthenticationManager implements AuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";
//        System.out.println("In AuthenticateManager " );
//        System.out.println("Username " + username + " Pass " + password);
        Optional<User> user =  userRepository.findByEmail(username);
        if (user == null) {
            logger.error("User Doesn't exist");
            throw new BadCredentialsException("1000");
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            logger.error("Password doesn't match");
            throw new BadCredentialsException("1000");
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(authority);
        return new UsernamePasswordAuthenticationToken(username,password,list);
//        new SimpleGrantedAuthority(user.get().getEmail())), username, "");
//                userRights.stream().map(x -> new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList()));
    }


}
