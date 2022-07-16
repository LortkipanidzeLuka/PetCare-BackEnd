package ge.edu.freeuni.petcarebackend.security;

import ge.edu.freeuni.petcarebackend.security.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtUtil;

    private final UserDetailsService userDetailsService;

    @Value("${server.servlet.context-path}")
    private String SERVER_PREFIX;

    public AuthenticationFilter(JwtTokenService jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        String username = null;
        if (token != null) {
            token = token.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.extractIsVerified(token) || isVerifyRequest(request, token)) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().equals(SERVER_PREFIX + "/swagger/**") && request.getMethod().equals("GET");
    }

    private boolean isVerifyRequest(HttpServletRequest request, String token) {
        return !jwtUtil.extractIsVerified(token) &&
                (
                        (request.getRequestURI().equals("%s/auth/verify".formatted(SERVER_PREFIX)) && request.getMethod().equals("POST")) ||
                                (request.getRequestURI().equals("%s/auth/verify/resend".formatted(SERVER_PREFIX)) && request.getMethod().equals("POST"))
                );
    }
}
