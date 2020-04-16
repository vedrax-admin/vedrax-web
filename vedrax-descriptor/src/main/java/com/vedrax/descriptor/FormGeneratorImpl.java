package com.vedrax.descriptor;

import com.vedrax.descriptor.components.*;
import com.vedrax.descriptor.form.AuditManager;
import com.vedrax.descriptor.form.ControlManager;
import com.vedrax.descriptor.form.GroupManager;
import com.vedrax.descriptor.util.MessageUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

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

    FormDescriptor formDescriptor = new FormDescriptor();
    formDescriptor.setMethod(formDto.getMethod() == null ? "POST" : formDto.getMethod());
    formDescriptor.setEndpoint(formDto.getEndpoint());

    //set group if any
    GroupManager groupManager = new GroupManager(formDto.getDto());
    formDescriptor.setGroups(groupManager.getGroups());

    //set controls
    ControlManager controlManager = new ControlManager(messageSource, locale);
    List<String> controlKeys = controlManager.initControls(formDto, formDescriptor);

    //set audit
    AuditManager auditManager = new AuditManager(messageSource, locale);
    List<String> auditKeys = auditManager.initAudit(formDto.getSource(), formDescriptor.getControls());
    groupManager.addAuditToGroups(formDescriptor, controlKeys, auditKeys);

    //set button label
    initFormButtonsLabel(formDescriptor, locale);

    //set success message
    initSuccessMessage(formDto.getSource(), formDto.getDto(), formDescriptor, locale);

    return formDescriptor;
  }

  /**
   * Init form buttons labels
   *
   * @param formDescriptor the form descriptor
   * @param locale         the locale
   */
  private void initFormButtonsLabel(FormDescriptor formDescriptor, Locale locale) {
    formDescriptor.setSubmitLabel(MessageUtil.getMessageFromKey(messageSource, "submit.label", null, locale));
    formDescriptor.setCancelLabel(MessageUtil.getMessageFromKey(messageSource, "cancel.label", null, locale));
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
    formDescriptor.setSuccessMessage(MessageUtil.getMessageFromKey(messageSource, dtoClass.getName() + "." + key, null, locale));
  }


}
