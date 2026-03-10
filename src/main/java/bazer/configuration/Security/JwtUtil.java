package bazer.configuration.Security;


import bazer.domain.user.security.UserCustomDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private SecretKey signingKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        byte[] keyBites = Decoders.BASE64.decode(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBites);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
    }


    public String GenerateToken(Authentication authentication){
        String username = authentication.getName();

        String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder().setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token){
        return getClaims(token).getSubject();
    }

    public List<String> getRoles(String token){
        String roles = getClaims(token).get("roles",String.class);
        return roles != null ? Arrays.asList(roles.split(",")) : Collections.emptyList();
    }

    public boolean isTokenValid(String token, UserCustomDetail userCustomDetail){
        try {
            Claims claims = getClaims(token);
            String username = claims.getSubject();
            return username.equals(userCustomDetail.getUsername()) && !isExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isExpired(Claims claims){
        return claims
                .getExpiration()
                .before(new Date());
    }

    public Claims getClaims(String token){
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

}
