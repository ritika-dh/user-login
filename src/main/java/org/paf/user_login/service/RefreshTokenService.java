package org.paf.user_login.service;

import jakarta.transaction.Transactional;
import org.paf.user_login.exception.RefreshTokenExpiredException;
import org.paf.user_login.model.RefreshToken;
import org.paf.user_login.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Value("${refresh.token.expirytime.sec}")
    private Long refreshTokenExpiryTime;

    public RefreshToken createRefreshToken(String username){
        RefreshToken refreshToken = RefreshToken.builder()
                .userName(username)
                .token(UUID.randomUUID().toString())
                .expiryTime(Instant.now().plusSeconds(refreshTokenExpiryTime))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public String refreshToken(String refreshTokenString) throws RefreshTokenExpiredException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString).orElseThrow(() -> new RefreshTokenExpiredException());
        if(isExpired(refreshToken)){
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException();
        }
        return jwtService.generateToken(refreshToken.getUserName());
    }

    private boolean isExpired(RefreshToken token) {
        System.out.println(token.getExpiryTime());
        System.out.println(Instant.now());
        return token.getExpiryTime().isBefore(Instant.now());
    }

    @Transactional
    public void deleteTokenForUser(String userName) {
        refreshTokenRepository.deleteByUserName(userName);
    }
}
