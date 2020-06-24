package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vedrax.descriptor.lov.NVP;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutocompleteDescriptor {

    private String endpoint;
    private String displayKey;
    private List<NVP> defaultParams = new ArrayList<>();
}
