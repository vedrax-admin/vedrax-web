package com.vedrax.descriptor;

import com.vedrax.descriptor.annotations.Lov;
import com.vedrax.descriptor.annotations.Properties;
import com.vedrax.descriptor.annotations.Property;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UserCreateDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Properties(properties = @Property(propertyName = "type", propertyValue = "password"))
    private String password;

    @NotNull
    private String fullName;

    @NotNull
    @Lov(enumType = UserRole.class)
    private UserRole userRole;
}
