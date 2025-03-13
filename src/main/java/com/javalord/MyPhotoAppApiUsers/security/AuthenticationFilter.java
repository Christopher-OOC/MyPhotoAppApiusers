package com.javalord.MyPhotoAppApiUsers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalord.MyPhotoAppApiUsers.data.UserEntity;
import com.javalord.MyPhotoAppApiUsers.data.UsersRepository;
import com.javalord.MyPhotoAppApiUsers.model.LoginRequestModel;
import com.javalord.MyPhotoAppApiUsers.service.UsersService;
import com.javalord.MyPhotoAppApiUsers.shared.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UsersRepository usersRepository;
    private Environment environment;

    public AuthenticationFilter(UsersRepository usersRepository,
                                Environment environment,
                                AuthenticationManager authenticationManager) {

        super(authenticationManager);
        this.usersRepository = usersRepository;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginRequestModel loginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);

            System.out.println("Test 1");
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestModel.getEmail(),
                            loginRequestModel.getPassword(),
                            new ArrayList<>()
                    )
            );
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String username = ((User) authResult.getPrincipal()).getUsername();
        UserEntity userDto = usersRepository.findByEmail(username);

        System.out.println("exp" + environment.getProperty("token.expirationTime"));
        System.out.println("sec" + environment.getProperty("token.secret"));

        String tokenSecret = environment.getProperty("token.secret");
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        Instant now = Instant.now();

        System.out.println("Test 3");

        String token = Jwts.builder()
                .subject(userDto.getUserId())
                .expiration(Date.from(now.plusSeconds(Long.parseLong(environment.getProperty("token.expirationTime")))))
                .issuedAt(Date.from(now))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
    }
}
