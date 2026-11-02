package com.umesh.urlshortener.config;

import com.umesh.urlshortener.filter.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
@EnableWebSecurity
public class SecurityConfig {
    private UserDetailsService userDetailsService;
    private JwtAuthFilter jwtAuthFilter;
    private final CorsProperties corsProperties;
    private final SecurityProblemHandler problemHandler;
    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthFilter jwtAuthfilter,CorsProperties corsProperties,SecurityProblemHandler problemHandler){
        this.userDetailsService=userDetailsService;
        this.jwtAuthFilter=jwtAuthFilter;
        this.corsProperties=corsProperties;
        this.problemHandler=problemHandler;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.cors(cors->cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf->csrf.disable())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception->exception.authenticationEntryPoint(problemHandler)
                        .accessDeniedHandler(problemHandler))
              .authorizeHttpRequests(auth->auth.requestMatchers("/api/v1/auth/**").
                      permitAll().
                      anyRequest().
                      authenticated())
              .sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(authenticationProvider())
              .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
              return http.build();

    }
    @Bean
    public PasswordEncoder  passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
      DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config=new CorsConfiguration();
        config.setAllowedOrigins(corsProperties.allowedOrigin());
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-CSRF-TOKEN"
        ));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**",config);
        return source;
    }

    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter jwtAuthFilter){
        FilterRegistrationBean<JwtAuthFilter> registration=new FilterRegistrationBean<>(jwtAuthFilter);
        registration.setEnabled(false);
        return registration;

    }
}