package com.vedrax.descriptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentEquipmentDto {

  @NotNull
  private String designation;

  @DateTimeFormat
  private Date controlDate;

  @Min(0)
  private Integer periodicityValue;

  @Size(max = 500)
  private String comment;

  @NotNull
  private Boolean withUncertainty;

  @NotNull
  private Boolean active;

}
