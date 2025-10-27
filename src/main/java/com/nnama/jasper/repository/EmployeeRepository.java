package com.nnama.jasper.repository;

import com.nnama.jasper.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
  
}
