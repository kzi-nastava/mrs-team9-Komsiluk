package rs.ac.uns.ftn.iss.Komsiluk.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import rs.ac.uns.ftn.iss.Komsiluk.security.filter.JwtAuthFilter;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/tokens/activation/**").permitAll()
                        .requestMatchers("/api/tokens/reset-password").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/images/**").permitAll()

                        // ROLE BASED
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/drivers/**").hasRole("DRIVER")
                        .requestMatchers("/api/passenger/**").hasRole("PASSENGER")

                        // RIDES (stabilni matcher-i)
                        .requestMatchers(HttpMethod.POST, "/api/rides/*/inconsistencies")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/rides/*/ratings").hasRole("PASSENGER")
                        .requestMatchers(HttpMethod.GET,  "/api/rides/*/ratings/**").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")


                        .requestMatchers(HttpMethod.POST, "/api/rides/*/finish").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/rides/*/live").hasAnyRole("DRIVER", "PASSENGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rides/*/live" ).hasAnyRole("DRIVER", "ADMIN")
                        // (ako koristiš start/stop/cancel/estimate/order)
                        .requestMatchers(HttpMethod.POST, "/api/rides/**").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/rides/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/rides/**").authenticated()

                        // sve ostalo
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // samo za H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
