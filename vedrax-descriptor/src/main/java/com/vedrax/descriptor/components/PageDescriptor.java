package com.vedrax.descriptor.components;

import lombok.Data;

import java.util.List;

/**
 * Class represents a page description
 */
@Data
public class PageDescriptor {

  private String title;
  private List<ActionDescriptor> actionDescriptors;
  private String from;
  private String componentType;
  private Object component;

}
