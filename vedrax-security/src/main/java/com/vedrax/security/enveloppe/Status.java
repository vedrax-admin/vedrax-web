package com.vedrax.security.enveloppe;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represents the api response status
 */
@Data
@NoArgsConstructor
public class Status {

  private boolean error;
  private int code;
  private String type;
  private String message;
}
