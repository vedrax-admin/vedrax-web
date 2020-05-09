package com.vedrax.descriptor.form;

import com.vedrax.descriptor.FormDto;
import com.vedrax.descriptor.annotations.*;
import com.vedrax.descriptor.annotations.Properties;
import com.vedrax.descriptor.components.*;
import com.vedrax.descriptor.enums.ActionType;
import com.vedrax.descriptor.enums.ControlType;
import com.vedrax.descriptor.enums.ValidationType;
import com.vedrax.descriptor.lov.EnumWithValue;
import com.vedrax.descriptor.lov.NVP;
import com.vedrax.descriptor.util.MessageUtil;
import com.vedrax.descriptor.util.ReflectUtil;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.MessageSource;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.vedrax.descriptor.typeof.TypeOf.whenTypeOf;

public class ControlManager {

  private final MessageSource messageSource;
  private final Locale locale;

  public ControlManager(MessageSource messageSource, Locale locale) {
    this.messageSource = messageSource;
    this.locale = locale;
  }

  public List<String> init(FormDto formDto, FormDescriptor formDescriptor) {
    Validate.notNull(formDto, "form dto must be provided");
    Validate.notNull(formDescriptor, "form descriptor must be provided");

    List<FormControlDescriptor> controls = getControls(formDto.getDto(), formDto.getSource());
    formDescriptor.setControls(controls);

    return controls.stream().map(FormControlDescriptor::getControlName).collect(Collectors.toList());
  }

  /**
   * Create the list of controls descriptors using reflection
   *
   * @param sourceClass the source class
   * @param source      the source
   * @return list of controls descriptors
   */
  private List<FormControlDescriptor> getControls(Class<?> sourceClass, Object source) {
    Validate.notNull(sourceClass, "source class must be provided");

    String packageName = sourceClass.getPackage().getName();

    List<FormControlDescriptor> controls = new ArrayList<>();

    Field[] fields = FieldUtils.getAllFields(sourceClass);

    boolean hasSource = source != null;

    for (Field field : fields) {

      FormControlDescriptor formControlDescriptor = generateFormControlWithAttribute(field, hasSource, packageName);
      if (formControlDescriptor != null) {

        initControlWithType(field, formControlDescriptor);
        initControlWithAnnotations(field, formControlDescriptor, packageName);
        setControlValue(source, formControlDescriptor);
        controls.add(formControlDescriptor);
      }

    }

    return controls;
  }

  /**
   * Method for generating a form control
   *
   * @param field the field of the dto class
   * @return form control descriptor
   */
  private FormControlDescriptor generateFormControlWithAttribute(Field field, boolean hasSource, String packageName) {

    if (checkIfAttributeShouldBeExcluded(field, hasSource)) {
      return null;
    }

    return initFormControl(field, packageName);
  }

  /**
   * Method for checking if an attribute should be included
   *
   * @param field the field for the dto class
   * @return true when add otherwise false
   */
  private boolean checkIfAttributeShouldBeExcluded(Field field, boolean hasSource) {
    return hasSource && field.isAnnotationPresent(Null.class);
  }


  /**
   * Generate form control with reflection
   *
   * @param field the field of the dto class
   * @return form control descriptor
   */
  private FormControlDescriptor initFormControl(Field field, String packageName) {
    FormControlDescriptor formControlDescriptor = new FormControlDescriptor();

    String controlKey = String.format("%s.%s", packageName, field.getName());

    formControlDescriptor.setControlName(field.getName());
    formControlDescriptor.setControlLabel(MessageUtil.getMessageFromKey(messageSource, controlKey + ".label", null, locale));
    formControlDescriptor.setControlHint(MessageUtil.getMessageFromKey(messageSource, controlKey + ".hint", null, locale));
    return formControlDescriptor;
  }

