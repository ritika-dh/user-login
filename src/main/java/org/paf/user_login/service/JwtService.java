package org.paf.user_login.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtService {

    private final String secretKey = "08612A6EB79138482FF4EB2EB5C4DCDA087F9ED5E6A1BF4E35A8C899CE7234AC";
    private Map<String, Long> blacklistedTokens = new HashMap<>();
    @Value("${access.token.expirytime.millisec}")
    private long accessTokenExpiryTime;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        System.out.println(accessTokenExpiryTime);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ accessTokenExpiryTime))
                .signWith(getKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isTokenValid(String token) {
        return !blacklistedTokens.containsKey(token);
    }

    public String extractUsernameFromToken(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    private Claims extractAllClaims(String token){

        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build();
        return parser.parseClaimsJws(token).getBody();
    }
    private Key getKey(){
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void revokeAccessToken(String revokeToken) {
        Long now = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < now);
        blacklistedTokens.put(revokeToken, System.currentTimeMillis()+ accessTokenExpiryTime);
    }
}
