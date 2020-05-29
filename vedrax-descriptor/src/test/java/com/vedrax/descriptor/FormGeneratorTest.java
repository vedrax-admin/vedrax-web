package com.vedrax.descriptor;

import com.vedrax.descriptor.components.FormDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormGeneratorTest {

  private MessageSource messageSource = mock(MessageSource.class);
  private FormGenerator formGenerator;

  @Before
  public void setUp() {
    this.formGenerator = new FormGeneratorImpl(messageSource);

    when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("message");
  }

  @Test
  public void whenGenerate_thenGenerateDescriptor() {

    FormDto formDto = new FormDto.Builder(UserCreateDto.class,"endpoint").build();

    FormDescriptor formDescriptor = formGenerator.generate(formDto, Locale.ENGLISH);

    assertThat(formDescriptor).isNotNull();
    assertThat(formDescriptor.getControls()).hasSize(4);
    assertThat(formDescriptor.getEndpoint()).isEqualTo("endpoint");

    //check email
    assertThat(formDescriptor.getControls().get(0).getControlName()).isEqualTo("email");
    assertThat(formDescriptor.getControls().get(0).getControlType()).isEqualTo("input");
    assertThat(formDescriptor.getControls().get(0).getControlValidations()).hasSize(2);
    assertThat(formDescriptor.getControls().get(0).getControlValidations().get(0).getValidationName()).isEqualTo("required");
    assertThat(formDescriptor.getControls().get(0).getControlValidations().get(1).getValidationName()).isEqualTo("email");

    //check password
    assertThat(formDescriptor.getControls().get(1).getControlName()).isEqualTo("password");
    assertThat(formDescriptor.getControls().get(1).getControlType()).isEqualTo("input");
    assertThat(formDescriptor.getControls().get(1).getControlValidations()).hasSize(1);
    assertThat(formDescriptor.getControls().get(1).getControlValidations().get(0).getValidationName()).isEqualTo("required");
    assertThat(formDescriptor.getControls().get(1).getControlProperties()).hasSize(1);
    assertThat(formDescriptor.getControls().get(1).getControlProperties().get(0).getPropertyName()).isEqualTo("type");
    assertThat(formDescriptor.getControls().get(1).getControlProperties().get(0).getPropertyValue()).isEqualTo("password");

    //check user role
    assertThat(formDescriptor.getControls().get(3).getControlName()).isEqualTo("userRole");
    assertThat(formDescriptor.getControls().get(3).getControlType()).isEqualTo("select");
    assertThat(formDescriptor.getControls().get(3).getControlOptions()).hasSize(2);

  }

  @Test
  public void givenSourceWithAuditInfo_whenGenerate_thenGenerateDescriptorWithAuditControls() {

    UserVO userVO = new UserVO();
    userVO.setEmail("finance@vedrax.com");
    userVO.setFullName("Remy Penchenat");
    userVO.setUserRole(UserRole.USER);
    userVO.setCreatedBy("ADMIN");
    userVO.setCreatedDate(new Date());

    FormDto formDto = new FormDto.Builder(UserCreateDto.class,"endpoint").withSource(userVO).build();

    FormDescriptor formDescriptor = formGenerator.generate(formDto, Locale.ENGLISH);

    assertThat(formDescriptor).isNotNull();
    assertThat(formDescriptor.getControls()).hasSize(6);

    assertThat(formDescriptor.getGroups().get(0).getName()).isEqualTo("Detail");
    assertThat(formDescriptor.getGroups().get(0).getIds()).containsExactly("email", "password", "fullName", "userRole");

    assertThat(formDescriptor.getGroups().get(1).getName()).isEqualTo("Audit");
    //assertThat(formDescriptor.getGroups().get(1).getIds()).containsExactly("createdDate", "createdBy");
  }

  @Test
  public void test(){

    AssessmentEquipmentEntityVO assessmentEquipment = new AssessmentEquipmentEntityVO();
    assessmentEquipment.setId(1L);
    assessmentEquipment.setDesignation("designation");
    assessmentEquipment.setControlDate(new Date());
    assessmentEquipment.setPeriodicityValue(1);
    assessmentEquipment.setWithUncertainty(true);
    assessmentEquipment.setActive(true);
    assessmentEquipment.setCreatedDate(new Date());
    assessmentEquipment.setCreatedBy("remy");

    String url = String.format("%s/%s", "/test", assessmentEquipment.getId());

    FormDto formDto = new FormDto.Builder(AssessmentEquipmentDto.class,url)
            .withMethod("PUT")
            .withSource(assessmentEquipment).build();

    FormDescriptor formDescriptor = formGenerator.generate(formDto, null);

    assertThat(formDescriptor).isNotNull();
  }


}
