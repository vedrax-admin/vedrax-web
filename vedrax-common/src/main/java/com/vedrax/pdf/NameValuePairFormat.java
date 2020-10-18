package com.vedrax.pdf;

import com.vedrax.util.CellItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NameValuePairFormat {

    private List<CellItem> values;
    int horizontalAlignment;
}
