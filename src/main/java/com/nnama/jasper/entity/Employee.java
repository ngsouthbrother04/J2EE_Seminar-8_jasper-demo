package com.nnama.jasper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  int id;

  String name;
  String designation;
  double salary;
  String doj;

  @ManyToOne(fetch = FetchType.LAZY)
  Department department;
}
