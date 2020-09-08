package com.vedrax.jpa;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * The auditor class
 *
 * @author remypenchenat
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditor {

  @Column(name = "created_date")
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @Column(name = "modified_date")
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Column(name = "created_by")
  @CreatedBy
  private String createdBy;

  @Column(name = "modified_by")
  @LastModifiedBy
  private String modifiedBy;

}
