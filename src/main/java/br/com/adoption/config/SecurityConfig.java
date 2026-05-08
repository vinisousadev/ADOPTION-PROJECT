package br.com.adoption.config;

import br.com.adoption.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/confirm-email").permitAll()
                        .requestMatchers("/auth/resend-confirmation").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animals/available", "/animals/available/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animals/mine").authenticated()
                        .requestMatchers(HttpMethod.GET, "/animals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animal-photos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animal-photos/*").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users/me/profile-photo").authenticated()
                        .requestMatchers(HttpMethod.GET, "/animals").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/adoption-requests").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/adoption-requests/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/feed-posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/feed-posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/feed-posts/*/photo").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/feed-posts/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/feed-posts/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/feed-posts/*/likes").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/feed-posts/*/likes").authenticated()
                        .requestMatchers(HttpMethod.GET, "/feed-posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/feed-posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/feed-posts/comments/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/feed-posts/comments/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/feed-posts/*/video").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/feed-posts/*/video").authenticated()
                        .requestMatchers(HttpMethod.GET, "/notifications").authenticated()
                        .requestMatchers(HttpMethod.GET, "/notifications/unread-count").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/notifications/*/read").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/notifications/read-all").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/users/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/users/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/animals").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/animals/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/animals/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/animals/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/animal-photos").authenticated()
                        .requestMatchers(HttpMethod.POST, "/animal-photos/upload").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/animal-photos/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/animal-photos/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/animal-photos/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/adoption-requests").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/adoption-requests/*/approve").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/adoption-requests/*/reject").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/adoption-requests/*/cancel").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://adotapp.com",
                "https://www.adotapp.com"
        ));
        configuration.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
