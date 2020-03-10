package com.vedrax.descriptor;

import com.vedrax.descriptor.annotations.*;
import com.vedrax.descriptor.annotations.Properties;
import com.vedrax.descriptor.components.*;
import com.vedrax.descriptor.enums.ValidationType;
import com.vedrax.descriptor.lov.EnumWithValue;
import com.vedrax.descriptor.lov.NVP;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import static com.vedrax.descriptor.typeof.TypeOf.whenTypeOf;
import static com.vedrax.descriptor.util.DateUtil.dateToISO;

@Service
public class FormGeneratorImpl implements FormGenerator {

  private final MessageSource messageSource;

  public FormGeneratorImpl(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public FormDescriptor generate(FormDto formDto, Locale locale) {
    Validate.notNull(formDto, "formDto must be provided");
    Validate.notNull(formDto.getDto(), "dto class must be provided");
    Validate.notNull(formDto.getEndpoint(), "endpoint must be provided");
    Validate.notNull(locale, "locale must be provided");

    String method = formDto.getMethod() == null ? "POST" : formDto.getMethod();

    FormDescriptor formDescriptor = getFormDescriptor(formDto.getDto());
    List<FormControlDescriptor> controls = initListOfControlsFromClass(formDto.getDto(), formDto.getSource(), locale);
    formDescriptor.setControls(controls);
    formDescriptor.setMethod(method);
    formDescriptor.setEndpoint(formDto.getEndpoint());

    addAuditInformation(formDto.getSource(), formDescriptor);

    return formDescriptor;
  }

  private void addAuditInformation(Object source, FormDescriptor formDescriptor) {
    if (source == null) {
      return;
    }

    AuditDescriptor auditDescriptor = new AuditDescriptor();
    auditDescriptor.setCreatedDate(getDateField(source, "createdDate"));
    auditDescriptor.setCreatedBy(getStringField(source, "createdBy"));
    auditDescriptor.setModifiedDate(getDateField(source, "modifiedDate"));
    auditDescriptor.setModifiedBy(getStringField(source, "modifiedBy"));

    formDescriptor.setAudit(auditDescriptor);
  }

  private FormDescriptor getFormDescriptor(Class<?> dto) {
    Validate.notNull(dto, "dto class must be provided");

    FormDescriptor formDescriptor = new FormDescriptor();
    initGroupsFromAnnotation(dto, formDescriptor);
    return formDescriptor;
  }

  private void initGroupsFromAnnotation(Class<?> sourceClass, FormDescriptor formDescriptor) {
    Optional<Groups> optionalGroups = getGroupsAnnotation(sourceClass);
    if (optionalGroups.isPresent()) {
      for (Group group : optionalGroups.get().groups()) {
        FormGroupDescriptor formGroupDescriptor = createGroup(group);
        formDescriptor.addGroup(formGroupDescriptor);
      }
    }
  }

  private Optional<Groups> getGroupsAnnotation(Class<?> dtoClass) {
    Groups annotation = dtoClass.getDeclaredAnnotation(Groups.class);
    return Optional.ofNullable(annotation);
  }

  private FormGroupDescriptor createGroup(Group group) {
    FormGroupDescriptor formGroupDescriptor = new FormGroupDescriptor();
    formGroupDescriptor.setName(group.name());
    formGroupDescriptor.setIds(Arrays.asList(group.properties()));
    return formGroupDescriptor;
  }

  private List<FormControlDescriptor> initListOfControlsFromClass(Class<?> sourceClass,
                                                                  Object source,
                                                                  Locale locale) {

    List<FormControlDescriptor> controls = new ArrayList<>();

    String packageName = getPackageNameFromClass(sourceClass);
    Field[] fields = FieldUtils.getAllFields(sourceClass);

    boolean hasSource = source != null;

    for (Field field : fields) {

      FormControlDescriptor formControlDescriptor = generateFormControlWithAttribute(packageName, field, hasSource, locale);
      if (formControlDescriptor != null) {
        setControlValue(source, formControlDescriptor);
        controls.add(formControlDescriptor);
      }
    }

    return controls;
  }

  private FormControlDescriptor generateFormControlWithAttribute(String packageName, Field field, boolean onUpdate, Locale locale) {

    if (checkIfAttributeShouldBeExcluded(field, onUpdate)) {
      return null;
    }

    return generateFormControlWithAttribute(packageName, field, locale);
  }

  private boolean checkIfAttributeShouldBeExcluded(Field field, boolean onUpdate) {
    return onUpdate && field.isAnnotationPresent(Null.class);
  }

  private FormControlDescriptor generateFormControlWithAttribute(String packageName, Field field, Locale locale) {
    FormControlDescriptor formControlDescriptor = new FormControlDescriptor();

    String controlKey = packageName + "." + field.getName();

    formControlDescriptor.setControlName(field.getName());
    formControlDescriptor.setControlLabel(getMessageFromKey(controlKey + ".label", null, locale));
    formControlDescriptor.setControlHint(getMessageFromKey(controlKey + ".hint", null, locale));
    initControlWithType(field, formControlDescriptor);
    initControlWithAnnotations(field, formControlDescriptor, packageName, locale);
    return formControlDescriptor;
  }

  private void setControlValue(Object entity, FormControlDescriptor control) {

    //in case entity is not found
    if (entity == null) {
      return;
    }

    String controlName = control.getControlName();

    Optional<Object> fieldOpt = getField(entity, controlName);

    if (fieldOpt.isPresent()) {

      Object field = fieldOpt.get();

      if (field instanceof Date) {
        control.setControlValue(dateToISO((Date) field));
      } else {
        control.setControlValue(field);
      }

    }
  }

  private Optional<Object> getField(Object source, String attributeName) {
    try {
      return Optional.of(FieldUtils.readDeclaredField(source, attributeName, true));
    } catch (IllegalAccessException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private Date getDateField(Object source, String attributeName) {

    Optional<Object> fieldOpt = getField(source, attributeName);

    if (fieldOpt.isPresent()) {

      Object field = fieldOpt.get();

      if (field instanceof Date) {
        return (Date) field;
      } else {
        throw new IllegalArgumentException(attributeName + " is not a date attribute");
      }

    }

    return null;

  }

  private String getStringField(Object source, String attributeName) {

    Optional<Object> fieldOpt = getField(source, attributeName);

    if (fieldOpt.isPresent()) {

      Object field = fieldOpt.get();

      if (field instanceof String) {
        return (String) field;
      } else {
        throw new IllegalArgumentException(attributeName + " is not a string attribute");
      }

    }

    return null;

  }

  private void initControlWithType(Field field, FormControlDescriptor formControlDescriptor) {

    Class<?> type = field.getType();

    if (Date.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType("datepicker");
    } else if (Integer.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType("slider");
    } else if (Boolean.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType("checkbox");
    } else {
      formControlDescriptor.setControlType("input");
    }

  }

  private void initControlWithAnnotations(Field field,
                                          FormControlDescriptor formControlDescriptor,
                                          String packageName,
                                          Locale locale) {

    for (Annotation annotation : field.getDeclaredAnnotations()) {

      whenTypeOf(annotation)
        .is(Properties.class).then(properties -> fromListOfProperties(properties, formControlDescriptor))
        .is(Component.class).then(component -> fromComponent(component, formControlDescriptor))
        .is(Children.class).then(children -> fromChildren(children, locale, formControlDescriptor))
        .is(Lov.class).then(lov -> fromEnums(lov, locale, formControlDescriptor))
        .is(NotNull.class).then(validation -> fromValidation(ValidationType.required, true, packageName, locale, formControlDescriptor))
        .is(Size.class).then(validation -> fromSizeValidation(validation, packageName, locale, formControlDescriptor))
        .is(Email.class).then(validation -> fromValidation(ValidationType.email, true, packageName, locale, formControlDescriptor))
        .is(Min.class).then(validation -> fromValidation(ValidationType.min, validation.value(), packageName, locale, formControlDescriptor))
        .is(Max.class).then(validation -> fromValidation(ValidationType.max, validation.value(), packageName, locale, formControlDescriptor))
        .is(NotEmpty.class).then(validation -> fromValidation(ValidationType.required, true, packageName, locale, formControlDescriptor))
        .is(NotBlank.class).then(validation -> fromValidation(ValidationType.required, true, packageName, locale, formControlDescriptor))
        .is(Pattern.class).then(validation -> fromValidation(ValidationType.pattern, validation.regexp(), packageName, locale, formControlDescriptor));
    }

  }

  private void fromEnums(Lov lov, Locale locale, FormControlDescriptor formControlDescriptor) {
    generateLOVWithEnums(lov.enumType(), formControlDescriptor, locale);
  }

  private void generateLOVWithEnums(Class<? extends EnumWithValue> enumType,
                                    FormControlDescriptor formControlDescriptor, Locale locale) {

    List<NVP> nvpList = new ArrayList<>();

    String className = enumType.getName();

    EnumWithValue[] values = enumType.getEnumConstants();

    for (EnumWithValue enumWithValue : values) {
      String key = "" + enumWithValue;

      NVP nvp = new NVP();
      nvp.setKey(key);
      nvp.setValue(getMessageFromKey(className + "." + key, null, locale));
      nvpList.add(nvp);
    }

    formControlDescriptor.setControlOptions(nvpList);
    formControlDescriptor.setControlType("select");

  }

  private String getPackageNameFromClass(Class<?> clazz) {
    return clazz.getPackage().getName();
  }

  private void fromChildren(Children children, Locale locale, FormControlDescriptor formControlDescriptor) {
    List<FormControlDescriptor> controls = initListOfControlsFromClass(children.type(), null, locale);
    formControlDescriptor.setControlChildren(controls);
    formControlDescriptor.setControlType("arrayOfControls");
  }

  private void fromComponent(Component component, FormControlDescriptor formControlDescriptor) {
    //override the component type by default
    formControlDescriptor.setControlType(component.type());
  }

  private void fromSizeValidation(Size sizeValidation, String packageName, Locale locale, FormControlDescriptor formControlDescriptor) {
    fromValidation(ValidationType.maxlength, sizeValidation.max(), packageName, locale, formControlDescriptor);
    fromValidation(ValidationType.minlength, sizeValidation.min(), packageName, locale, formControlDescriptor);
  }

  private void fromValidation(ValidationType validationType,
                              Object value,
                              String packageName,
                              Locale locale,
                              FormControlDescriptor formControlDescriptor) {

    String messageKey = packageName + "." + formControlDescriptor.getControlName() + "." + validationType;
    ValidationDescriptor validationDescriptor = new ValidationDescriptor();
    validationDescriptor.setValidationName(validationType.getName());
    validationDescriptor.setValidationValue(value);
    if (value != null) {
      validationDescriptor.setValidationMessage(getMessageFromKey(messageKey, new Object[]{value}, locale));
    } else {
      validationDescriptor.setValidationMessage(getMessageFromKey(messageKey, null, locale));
    }
    formControlDescriptor.addValidation(validationDescriptor);
  }

  private void fromListOfProperties(Properties listOfProperties, FormControlDescriptor formControlDescriptor) {
    for (Property property : listOfProperties.properties()) {
      PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
      propertyDescriptor.setPropertyName(property.propertyName());
      propertyDescriptor.setPropertyValue(property.propertyValue());
      formControlDescriptor.addProperty(propertyDescriptor);
    }
  }

  private String getMessageFromKey(String key, Object[] params, Locale locale) {
    try {
      return messageSource.getMessage(key, params, locale);
    } catch (Exception ex) {
      return null;
    }
  }

}
