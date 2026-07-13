package az.clinify.demo.configs;

import az.clinify.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()
                        // Auth endpoints - public
                        .requestMatchers(HttpMethod.POST, "/api/auth/check-fin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/setup-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register-new-user").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/departments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/departments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/departments/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/doctors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/*/deactivate").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/availabilities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/availabilities/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/availabilities").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/availabilities/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/availabilities/*/status").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/availabilities/**").hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.GET, "/api/appointments/mine").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/doctor/mine").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/appointments").hasAnyRole("ADMIN", "RECEPTION")
                        .requestMatchers(HttpMethod.POST, "/api/appointments/walk-in").hasRole("RECEPTION")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/*")
                        .hasAnyRole("ADMIN", "DOCTOR", "RECEPTION")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/patient/**")
                        .hasAnyRole("ADMIN", "RECEPTION")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/doctor/*").hasAnyRole("ADMIN", "RECEPTION")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/approve", "/api/appointments/*/reject",
                                "/api/appointments/*/complete")
                        .hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/status")
                        .hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,
                                "/api/records/doctor/mine",
                                "/api/records/doctor/mine/*",
                                "/api/records/doctor/patients")
                        .hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/records/patient/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/records/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/records").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/records/*").hasRole("DOCTOR")

                        .requestMatchers(HttpMethod.GET, "/api/lab-responses", "/api/lab-responses/**")
                        .hasAnyRole("ADMIN", "LAB_TECHNICIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/lab-responses/**").hasRole("LAB_TECHNICIAN")
                        .requestMatchers(HttpMethod.PATCH, "/api/lab-responses/**").hasRole("LAB_TECHNICIAN")
                        .requestMatchers(HttpMethod.POST, "/api/lab-responses/**").hasRole("LAB_TECHNICIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lab-responses/**").hasRole("LAB_TECHNICIAN")

                        .anyRequest().permitAll());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}