  /**
   * Init control type
   *
   * @param field                 the field of the dto class
   * @param formControlDescriptor the form control descriptor
   */
  public void initControlWithType(Field field, FormControlDescriptor formControlDescriptor) {

    Class<?> type = field.getType();

    if (Date.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType(String.valueOf(ControlType.datepicker));
    } else if (Integer.class.isAssignableFrom(type)) {
      setAsNumber(formControlDescriptor);
    } else if (Double.class.isAssignableFrom(type)) {
      setAsNumber(formControlDescriptor);
    } else if (BigDecimal.class.isAssignableFrom(type)) {
      setAsNumber(formControlDescriptor);
    } else if (Boolean.class.isAssignableFrom(type)) {
      formControlDescriptor.setControlType(String.valueOf(ControlType.checkbox));
    } else {
      formControlDescriptor.setControlType(String.valueOf(ControlType.input));
    }

  }

  /**
   * Create number property
   */
  private void setAsNumber(FormControlDescriptor formControlDescriptor) {
    PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
    propertyDescriptor.setPropertyName("type");
    propertyDescriptor.setPropertyValue("number");

    formControlDescriptor.setControlType(String.valueOf(ControlType.input));
    formControlDescriptor.addProperty(propertyDescriptor);
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

    Optional<Object> fieldOpt = ReflectUtil.getField(entity, controlName);

    fieldOpt.ifPresent(control::setControlValue);

  }

  /**
   * Init control with annotations
   *
   * @param field                 the field of the dto class
   * @param formControlDescriptor the form control descriptor
   */
  public void initControlWithAnnotations(Field field,
                                         FormControlDescriptor formControlDescriptor, String packageName) {

    for (Annotation annotation : field.getDeclaredAnnotations()) {

      whenTypeOf(annotation)
        .is(com.vedrax.descriptor.annotations.Properties.class).then(properties -> fromListOfProperties(properties, formControlDescriptor))
        .is(Component.class).then(component -> fromComponent(component, formControlDescriptor))
        .is(Children.class).then(children -> fromChildren(children, formControlDescriptor))
        .is(Lov.class).then(lov -> fromEnums(lov, formControlDescriptor))
        .is(NotNull.class).then(validation -> fromValidation(ValidationType.required, true, formControlDescriptor, packageName))
        .is(Size.class).then(validation -> fromSizeValidation(validation, formControlDescriptor, packageName))
        .is(Email.class).then(validation -> fromValidation(ValidationType.email, true, formControlDescriptor, packageName))
        .is(Min.class).then(validation -> fromValidation(ValidationType.min, validation.value(), formControlDescriptor, packageName))
        .is(Max.class).then(validation -> fromValidation(ValidationType.max, validation.value(), formControlDescriptor, packageName))
        .is(NotEmpty.class).then(validation -> fromValidation(ValidationType.required, true, formControlDescriptor, packageName))
        .is(NotBlank.class).then(validation -> fromValidation(ValidationType.required, true, formControlDescriptor, packageName))
        .is(Search.class).then(search -> fromSearch(search, formControlDescriptor))
        .is(Pattern.class).then(validation -> fromValidation(ValidationType.pattern, validation.regexp(), formControlDescriptor, packageName));
    }

  }

  /**
   * Method for generating list of options with the Lov annotation
   *
   * @param lov                   the list of values annotation
   * @param formControlDescriptor the form control descriptor
   */
  private void fromEnums(Lov lov, FormControlDescriptor formControlDescriptor) {
    generateLOVWithEnums(lov.enumType(), formControlDescriptor);
  }

  /**
   * Method for generating LOV with the provided enums
   *
   * @param enumType              the type of enum
   * @param formControlDescriptor the form control descriptor
   */
  private void generateLOVWithEnums(Class<? extends EnumWithValue> enumType,
                                    FormControlDescriptor formControlDescriptor) {

    List<NVP> nvpList = new ArrayList<>();

    String className = enumType.getName();

    EnumWithValue[] values = enumType.getEnumConstants();

    for (EnumWithValue enumWithValue : values) {
      String key = "" + enumWithValue;

      NVP nvp = new NVP();
      nvp.setKey(key);
      nvp.setValue(MessageUtil.getMessageFromKey(messageSource, className + "." + key, null, locale));
      nvpList.add(nvp);
    }

    formControlDescriptor.setControlOptions(nvpList);
    formControlDescriptor.setControlType("select");

  }

