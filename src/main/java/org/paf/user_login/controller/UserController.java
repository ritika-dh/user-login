package org.paf.user_login.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import org.paf.user_login.dto.AuthRequest;
import org.paf.user_login.dto.AuthResponse;
import org.paf.user_login.exception.RefreshTokenExpiredException;
import org.paf.user_login.model.RefreshToken;
import org.paf.user_login.service.JwtService;
import org.paf.user_login.service.RefreshTokenService;
import org.paf.user_login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest authRequest){
        System.out.println(authRequest.getUserName() + "   " + authRequest.getPassword());
        userService.register(authRequest.getUserName(), authRequest.getPassword());
        return new ResponseEntity<>("created", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody AuthRequest authRequest){
        AuthResponse response = null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            String token = jwtService.generateToken(authRequest.getUserName());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUserName());
            response = new AuthResponse(token, refreshToken.getToken());
        } else {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<String> refreshToken(@RequestBody String refreshToken){
        try {
            return new ResponseEntity<>(refreshTokenService.refreshToken(refreshToken), HttpStatus.OK);
        } catch (RefreshTokenExpiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/revoketoken")
    public ResponseEntity<String> revokeToken(@RequestBody String token){
        try {
            String userName = jwtService.extractUsernameFromToken(token);
            if(StringUtils.isNotBlank(userName))
                refreshTokenService.deleteTokenForUser(userName);
        } catch (ExpiredJwtException e){
            return new ResponseEntity<>("token already expired", HttpStatus.OK);
        }
        jwtService.revokeAccessToken(token);
        return new ResponseEntity<>("revoked", HttpStatus.OK);
    }

}
