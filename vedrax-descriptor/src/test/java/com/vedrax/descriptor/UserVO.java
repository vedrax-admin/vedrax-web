package com.vedrax.descriptor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserVO {

  private String email;
  private String fullName;
  private UserRole userRole;
  private String createdBy;
  private Date createdDate;
  private String modifiedBy;
  private Date modifiedDate;
}
