package com.vedrax.pdf;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressFormat {

    private String companyName;
    private String address;
    private String zip;
    private String city;
}