  /**
   * Method for creating children components
   *
   * @param children              the children annotation
   * @param formControlDescriptor the form control descriptor
   */
  private void fromChildren(Children children, FormControlDescriptor formControlDescriptor) {
    List<FormControlDescriptor> controls = getControls(children.type(), null);
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
   * @param formControlDescriptor the form control descriptor
   */
  private void fromSizeValidation(Size sizeValidation, FormControlDescriptor formControlDescriptor, String packageName) {
    fromValidation(ValidationType.maxlength, sizeValidation.max(), formControlDescriptor, packageName);
    fromValidation(ValidationType.minlength, sizeValidation.min(), formControlDescriptor, packageName);
  }

  /**
   * Method for adding validation
   *
   * @param validationType        the validation type enum
   * @param value                 the validation value
   * @param formControlDescriptor the form control descriptor
   */
  private void fromValidation(ValidationType validationType,
                              Object value,
                              FormControlDescriptor formControlDescriptor,
                              String packageName) {

    String messageKey = String.format("%s.%s.%s", packageName, formControlDescriptor.getControlName(), validationType);
    ValidationDescriptor validationDescriptor = new ValidationDescriptor();
    validationDescriptor.setValidationName(validationType.getName());
    validationDescriptor.setValidationValue(value);
    if (value != null) {
      validationDescriptor.setValidationMessage(MessageUtil.getMessageFromKey(messageSource, messageKey, new Object[]{value}, locale));
    } else {
      validationDescriptor.setValidationMessage(MessageUtil.getMessageFromKey(messageSource, messageKey, null, locale));
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
   * Method for creating search control
   *
   * @param search                the search annotation
   * @param formControlDescriptor the form control descriptor
   */
  private void fromSearch(Search search, FormControlDescriptor formControlDescriptor) {

    String packageName = search.vo().getPackage().getName();

    TableDescriptor tableDescriptor = new TableDescriptor();
    tableDescriptor.setTitle(MessageUtil.getMessageFromKey(messageSource, packageName + ".title", null, locale));
    tableDescriptor.setPaginated(true);
    tableDescriptor.setLoadOnInit(false);
    tableDescriptor.setPath(search.endpoint());
    tableDescriptor.setColumns(getColumns(search.vo()));
    tableDescriptor.setSearchControls(getControls(search.form(), null));

    formControlDescriptor.setControlType(String.valueOf(ControlType.search));
    formControlDescriptor.setControlSearch(tableDescriptor);

  }

  /**
   * Method for getting the search columns
   *
   * @param vo the applied VO
   * @return list of columns info
   */
  private List<ColumnDescriptor> getColumns(Class<?> vo) {

    String packageName = vo.getPackage().getName();

    List<ColumnDescriptor> columns = new ArrayList<>();

    Field[] fields = FieldUtils.getAllFields(vo);

    for (Field field : fields) {

      String labelKey = String.format("%s.%s.label", packageName, field.getName());

      ColumnDescriptor columnDescriptor = new ColumnDescriptor();
      columnDescriptor.setId(field.getName());
      columnDescriptor.setLabel(MessageUtil.getMessageFromKey(messageSource, labelKey, null, locale));
      columns.add(columnDescriptor);

    }

    //append action column
    columns.add(getActionColumn());

    return columns;

  }

  private ColumnDescriptor getActionColumn(){
    ColumnDescriptor columnDescriptor = new ColumnDescriptor();
    columnDescriptor.setId("actionSearch");
    columnDescriptor.setLabel(MessageUtil.getMessageFromKey(messageSource, "actions.label", null, locale));
    columnDescriptor.setActions(getActions());
    return columnDescriptor;
  }

  private List<ActionDescriptor> getActions(){
    List<ActionDescriptor> actions = new ArrayList<>();
    ActionDescriptor actionDescriptor = new ActionDescriptor();
    actionDescriptor.setLabel(MessageUtil.getMessageFromKey(messageSource, "selection.label", null, locale));
    actionDescriptor.setAction(ActionType.select);
    actions.add(actionDescriptor);
    return actions;
  }

}
