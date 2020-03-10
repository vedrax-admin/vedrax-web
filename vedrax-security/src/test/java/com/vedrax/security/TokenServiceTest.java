package com.vedrax.security;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenServiceTest {

  private TokenService tokenService;

  @Before
  public void setUp() {
    tokenService = new TokenServiceImpl();
  }

  @Test
  public void givenPrincipal_whenCreateToken_thenReturnJWT(){
    UserPrincipal userPrincipal = new UserPrincipal("finance@vedrax.com","Remy Penchenat","ADMIN");

    String token = tokenService.createToken(userPrincipal);

    assertThat(token).isNotNull();

    Optional<UserPrincipal> returnPrincipal = tokenService.parseToken(token);

    assertThat(returnPrincipal.isPresent()).isTrue();
  }

  @Test
  public void givenInvalidToken_whenParseToken_thenReturnNoJWT(){

    Optional<UserPrincipal> returnPrincipal = tokenService.parseToken("invalid");

    assertThat(returnPrincipal.isPresent()).isFalse();
  }

  @Test(expected = NullPointerException.class)
  public void givenNoToken_whenParseToken_thenThrowsException() {
    tokenService.parseToken(null);
  }



  @Test(expected = NullPointerException.class)
  public void givenNoPrincipal_whenCreateToken_thenThrowsException() {
    tokenService.createToken(null);
  }

}
