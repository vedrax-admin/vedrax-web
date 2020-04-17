package com.vedrax.descriptor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentEquipmentEntityVO {

  private Long id;
  private String designation;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date controlDate;
  private Integer periodicityValue;
  private String comment;
  private Boolean withUncertainty;
  private Boolean active;
  //audit
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private Date createdDate;
  private String createdBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private Date modifiedDate;
  private String modifiedBy;
}
