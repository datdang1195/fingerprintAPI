package com.ekino.team2.punctuality.service;


import com.ekino.team2.punctuality.entity.Admin;
import com.ekino.team2.punctuality.exception.Constant;
import com.ekino.team2.punctuality.exception.MyException;
import com.ekino.team2.punctuality.repository.AdminRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class GoogleService {

    private static final String GWT_CLIENT = "604360837592-11g35ivc24m9prl0cf0vs8bn3sonprsp.apps.googleusercontent.com";
    private static final String GWT_SECRET = "tCfTs5o4yAeTEd6WSHAPtQJt";
    private static final long GWT_EXPIRATION = 172800000L;

    private static Logger logger = LoggerFactory.getLogger(GoogleService.class);

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Check the token with Google server to make sure it is not expired or stolen.
     * If the token valid then retunn email from token
     *
     * @param token
     */
    public Optional<String> getGoogleAccount(final String token) {
        try {
            final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            final JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(GWT_CLIENT))
                    .build();
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                Optional<String> googleAccount = Optional.ofNullable(payload.getEmail());
                if (googleAccount.isPresent() && checkGoogleAccountValid(googleAccount.get()))
                    return googleAccount;
                else
                    throw new MyException(Constant.GOOGLE_ACCOUNT_INVALID, Constant.GOOGLE_ACCOUNT_INVALID_MSG, "");
            } else
                throw new MyException(Constant.TOKEN_ID_NULL, Constant.TOKEN_ID_NULL_MSG, "");
        } catch (MyException e) {
            throw e;
        } catch (Exception e) {
            throw new MyException(Constant.TOKEN_ERROR, Constant.TOKEN_ERROR_MSG, e.getMessage());
        }
    }

    public String generateToken(String googleEmail) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + GWT_EXPIRATION);
        return Jwts.builder()
                .setSubject(googleEmail)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, GWT_SECRET)
                .compact();
    }


    public String getEmailFromJWT(String token) {
        return Jwts.parser()
                .setSigningKey(GWT_SECRET)
                .parseClaimsJws(token)
                .getBody().getSubject();

    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(GWT_SECRET).parseClaimsJws(authToken);
        } catch (ExpiredJwtException ex) {
            throw new MyException(Constant.GOOGLE_ACCOUNT_INVALID, Constant.GOOGLE_ACCOUNT_INVALID_MSG, "");
        } catch (UnsupportedJwtException ex) {
            throw new MyException(Constant.JWT_EXPIRED, Constant.JWT_EXPIRED_MSG, "");
        } catch (IllegalArgumentException ex) {
            throw new MyException(Constant.JWT_CLAIMS_EMPTY, Constant.JWT_CLAIMS_EMPTY_MSG, "");
        } catch (Exception e) {
            throw new MyException(Constant.JWT_INVALID, Constant.JWT_INVALID_MSG, "");
        }
        return true;
    }

    public boolean checkGoogleAccountValid(String googleAccount) {
        List<Admin> admins = adminRepository.findByGoogleAccount(googleAccount);
        if (admins.isEmpty()) return false;
        return true;
    }
}
