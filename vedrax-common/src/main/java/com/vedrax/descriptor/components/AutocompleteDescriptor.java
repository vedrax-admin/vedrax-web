package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vedrax.descriptor.lov.NVP;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutocompleteDescriptor {

    private String endpoint;
    private String displayKey;
    private List<NVP> defaultParams;
    private List<FormControlDescriptor> filters;

    public AutocompleteDescriptor() {
        this.defaultParams = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    public void addFilter(FormControlDescriptor control){
        this.filters.add(control);
    }
}
