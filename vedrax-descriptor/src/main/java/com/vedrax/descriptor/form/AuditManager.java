package com.vedrax.descriptor.form;

import com.vedrax.descriptor.components.FormControlDescriptor;
import com.vedrax.descriptor.components.PropertyDescriptor;
import com.vedrax.descriptor.enums.ControlType;
import com.vedrax.descriptor.util.MessageUtil;
import com.vedrax.descriptor.util.ReflectUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;

import java.util.*;

/**
 * Class which manages audit information display in a form
 */
public class AuditManager {

  private final MessageSource messageSource;
  private final Locale locale;
  private static final String CREATED_BY = "createdBy";
  private static final String CREATED_DATE = "createdDate";
  private static final String MODIFIED_BY = "modifiedBy";
  private static final String MODIFIED_DATE = "modifiedDate";
  private static final String READ_ONLY = "readOnly";

  public AuditManager(MessageSource messageSource, Locale locale) {
    this.messageSource = messageSource;
    this.locale = locale;
  }

  /**
   * Method for incorporating audit information when provided in the data class
   *
   * @param source         the data class
   * @param controls       the list of controls descriptor
   */
  public List<String> init(Object source,
                           List<FormControlDescriptor> controls) {
    Validate.notNull(controls, "controls must be provided");

    List<String> auditKeys = new ArrayList<>();

    addAuditToControls(source, CREATED_DATE, controls, auditKeys);
    addAuditToControls(source, CREATED_BY, controls, auditKeys);
    addAuditToControls(source, MODIFIED_DATE, controls, auditKeys);
    addAuditToControls(source, MODIFIED_BY, controls, auditKeys);

    return auditKeys;

  }

  /**
   * Method for adding audit information to the list of controls
   *
   * @param source        the data source
   * @param attributeName the audit attribute name
   * @param controls      the list of controls descriptors
   * @param auditKeys     the list of audit keys
   */
  private void addAuditToControls(Object source,
                                  String attributeName,
                                  List<FormControlDescriptor> controls,
                                  List<String> auditKeys) {

    if (source == null) {
      return;
    }

    Optional<Object> fieldOpt = ReflectUtil.getField(source, attributeName);

    if (fieldOpt.isPresent()) {

      controls.add(generateAuditControl(attributeName, fieldOpt.get()));

      auditKeys.add(attributeName);
    }

  }

  /**
   * Generate audit control
   *
   * @param attributeName the audit control name
   * @param value         the audit control value
   * @return form control descriptor
   */
  private FormControlDescriptor generateAuditControl(String attributeName, Object value) {
    FormControlDescriptor formControlDescriptor = new FormControlDescriptor();

    formControlDescriptor.setControlName(attributeName);
    formControlDescriptor.setControlLabel(MessageUtil.getMessageFromKey(messageSource, attributeName + ".label", null, locale));
    formControlDescriptor.addProperty(new PropertyDescriptor(READ_ONLY,true));
    formControlDescriptor.setControlType(String.valueOf(ControlType.input));
    formControlDescriptor.setControlValue(value);

    return formControlDescriptor;
  }


}
