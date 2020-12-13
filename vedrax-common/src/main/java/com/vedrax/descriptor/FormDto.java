package com.vedrax.descriptor;

import lombok.Data;

@Data
public class FormDto {

  public static class Builder {
    private final Class<?> dto;
    private final String endpoint;
    private String method;
    private Boolean multipart;
    private Object source;
    private boolean updateTable;
    private String title;
    private String successMessage;
    private String successURL;

    public Builder(Class<?> dto, String endpoint){
      this.dto = dto;
      this.endpoint = endpoint;
      this.updateTable = false;
      this.multipart = false;
    }

    public Builder withMethod(String method){
      this.method = method;
      return this;
    }

    public Builder withMultipart(Boolean multipart){
      this.multipart = multipart;
      return this;
    }

    public Builder withSource(Object source){
      this.source = source;
      return this;
    }

    public Builder withTitle(String title){
      this.title = title;
      return this;
    }

    public Builder withUpdateTable(boolean updateTable){
      this.updateTable = updateTable;
      return this;
    }

    public Builder withSuccessMessage(String successMessage){
      this.successMessage = successMessage;
      return this;
    }

    public Builder withSuccessURL(String successURL){
      this.successURL = successURL;
      return this;
    }

    public FormDto build(){
      FormDto formDto = new FormDto();
      formDto.dto = this.dto;
      formDto.endpoint = this.endpoint;
      formDto.method = this.method;
      formDto.multipart = this.multipart;
      formDto.source = this.source;
      formDto.updateTable = this.updateTable;
      formDto.title = this.title;
      formDto.successMessage = this.successMessage;
      formDto.successURL = this.successURL;
      return formDto;

    }
  }

  private Class<?> dto;
  private String endpoint;
  private String method;
  private Boolean multipart;
  private Object source;
  private boolean updateTable;
  private String title;
  private String successMessage;
  private String successURL;

  private FormDto(){}

}
