package com.vedrax.descriptor.form;

import com.vedrax.descriptor.FormDto;
import com.vedrax.descriptor.annotations.Properties;
import com.vedrax.descriptor.annotations.*;
import com.vedrax.descriptor.components.*;
import com.vedrax.descriptor.enums.ControlType;
import com.vedrax.descriptor.enums.ValidationType;
import com.vedrax.descriptor.lov.EnumWithValue;
import com.vedrax.descriptor.lov.NVP;
import com.vedrax.util.MessageUtil;
import com.vedrax.util.ReflectUtil;
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

        if (fieldOpt.isPresent()) {

            Object value = fieldOpt.get();

            if (control.getControlType().equals(String.valueOf(ControlType.autocomplete))) {

                AutocompleteDescriptor autocompleteDescriptor = control.getControlSearch();

                Validate.notNull(autocompleteDescriptor, "AutocompleteDescriptor must be provided via annotation");

                NVP nvp = new NVP();
                nvp.setKey(String.valueOf(value));
                Optional<Object> displayOpt = ReflectUtil.getField(entity, autocompleteDescriptor.getDisplayKey());
                nvp.setValue(String.valueOf(displayOpt.orElse(value)));
                control.setControlValue(nvp);
            } else {
                fieldOpt.ifPresent(control::setControlValue);
            }

        }


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
                    .is(Properties.class).then(properties -> fromListOfProperties(properties, formControlDescriptor))
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
                    .is(Autocomplete.class).then(autocomplete -> fromAutocomplete(autocomplete, formControlDescriptor))
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

    private void fromAutocomplete(Autocomplete autocomplete, FormControlDescriptor formControlDescriptor) {
        formControlDescriptor.setControlType(String.valueOf(ControlType.autocomplete));

        AutocompleteDescriptor autocompleteDescriptor = new AutocompleteDescriptor();
        autocompleteDescriptor.setEndpoint(autocomplete.endpoint());
        autocompleteDescriptor.setDisplayKey(autocomplete.displayAttribute());
        List<NVP> params = new ArrayList<>();

        for (String param : autocomplete.params()) {
            String[] splitString = param.split(":");
            NVP nameValuePair = new NVP();
            nameValuePair.setKey(splitString[0]);
            nameValuePair.setValue(splitString[1]);
            params.add(nameValuePair);
        }

        autocompleteDescriptor.setDefaultParams(params);

        List<NVP> filters = new ArrayList<>();
        for (String filter : autocomplete.filters()) {
            NVP nameValuePair = new NVP();
            nameValuePair.setKey(filter);
            nameValuePair.setValue(MessageUtil.getMessageFromKey(messageSource, "filter." + filter, null, locale));
            filters.add(nameValuePair);
        }

        autocompleteDescriptor.setFilters(filters);

        formControlDescriptor.setControlSearch(autocompleteDescriptor);
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
        formControlDescriptor.setControlKeysAsTitle(Arrays.asList(children.controlKeysAsTitle()));
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
        if (sizeValidation.max() > 0) {
            fromValidation(ValidationType.maxlength, sizeValidation.max(), formControlDescriptor, packageName);
        }

        if (sizeValidation.min() > 0) {
            fromValidation(ValidationType.minlength, sizeValidation.min(), formControlDescriptor, packageName);
        }
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

}
