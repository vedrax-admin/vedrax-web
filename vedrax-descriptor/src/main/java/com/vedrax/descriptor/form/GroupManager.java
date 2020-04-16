package com.vedrax.descriptor.form;

import com.vedrax.descriptor.annotations.Group;
import com.vedrax.descriptor.annotations.Groups;
import com.vedrax.descriptor.components.FormDescriptor;
import com.vedrax.descriptor.components.FormGroupDescriptor;
import org.apache.commons.lang3.Validate;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class GroupManager {

  private static final String DETAIL_LABEL = "Detail";
  private static final String AUDIT_LABEL = "Audit";

  private Class<?> sourceClass;

  public GroupManager(Class<?> sourceClass) {
    Objects.requireNonNull(sourceClass, "source class must be provided");

    this.sourceClass = sourceClass;
  }

  /**
   * Method for getting groups
   *
   * @return list of groups
   */
  public void init(FormDescriptor formDescriptor) {
    Validate.notNull(formDescriptor,"form descriptor must be provided");
    
    List<FormGroupDescriptor> groups = new ArrayList<>();

    Optional<Groups> optionalGroups = getGroupsAnnotation(sourceClass);
    if (optionalGroups.isPresent()) {
      for (Group group : optionalGroups.get().groups()) {
        FormGroupDescriptor formGroupDescriptor = createGroup(group);
        groups.add(formGroupDescriptor);
      }
    }

    formDescriptor.setGroups(groups);
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
   * Method for adding audit group to descriptor form
   *
   * @param formDescriptor the form descriptor
   * @param auditKeys      the audit controls keys
   */
  public void addAuditToGroups(FormDescriptor formDescriptor,List<String> controlKeys, List<String> auditKeys) {
    Validate.notNull(formDescriptor, "form descriptor must be provided");

    if (!CollectionUtils.isEmpty(auditKeys)) {

      if (CollectionUtils.isEmpty(formDescriptor.getGroups())) {
        formDescriptor.addGroup(createGroup(DETAIL_LABEL, controlKeys));
      }

      formDescriptor.addGroup(createGroup(AUDIT_LABEL, auditKeys));

    }

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


}
