package bazer.configuration.Security;

import bazer.domain.user.security.UserCustomDetail;
import bazer.domain.user.security.UserCustomDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserCustomDetailService userCustomDetailServiceService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/usuarios/login")
                || path.equals("/usuarios/criar");
    }
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")){
            try{
                final String token = authHeader.substring(7);
                //puxa o username do token
                final String username = jwtUtil.getUsername(token);

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserCustomDetail userDetails = userCustomDetailServiceService.loadUserByUsername(username);

                    if(jwtUtil.isTokenValid(token, userDetails)){
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

            }
            catch (ExpiredJwtException e) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                response.getWriter().write("""
                {
                  "status":401,
                  "error":"TOKEN_EXPIRED",
                  "message":"Token expirado"
                }
            """);

                return;
            }
            catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                LOGGER.error("Erro: " + e.getMessage());

            }
        }


        filterChain.doFilter(request, response);
    }

}
