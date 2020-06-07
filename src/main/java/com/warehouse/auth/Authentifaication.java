package com.warehouse.auth;

import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.Model.Permission;
import com.warehouse.Model.Role;
import com.warehouse.Model.User;
import com.warehouse.Model.auth.AuthenticatedUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Authentifaication {
    private static final byte[] SECRET = Base64.getDecoder().decode("8sMANI1HtecZDv4m6d1Ax0SxoQI4kYwLylkfvuBL3CQ=");
    private static final String STORAGE_TOKEN_PREFIX = "MID_STORAGE";

    public static Optional<List<Permission>> authentificate(String jwt) {
        try {
            if (jwt.startsWith(STORAGE_TOKEN_PREFIX)) {
                jwt = jwt.replace(STORAGE_TOKEN_PREFIX, "");
                Jws<Claims> result = Jwts
                        .parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(jwt);
                Long userId = result.getBody().get("id", Long.class);
                try {
                    return PermissionDAO.getInstance().getUsersPermissions(userId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                //TODO add exception throwing
                System.out.println("Bad token prefix");
            }
        } catch (ExpiredJwtException e) {
            System.out.println("Token is expired");
        }

        return Optional.of(new ArrayList<>());
    }

    public static Optional<AuthenticatedUserDTO> generateJWTToken(User user) {
        Instant now = Instant.now();
        try {
            Optional<Role> role = RoleDAO.getInstance().getUserRole(user.getId());
            Optional<List<Permission>> permissions = PermissionDAO.getInstance().getUsersPermissions(user.getId());
            if (role.isPresent() && permissions.isPresent()) {
                return Optional.of(new AuthenticatedUserDTO(
                        user.getId(),
                        user.getName(),
                        STORAGE_TOKEN_PREFIX + Jwts.builder()
                                .setSubject(user.getName())
                                .claim("id", user.getId())
                                .setIssuedAt(Date.from(now))
                                .setExpiration(Date.from(now.minus(1, ChronoUnit.MINUTES)))
                                .signWith(Keys.hmacShaKeyFor(SECRET))
                                .compact(),
                        role.get().getName(),
                        permissions.get().stream().map(a -> a.getName()).collect(Collectors.toList()))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
