package com.vedrax.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.vedrax.util.DateUtils.*;

/**
 * Service for creating security token (JWT)
 *
 * @author remypenchenat
 */
public class TokenUtility {

  private static final String ISSUER = "vedrax.com";
  private static final String JWT_SECRET = "dfg54rt&gtrt$53df@1fgAS";

  /**
   * Parse the provided JWT
   *
   * @param token the JWT to be parsed
   * @return an optional {@link UserPrincipal}
   */
  public static Optional<UserPrincipal> parseToken(String token) {
    Validate.notNull(token, "token must be provided");

    try {
      Claims body = Jwts.parser()
        .setSigningKey(JWT_SECRET)
        .requireIssuer(ISSUER)
        .parseClaimsJws(token)
        .getBody();

      String username = body.getSubject();
      String fullName = (String) body.get(UserPrincipal.FULL_NAME);
      String role = (String) body.get(UserPrincipal.ROLE);

      return Optional.of(new UserPrincipal(username, fullName, role));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Creates an expiring JWT - 24 hours
   *
   * @param user the {@link UserPrincipal}
   * @return a security token
   */
  public static String createToken(final UserPrincipal user) {
    Validate.notNull(user, "user should not be null");

    return initToken(user, 86400);
  }

  /**
   * Only for task queue
   *
   * @return
   */
  public static String getAdminToken() {
    return initToken(new UserPrincipal("SYSTEM", "SYS", "ADMIN"), 0);
  }

  private static String initToken(final UserPrincipal user, int nbOfSeconds) {
    LocalDateTime now = LocalDateTime.now();
    Claims claims = Jwts
      .claims()
      .setIssuer(ISSUER)
      .setIssuedAt(convertToDateTime(now));

    if (nbOfSeconds > 0) {
      LocalDateTime issuedAt = convertToLocalDateTime(claims.getIssuedAt());
      LocalDateTime expiresAt = addUnitToLocalDateTime(issuedAt, nbOfSeconds, ChronoUnit.SECONDS);
      claims.setExpiration(convertToDateTime(expiresAt));
    }

    claims.setSubject(user.getUsername());
    claims.put(UserPrincipal.FULL_NAME, user.getFullName());
    claims.put(UserPrincipal.ROLE, user.getRole());

    return Jwts.builder()
      .setClaims(claims)
      .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
      .compact();
  }

}
