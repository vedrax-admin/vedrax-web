package com.vedrax.descriptor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentEquipmentVO {

  private Long id;
  private String designation;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date controlDate;
  private Integer periodicityValue;
  private Boolean active;
}
