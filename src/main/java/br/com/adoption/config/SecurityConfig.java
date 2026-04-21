package br.com.adoption.config;

import br.com.adoption.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animals/available").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animals/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animal-photos").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/animals").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/adoption-requests").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/animals").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/animals/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/animals/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/animals/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/animal-photos").authenticated()
                        .requestMatchers(HttpMethod.POST, "/adoption-requests").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/adoption-requests/*/approve").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/adoption-requests/*/reject").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
