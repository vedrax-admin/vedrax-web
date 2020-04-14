package com.vedrax.descriptor;

import com.vedrax.descriptor.annotations.*;
import com.vedrax.descriptor.annotations.Properties;
import com.vedrax.descriptor.components.*;
import com.vedrax.descriptor.enums.ControlType;
import com.vedrax.descriptor.enums.ValidationType;
import com.vedrax.descriptor.lov.EnumWithValue;
import com.vedrax.descriptor.lov.NVP;
import com.vedrax.descriptor.util.DateUtil;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import static com.vedrax.descriptor.typeof.TypeOf.whenTypeOf;
import static com.vedrax.descriptor.util.DateUtil.dateToISO;

/**
 * Service used for generating form descriptor
 */
@Service
public class FormGeneratorImpl implements FormGenerator {

  private final MessageSource messageSource;

  public FormGeneratorImpl(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Method for generating the form descriptor
   *
   * @param formDto the provided class for generating the descriptor
   * @param locale  the locale
   * @return form descriptor
   */
  public FormDescriptor generate(FormDto formDto, Locale locale) {
    Validate.notNull(formDto, "formDto must be provided");
    Validate.notNull(formDto.getDto(), "dto class must be provided");
    Validate.notNull(formDto.getEndpoint(), "endpoint must be provided");
    Validate.notNull(locale, "locale must be provided");

    //POST is used by default
    String method = formDto.getMethod() == null ? "POST" : formDto.getMethod();

    FormDescriptor formDescriptor = getFormDescriptor(formDto.getDto());
    initListOfControlsFromClass(formDto.getDto(), formDto.getSource(), formDescriptor, locale);
    initFormButtonsLabel(formDescriptor, locale);
    initSuccessMessage(formDto.getSource(), formDto.getDto(), formDescriptor, locale);
    formDescriptor.setMethod(method);
    formDescriptor.setEndpoint(formDto.getEndpoint());

    return formDescriptor;
  }

  /**
   * Method for initiating a FormDescriptor and its groups
   *
   * @param dto the class to generate the descriptor
   * @return form descriptor
   */
  private FormDescriptor getFormDescriptor(Class<?> dto) {
    Validate.notNull(dto, "dto class must be provided");

    FormDescriptor formDescriptor = new FormDescriptor();
    initGroupsFromAnnotation(dto, formDescriptor);
    return formDescriptor;
  }

  /**
   * Init form buttons labels
   *
   * @param formDescriptor the form descriptor
   * @param locale         the locale
   */
  private void initFormButtonsLabel(FormDescriptor formDescriptor, Locale locale) {
    formDescriptor.setSubmitLabel(getMessageFromKey("submit.label", null, locale));
    formDescriptor.setCancelLabel(getMessageFromKey("cancel.label", null, locale));
  }

  /**
   * Method for adding success message
   *
   * @param source         flag set to true when update
   * @param dtoClass       the dto class
   * @param formDescriptor the form descriptor
   * @param locale         the locale
   */
  private void initSuccessMessage(Object source, Class<?> dtoClass, FormDescriptor formDescriptor, Locale locale) {
    String key = source != null ? "success.update.message" : "success.create.message";
    formDescriptor.setSuccessMessage(getMessageFromKey(dtoClass.getName() + "." + key, null, locale));
  }

  /**
   * Init group of components with Groups annotation
   *
   * @param sourceClass    the source which resides the data
   * @param formDescriptor the form descriptor
   */
  private void initGroupsFromAnnotation(Class<?> sourceClass, FormDescriptor formDescriptor) {
    Optional<Groups> optionalGroups = getGroupsAnnotation(sourceClass);
    if (optionalGroups.isPresent()) {
      for (Group group : optionalGroups.get().groups()) {
        FormGroupDescriptor formGroupDescriptor = createGroup(group);
        formDescriptor.addGroup(formGroupDescriptor);
      }
    }
  }

  /**
   * Method for getting Groups annotation
   *
   * @param dtoClass the dto class
   * @return the optional Groups
   */
  private Optional<Groups> getGroupsAnnotation(Class<?> dtoClass) {
    Groups annotation = dtoClass.getDeclaredAnnotation(Groups.class);
    return Optional.ofNullable(annotation);
  }

  /**
   * Method for creating a group descriptor from annotation
   *
   * @param group the group annotation
   * @return group descriptor
   */
  private FormGroupDescriptor createGroup(Group group) {
    FormGroupDescriptor formGroupDescriptor = new FormGroupDescriptor();
    formGroupDescriptor.setName(group.name());
    formGroupDescriptor.setIds(Arrays.asList(group.properties()));
    return formGroupDescriptor;
  }

  /**
   * Create the list of controls descriptors using reflection
   *
   * @param sourceClass    the dto class
   * @param source         the object with the data if any
   * @param formDescriptor the form descriptor
   * @param locale         the locale
   * @return list of controls descriptors
   */
  private List<FormControlDescriptor> initListOfControlsFromClass(Class<?> sourceClass,
                                                                  Object source,
                                                                  FormDescriptor formDescriptor,
                                                                  Locale locale) {

    List<FormControlDescriptor> controls = new ArrayList<>();

    //keep track of the controls keys
    List<String> keys = new ArrayList<>();

    String packageName = getPackageNameFromClass(sourceClass);
    Field[] fields = FieldUtils.getAllFields(sourceClass);

    boolean hasSource = source != null;

    for (Field field : fields) {

      FormControlDescriptor formControlDescriptor = generateFormControlWithAttribute(packageName, field, hasSource, locale);
      if (formControlDescriptor != null) {
        keys.add(formControlDescriptor.getControlName());
        setControlValue(source, formControlDescriptor);
        controls.add(formControlDescriptor);
      }

    }

    updateControlsWithAudit(source, controls, keys, formDescriptor, locale);

    return controls;
  }

  /**
   * Method for incorporating audit information when provided in the data class
   *
   * @param source         the data class
   * @param controls       the list of controls descriptor
   * @param keys           the controls key. Used to group the audit information in a group
   * @param formDescriptor the form descriptor
   * @param locale         the locale
   */
  private void updateControlsWithAudit(Object source,
                                       List<FormControlDescriptor> controls,
                                       List<String> keys,
                                       FormDescriptor formDescriptor,
                                       Locale locale) {

    if (formDescriptor != null) {

      List<String> auditKeys = new ArrayList<>();

      addAuditToControls(source, "createdDate", controls, auditKeys, locale);
      addAuditToControls(source, "createdBy", controls, auditKeys, locale);
      addAuditToControls(source, "modifiedDate", controls, auditKeys, locale);
      addAuditToControls(source, "modifiedBy", controls, auditKeys, locale);

      formDescriptor.setControls(controls);

      addAuditGroup(formDescriptor, keys, auditKeys);
    }

  }

  /**
   * Method for adding audit information to the list of controls
   *
   * @param source        the data source
   * @param attributeName the audit attribute name
   * @param controls      the list of controls descriptors
   * @param auditKeys     the list of audit keys
   * @param locale        the locale
   */
  private void addAuditToControls(Object source,
                                  String attributeName,
                                  List<FormControlDescriptor> controls,
                                  List<String> auditKeys,
                                  Locale locale) {

    if (source == null) {
      return;
    }

    Optional<Object> fieldOpt = getField(source, attributeName);

    if (fieldOpt.isPresent()) {

      Object field = fieldOpt.get();

      if (field instanceof Date) {
        controls.add(generateAuditControl(attributeName, DateUtil.dateToISO((Date) field), locale));
      } else {
        controls.add(generateAuditControl(attributeName, String.valueOf(field), locale));
      }

      auditKeys.add(attributeName);
    }

  }

  /**
   * Generate audit control
   *
   * @param attributeName the audit control name
   * @param value         the audit control value
   * @param locale        the provided locale
   * @return form control descriptor
   */
  private FormControlDescriptor generateAuditControl(String attributeName, String value, Locale locale) {
    FormControlDescriptor formControlDescriptor = new FormControlDescriptor();

    formControlDescriptor.setControlName(attributeName);
    formControlDescriptor.setControlLabel(getMessageFromKey(attributeName + ".label", null, locale));

    PropertyDescriptor readOnlyProperty = new PropertyDescriptor();
    readOnlyProperty.setPropertyName("readOnly");
    readOnlyProperty.setPropertyValue(true);

    formControlDescriptor.addProperty(readOnlyProperty);
    formControlDescriptor.setControlType(String.valueOf(ControlType.input));
    formControlDescriptor.setControlValue(value);

    return formControlDescriptor;
  }

  /**
   * Method for adding audit group to descriptor form
   *
   * @param formDescriptor the form descriptor
   * @param keys           the controls keys
   * @param auditKeys      the audit controls keys
   */
  private void addAuditGroup(FormDescriptor formDescriptor, List<String> keys, List<String> auditKeys) {

    if (!CollectionUtils.isEmpty(auditKeys)) {

      if (CollectionUtils.isEmpty(formDescriptor.getGroups())) {
        formDescriptor.addGroup(createGroup("Detail", keys));
      }

      formDescriptor.addGroup(createGroup("Audit", auditKeys));

    }

  }

  /**
   * Method for creating a group
   *
   * @param name the group name
   * @param keys the group keys
   * @return the group descriptor
   */
  private FormGroupDescriptor createGroup(String name, List<String> keys) {
    FormGroupDescriptor formGroupDescriptor = new FormGroupDescriptor();
    formGroupDescriptor.setName(name);
    formGroupDescriptor.setIds(keys);
    return formGroupDescriptor;
  }

  /**
   * Method for generating a form control
   *
   * @param packageName the package name of the dto class
   * @param field       the field of the dto class
   * @param onUpdate    flag set to true when method is PUT
   * @param locale      the locale
   * @return form control descriptor
   */
  private FormControlDescriptor generateFormControlWithAttribute(String packageName,
                                                                 Field field,
                                                                 boolean onUpdate,
                                                                 Locale locale) {

    if (checkIfAttributeShouldBeExcluded(field, onUpdate)) {
      return null;
    }

    return generateFormControlWithAttribute(packageName, field, locale);
  }

  /**
   * Method for checking if an attribute should be included
   *
   * @param field    the field for the dto class
   * @param onUpdate flag set to true when method is PUT
   * @return true when add otherwise false
   */
  private boolean checkIfAttributeShouldBeExcluded(Field field, boolean onUpdate) {
    return onUpdate && field.isAnnotationPresent(Null.class);
  }

  /**
   * Generate form control with reflection
   *
   * @param packageName the package name of the dto class
   * @param field       the field of the dto class
   * @param locale      the locale
   * @return form control descriptor
   */
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

  /**
   * Method for setting value of the specified control
   *
   * @param entity  the data source
   * @param control the form control descriptor
   */
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

  /**
   * Method for getting field from data source
   *
   * @param source        the data source
   * @param attributeName the attribute to search for
   * @return the retrieved attribute information
   */
  private Optional<Object> getField(Object source, String attributeName) {
    try {
      return Optional.of(FieldUtils.readDeclaredField(source, attributeName, true));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Init control type
   *
   * @param field                 the field of the dto class
   * @param formControlDescriptor the form control descriptor
   */
  private void initControlWithType(Field field, FormControlDescriptor formControlDescriptor) {

    Class<?> type = field.getType();

    if (Date.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType(String.valueOf(ControlType.datepicker));
    } else if (Integer.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType(String.valueOf(ControlType.slider));
    } else if (Boolean.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType(String.valueOf(ControlType.checkbox));
    } else {
      formControlDescriptor.setControlType(String.valueOf(ControlType.input));
    }

  }

  /**
   * Init control with annotations
   *
   * @param field                 the field of the dto class
   * @param formControlDescriptor the form control descriptor
   * @param packageName           the package name
   * @param locale                the locale
   */
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

  /**
   * Method for generating list of options with the Lov annotation
   *
   * @param lov                   the list of values annotation
   * @param locale                the locale
   * @param formControlDescriptor the form control descriptor
   */
  private void fromEnums(Lov lov, Locale locale, FormControlDescriptor formControlDescriptor) {
    generateLOVWithEnums(lov.enumType(), formControlDescriptor, locale);
  }

  /**
   * Method for generating LOV with the provided enums
   *
   * @param enumType              the type of enum
   * @param formControlDescriptor the form control descriptor
   * @param locale                the locale
   */
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

  /**
   * Method for getting package name with the provided class
   *
   * @param clazz the provided class
   * @return the package name
   */
  private String getPackageNameFromClass(Class<?> clazz) {
    return clazz.getPackage().getName();
  }

  /**
   * Method for creating children components
   *
   * @param children              the children annotation
   * @param locale                the locale
   * @param formControlDescriptor the form control descriptor
   */
  private void fromChildren(Children children, Locale locale, FormControlDescriptor formControlDescriptor) {
    List<FormControlDescriptor> controls = initListOfControlsFromClass(children.type(), null, null, locale);
    formControlDescriptor.setControlChildren(controls);
    formControlDescriptor.setControlType(String.valueOf(ControlType.arrayOfControls));
  }

  /**
   * Method for overriding the control type
   *
   * @param component             the component annotation
   * @param formControlDescriptor the form control descriptor
   */
  private void fromComponent(Component component, FormControlDescriptor formControlDescriptor) {
    //override the component type by default
    formControlDescriptor.setControlType(component.type());
  }

  /**
   * Method for including size validation
   *
   * @param sizeValidation        the size annotation
   * @param packageName           the package name
   * @param locale                the locale
   * @param formControlDescriptor the form control descriptor
   */
  private void fromSizeValidation(Size sizeValidation, String packageName, Locale locale, FormControlDescriptor formControlDescriptor) {
    fromValidation(ValidationType.maxlength, sizeValidation.max(), packageName, locale, formControlDescriptor);
    fromValidation(ValidationType.minlength, sizeValidation.min(), packageName, locale, formControlDescriptor);
  }

  /**
   * Method for adding validation
   *
   * @param validationType        the validation type enum
   * @param value                 the validation value
   * @param packageName           the package name
   * @param locale                the locale
   * @param formControlDescriptor the form control descriptor
   */
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

  /**
   * Method for adding properties value to form control descriptor
   *
   * @param listOfProperties      the properties annotation
   * @param formControlDescriptor the form control descriptor
   */
  private void fromListOfProperties(Properties listOfProperties, FormControlDescriptor formControlDescriptor) {
    for (Property property : listOfProperties.properties()) {
      PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
      propertyDescriptor.setPropertyName(property.propertyName());
      propertyDescriptor.setPropertyValue(property.propertyValue());
      formControlDescriptor.addProperty(propertyDescriptor);
    }
  }

  /**
   * Method for getting the message
   *
   * @param key    the message key
   * @param params the parameters if any
   * @param locale the locale
   * @return the message
   */
  private String getMessageFromKey(String key, Object[] params, Locale locale) {
    Validate.notNull(key, "key must be provided");

    try {
      return messageSource.getMessage(key, params, locale);
    } catch (Exception ex) {
      return key;
    }
  }

}
