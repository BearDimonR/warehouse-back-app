package com.warehouse.auth;

import com.warehouse.Model.Token;
import com.warehouse.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;


public class Authentifaication {
    private static byte[] byteKey = Base64.getDecoder().decode("8sMANI1HtecZDv4m6d1Ax0SxoQI4kYwLylkfvuBL3CQ=");

    public static void authentificate(String jwt) {
        Jws<Claims> result = Jwts
                .parser()
                .setSigningKey(byteKey)
                .parseClaimsJws(jwt);
        result.getBody().get("id", Long.class);
        System.out.println(result);

    }

    public static Optional<Token> generateJWTToken(User user) {
        try {
            Instant now = Instant.now();
            return Optional.of(
                    new Token(Jwts.builder()
                    .setSubject(user.getName())
                    .claim("id", user.getId())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(1, ChronoUnit.MINUTES)))
                    .signWith(Keys.hmacShaKeyFor(byteKey))
                    .compact()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
