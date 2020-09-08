package com.vedrax.descriptor.annotations;

import com.vedrax.descriptor.lov.EnumWithValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Lov {
  Class<? extends EnumWithValue> enumType();
}
