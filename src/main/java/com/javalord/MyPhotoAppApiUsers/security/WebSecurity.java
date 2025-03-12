package com.javalord.MyPhotoAppApiUsers.security;

import com.example.appdevelopersblog.PhotoAppAPIUsers.service.UsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment environment;

    public WebSecurity(UsersService usersService,
                       Environment environment,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.usersService = usersService;
        this.environment  = environment;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {

        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = managerBuilder.build();

        return http.authorizeHttpRequests( requests -> requests
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers("/users/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
//                        .access(new WebExpressionAuthorizationManager("hasIpAddress('"+environment.getProperty("gateway.ip")+"')"))
                        .requestMatchers("/h2-console/**").permitAll()
        )
                .csrf(csrf -> csrf
                        .disable()
                )
                .addFilter(new AuthenticationFilter(usersService, environment, authenticationManager))
                .authenticationManager(authenticationManager)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .build();
    }

}
