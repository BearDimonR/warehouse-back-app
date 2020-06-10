package com.warehouse.Authentication;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.Model.Permission;
import com.warehouse.Model.Role;
import com.warehouse.Model.User;
import com.warehouse.Model.auth.AuthenticatedUserDTO;
import com.warehouse.exceptions.AuthRequiredException;
import com.warehouse.exceptions.NoPermissionException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.html.Option;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Authentication {
    private static final byte[] SECRET = Base64.getDecoder().decode("8sMANI1HtecZDv4m6d1Ax0SxoQI4kYwLylkfvuBL3CQ=");
    private static final String STORAGE_TOKEN_PREFIX = "MID_STORAGE";

    private static Logger logger = LogManager.getLogger(Authentication.class);

    public static Optional<List<String>> getUserPermissions(HttpExchange exchange) throws SQLException, AuthRequiredException {
        if (exchange.getRequestHeaders().get("Authorization") == null)
            throw new AuthRequiredException();
        String jwt = exchange.getRequestHeaders().get("Authorization").get(0).replace("Bearer ", "");
        try {
            if (jwt.startsWith(STORAGE_TOKEN_PREFIX)) {
                jwt = jwt.replace(STORAGE_TOKEN_PREFIX, "");
                Jws<Claims> result = Jwts
                        .parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(jwt);
                Long userId = result.getBody().get("id", Long.class);
                Optional<List<Permission>> permissions = PermissionDAO.getInstance().getUsersPermissions(userId);
                if (permissions.isPresent())
                    return Optional.of(permissions.get().stream().map(a -> a.getName()).collect(Collectors.toList()));
                return Optional.empty();
            } else {
                logger.warn("Bad token prefix: " + jwt);
                throw new AuthRequiredException("Bad token prefix");
            }
        } catch (ExpiredJwtException e) {
            logger.error("Token is expired");
            throw new AuthRequiredException("Token is expired");
        }
    }

    public static boolean hasPermission(HttpExchange exchange, String permission) throws SQLException, AuthRequiredException, NoPermissionException {
        Optional<List<String>> userPermissions = Authentication.getUserPermissions(exchange);
        if (userPermissions.isPresent() && userPermissions.get().contains(permission)) {
                return true;
            }
            throw new NoPermissionException("user do not have permission '"+permission+"'");
        }

        public static boolean hasPermissions(HttpExchange exchange, List<String> permissionsToCheck) throws SQLException, AuthRequiredException, NoPermissionException {
            Optional<List<String>> userPermissions = Authentication.getUserPermissions(exchange);
            if (userPermissions.isPresent()) {
            if (permissionsToCheck.stream().filter(a -> !userPermissions.get().contains(a)).count() != 0) return false;
            return true;
        }
        throw new NoPermissionException();
    }

    public static Optional<AuthenticatedUserDTO> generateLoginResponse(User user) throws SQLException {
        Instant now = Instant.now();
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
                            .setExpiration(Date.from(now.plus(60, ChronoUnit.MINUTES)))
                            .signWith(Keys.hmacShaKeyFor(SECRET))
                            .compact(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(now.plus(60, ChronoUnit.MINUTES)))
                    ,
                    role.get().getName(),
                    permissions.get().stream().map(a -> a.getName()).collect(Collectors.toList()))
            );
        }
        return Optional.empty();
    }
}
