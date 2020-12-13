package com.vedrax.descriptor;

import com.vedrax.descriptor.components.FormDescriptor;
import com.vedrax.descriptor.form.AuditManager;
import com.vedrax.descriptor.form.ControlManager;
import com.vedrax.descriptor.form.EndpointManager;
import com.vedrax.descriptor.form.GroupManager;
import com.vedrax.util.MessageUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

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

    locale = locale == null ? Locale.ENGLISH : locale;

    FormDescriptor formDescriptor = new FormDescriptor();
    formDescriptor.setMethod(formDto.getMethod() == null ? "POST" : formDto.getMethod());
    formDescriptor.setEndpoint(formDto.getEndpoint());
    formDescriptor.setUpdateTable(formDto.isUpdateTable());
    formDescriptor.setTitle(formDto.getTitle());
    formDescriptor.setSuccessUrl(formDto.getSuccessURL());
    formDescriptor.setSuccessMessage(formDto.getSuccessMessage());
    formDescriptor.setMultipart(formDto.getMultipart());

    //set group if any
    GroupManager groupManager = new GroupManager(formDto.getDto());
    groupManager.init(formDescriptor);

    //set LOVs if any
    EndpointManager endpointManager = new EndpointManager(formDto.getDto());
    endpointManager.init(formDescriptor);

    //set controls
    ControlManager controlManager = new ControlManager(messageSource, locale);
    List<String> controlKeys = controlManager.init(formDto, formDescriptor);

    //set audit
    AuditManager auditManager = new AuditManager(messageSource, locale);
    List<String> auditKeys = auditManager.init(formDto.getSource(), formDescriptor.getControls());
    groupManager.addAuditToGroups(formDescriptor, controlKeys, auditKeys);

    //set button label
    initFormButtonsLabel(formDescriptor, locale);

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
