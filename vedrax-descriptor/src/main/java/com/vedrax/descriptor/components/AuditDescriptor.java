package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AuditDescriptor {
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private Date createdDate;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private Date modifiedDate;
  private String createdBy;
  private String modifiedBy;
}
