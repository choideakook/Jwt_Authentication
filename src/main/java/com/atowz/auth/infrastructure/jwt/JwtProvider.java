package com.atowz.auth.infrastructure.jwt;

import com.atowz.global.exception.jwt.InvalidJwtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.atowz.global.exception.ui.ErrorStatus.JWT_INVALID;
import static com.atowz.global.exception.ui.ErrorStatus.JSON_DOSE_NOT_SUPPORT;

@Component
public class JwtProvider {

    private SecretKey cachedSecretKey;

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    private SecretKey createSecretKey() {
        String ketBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(ketBase64Encoded.getBytes());
    }

    public SecretKey getSecretKey() {
        if (cachedSecretKey == null) {
            cachedSecretKey = createSecretKey();
        }
        return cachedSecretKey;
    }

    public String getToken(Map<String, Object> claims, long seconds) {
        long now = new Date().getTime();
        Date tokenExpireIn = new Date(now + 1000L + seconds);

        return Jwts.builder()
                .claim("body", jsonToStr(claims))
                .setExpiration(tokenExpireIn)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Map<String, Object> getClaims(String token) {
        try {
            String body = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("body", String.class);

            return strToMap(body);

        } catch (Exception e) {
            throw new InvalidJwtException(JWT_INVALID);
        }
    }

    private Object jsonToStr(Map<String, Object> claims) {
        try {
            return new ObjectMapper().writeValueAsString(claims);
        } catch (JsonProcessingException e) {
            throw new InvalidJwtException(JSON_DOSE_NOT_SUPPORT);
        }
    }

        private Map<String, Object> strToMap(String jsonStr) {
        try {
            return new ObjectMapper().readValue(jsonStr, LinkedHashMap.class);
        } catch (JsonProcessingException e) {
            throw new InvalidJwtException(JWT_INVALID);
        }
    }
}
