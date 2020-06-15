package com.warehouse.Authentication;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.PermissionDAO;
import com.warehouse.DAO.RoleDAO;
import com.warehouse.Exception.AuthWrongException;
import com.warehouse.Model.*;
import com.warehouse.Exception.AuthRequiredException;
import com.warehouse.Exception.NoPermissionException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class Authentication {
    private static final byte[] SECRET = Base64.getDecoder().decode("8sMANI1HtecZDv4m6d1Ax0SxoQI4kYwLylkfvuBL3CQ=");
    private static final byte[] RENOVATION_SECRET = Base64.getDecoder().decode("OJcrU882amYKYerAnwr9HFtN2aftwSNHzLxNZyygaws=");
    private static final String STORAGE_TOKEN_PREFIX = "MID_STORAGE";
    private static final String RENOVATION_STORAGE_TOKEN_PREFIX = "MID_RENOVATION_STORAGE";

    private static Logger logger = LogManager.getLogger(Authentication.class);

    public static Optional<List<String>> getUserPermissions(HttpExchange exchange) throws SQLException, AuthRequiredException {
        try {
            Optional<List<Permission>> permissions = PermissionDAO.getInstance().getUsersPermissions((Long) parseToken(extractToken(exchange), SECRET).get("id"));
            return (permissions.isPresent()) ? Optional.of(permissions.get().stream().map(a -> a.getName()).collect(Collectors.toList())) : Optional.empty();
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
        throw new NoPermissionException("User do not have permission '" + permission + "'");
    }

    public static boolean hasPermissions(HttpExchange exchange, List<String> permissionsToCheck) throws SQLException, AuthRequiredException, NoPermissionException {
        Optional<List<String>> userPermissions = Authentication.getUserPermissions(exchange);
        if (userPermissions.isPresent()) {
            if (permissionsToCheck.stream().filter(a -> !userPermissions.get().contains(a)).count() != 0) return false;
            return true;
        }
        throw new NoPermissionException("User do not have permissions: '" + permissionsToCheck.stream().collect(Collectors.joining(" , ")));
    }

    public static Optional<AuthenticatedUserDTO> generateLoginResponse(User user) throws SQLException {
        Instant now = Instant.now();
        Optional<List<Permission>> permissions = PermissionDAO.getInstance().getUsersPermissions(user.getId());
        Optional<Role> role = RoleDAO.getInstance().getUserRole(user.getId());
        if (role.isPresent()&&permissions.isPresent()) {
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
                    RENOVATION_STORAGE_TOKEN_PREFIX + Jwts.builder()
                            .setSubject(user.getName())
                            .claim("id", user.getId())
                            .setIssuedAt(Date.from(now))
                            .setExpiration(Date.from(now.plus(1, ChronoUnit.SECONDS)))
                            .signWith(Keys.hmacShaKeyFor(RENOVATION_SECRET))
                            .compact(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(now.plus(60, ChronoUnit.MINUTES)))
                    ,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(now.plus(1, ChronoUnit.SECONDS)))
                    ,
                    user.getRoleId(),
                    role.get().isSuper(),
                    permissions.get().stream().map(a -> a.getName()).filter(a->!a.endsWith("read")).collect(Collectors.toList()),
                    permissions.get().stream().map(a -> a.getName()).filter(a->a.endsWith("read")).collect(Collectors.toList())
                    )
            );
        }
        return Optional.empty();
    }

    public static Optional<TokenRenovationDTO> generateRenovationResponse(String token) throws AuthWrongException {
        Instant now = Instant.now();
        if (token.startsWith(RENOVATION_STORAGE_TOKEN_PREFIX)) {
            token = token.replace(RENOVATION_STORAGE_TOKEN_PREFIX, "");
            Map<String, Object> tokenParams = parseToken(token, RENOVATION_SECRET);
            return Optional.of(new TokenRenovationDTO(
                    STORAGE_TOKEN_PREFIX + Jwts.builder()
                            .setSubject((String) tokenParams.get("name"))
                            .claim("id", (Long) tokenParams.get("id"))
                            .setIssuedAt(Date.from(now))
                            .setExpiration(Date.from(now.plus(60, ChronoUnit.MINUTES)))
                            .signWith(Keys.hmacShaKeyFor(SECRET))
                            .compact(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(now.plus(60, ChronoUnit.MINUTES)))
            ));
        } else {
            logger.warn("Bad token prefix: " + token);
            throw new AuthWrongException("Bad token prefix");
        }
    }

    private static String extractToken(HttpExchange exchange) throws AuthRequiredException {
        if (exchange.getRequestHeaders().get("Authorization") == null)
            throw new AuthRequiredException();
        String jwt = exchange.getRequestHeaders().get("Authorization").get(0).replace("Bearer ", "");
        if (jwt.startsWith(STORAGE_TOKEN_PREFIX)) {
            return jwt.replace(STORAGE_TOKEN_PREFIX, "");
        } else {
            logger.warn("Bad token prefix: " + jwt);
            throw new AuthRequiredException("Bad token prefix");
        }
    }

    private static Map<String, Object> parseToken(String token, byte[] secret) {
        try{
            Map<String, Object> tokenParams = new HashMap<>();
            Jws<Claims> result = Jwts
                    .parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            tokenParams.put("id", result.getBody().get("id", Long.class));
            tokenParams.put("name", result.getBody().get("sub", String.class));
            return tokenParams;
        }catch (Exception e){
            e.printStackTrace();
        }
      return new HashMap<String, Object>();
    }
}
