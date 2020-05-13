package com.vedrax.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.vedrax.security.util.DateUtil.*;

/**
 * Service for creating security token (JWT)
 *
 * @author remypenchenat
 */
@Service
public class TokenServiceImpl implements TokenService {

  private static final String ISSUER = "vedrax.com";
  private static final String JWT_SECRET = "dfg54rt&gtrt$53df@1fgAS";

  /**
   * Parse the provided JWT
   *
   * @param token the JWT to be parsed
   * @return an optional {@link UserPrincipal}
   */
  @Override
  public Optional<UserPrincipal> parseToken(String token) {
    Validate.notNull(token, "token must be provided");

    try {
      Claims body = getClaimsWithToken(token);
      UserPrincipal userPrincipal = claimsToPrincipal(body);
      return Optional.of(userPrincipal);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Get {@link Claims} with the provided JWT
   *
   * @param token the security token
   * @return the claims
   */
  private Claims getClaimsWithToken(String token) {
    return Jwts.parser()
      .setSigningKey(JWT_SECRET)
      .requireIssuer(ISSUER)
      .parseClaimsJws(token)
      .getBody();
  }

  /**
   * Get {@link UserPrincipal} with the provided {@link Claims}
   *
   * @param body the claims
   * @return the user principal
   */
  private UserPrincipal claimsToPrincipal(Claims body) {
    String username = body.getSubject();
    String fullName = (String) body.get(UserPrincipal.FULL_NAME);
    String role = (String) body.get(UserPrincipal.ROLE);

    return new UserPrincipal(username, fullName, role);
  }

  /**
   * Creates an expiring JWT - 24 hours
   *
   * @param user the {@link UserPrincipal}
   * @return a security token
   */
  public String createToken(final UserPrincipal user) {
    Validate.notNull(user, "user should not be null");

    return initToken(user, 86400);
  }

  /**
   * Only for task queue
   *
   * @return
   */
  public String getAdminToken() {
    return initToken(new UserPrincipal("SYSTEM", "SYS", "ADMIN"), 0);
  }

  private String initToken(final UserPrincipal user, int nbOfSeconds) {
    Claims claims = setClaims();
    setDuration(claims, 86400);
    setAttributes(claims, user);

    return Jwts.builder()
      .setClaims(claims)
      .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
      .compact();
  }

  /**
   * Set a new @{link Claims}
   *
   * @return a claims
   */
  private Claims setClaims() {
    LocalDateTime now = LocalDateTime.now();
    return Jwts
      .claims()
      .setIssuer(ISSUER)
      .setIssuedAt(convertToDateTime(now));
  }

  /**
   * Set the expiration time
   *
   * @param claims      the @{link Claims}
   * @param nbOfSeconds the nb of seconds when expired
   */
  private void setDuration(Claims claims, int nbOfSeconds) {
    if (nbOfSeconds > 0) {
      LocalDateTime issuedAt = convertToLocalDateTime(claims.getIssuedAt());
      LocalDateTime expiresAt = addUnitToLocalDateTime(issuedAt, nbOfSeconds, ChronoUnit.SECONDS);
      claims.setExpiration(convertToDateTime(expiresAt));
    }
  }

  /**
   * Set the attributes of the provided {@link Claims} with the
   * {@link UserPrincipal}
   *
   * @param claims the @{link Claims}
   * @param user   the {@link UserPrincipal}
   */
  private void setAttributes(Claims claims, UserPrincipal user) {
    claims.setSubject(user.getUsername());
    claims.put(UserPrincipal.FULL_NAME, user.getFullName());
    claims.put(UserPrincipal.ROLE, user.getRole());
  }

}
