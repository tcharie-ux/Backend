package tg.ipnet.greenback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import tg.ipnet.greenback.security.jwt.AuthEntryPointJwt;
import tg.ipnet.greenback.security.jwt.AuthTokenFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthEntryPointJwt unauthorizedHandler,
            AuthTokenFilter authenticationFilter,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        unauthorizedHandler.commence(request, response, authException);
                    });
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/health", "/api/auth/login", "/api/auth/register", "/api/v1/login", "/api/v1/register", "/api/v1/register-architect", "/api/v1/verify-code", "/api/v1/resend-verification-code", "/error", "/csrf", "/resources/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/import/**", "/etats/**", "/datasource/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/notifications/invitations/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/notifications/invitations/*/accept").permitAll()
                        .requestMatchers("/api/v1/role", "/api/v1/history").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/invite-architect").hasAnyRole("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/notifications").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/notifications/received").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/notifications/sent").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/notifications/*/accept").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects").hasAnyRole("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/sketches").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/sketches").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/sketches/*").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/models-2d").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/models-2d").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/models-2d/*").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/models-2d/*/models-3d").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/models-2d/*/models-3d").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/change_password/*").hasAnyRole("ADMIN", "ARCHITECTE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*").hasAnyRole("ADMIN", "ARCHITECTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; font-src 'self'")
                        )
                );

        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
