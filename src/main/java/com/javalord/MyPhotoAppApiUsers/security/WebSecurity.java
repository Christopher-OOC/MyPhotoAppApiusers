package com.javalord.MyPhotoAppApiUsers.security;

import com.javalord.MyPhotoAppApiUsers.data.UsersRepository;
import com.javalord.MyPhotoAppApiUsers.service.UsersService;
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

    UsersRepository usersRepository;
    private Environment environment;

    public WebSecurity(
            UsersRepository usersRepository,
           Environment environment
    ) {
        this.environment  = environment;
        this.usersRepository = usersRepository;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {

        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = managerBuilder.build();

        AuthenticationFilter authFilter = new AuthenticationFilter(usersRepository, environment, authenticationManager);
        authFilter.setFilterProcessesUrl("/users/login");

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
                .addFilter(authFilter)
                .authenticationManager(authenticationManager)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .build();
    }

}
