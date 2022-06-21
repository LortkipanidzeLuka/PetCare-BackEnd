package ge.edu.freeuni.petcarebackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
public class SecurityAdapter extends WebSecurityConfigurerAdapter {

    @Value("${allowed.origins}")
    private String[] allowedOrigins;

    private final AuthenticationFilter filter;

    public SecurityAdapter(AuthenticationFilter filter) {
        this.filter = filter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeRequests(configurer -> configurer
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/swagger/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/auth/register", "/auth/login", "/auth/refresh").permitAll()
                        .antMatchers(HttpMethod.GET, "/advertisements/**").permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(List.of("origin", "content-type", "accept", "authorization", "content-disposition"));
        configuration.setMaxAge((long) (10 * 60));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

}