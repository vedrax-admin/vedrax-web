package com.vedrax.math;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class Matrix {

  private String key;

  private boolean visible = true;

  private List<NVP> entries = new ArrayList<>();

  public void addEntry(NVP nvp){
    entries.add(nvp);
  }
}
