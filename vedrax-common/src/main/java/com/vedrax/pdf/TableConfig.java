package com.vedrax.pdf;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableConfig {

    private int numColumns;
    private int[] widths;
    private int border;
    private int horizontalAlignment;
    private int verticalAlignment;
}
