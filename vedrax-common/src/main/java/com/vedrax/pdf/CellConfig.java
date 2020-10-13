package com.vedrax.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CellConfig {
    private Font font;
    private String text;
    private int horizontalAlignment;
    private int verticalAlignment;
    private BaseColor backgroundColor;
}
