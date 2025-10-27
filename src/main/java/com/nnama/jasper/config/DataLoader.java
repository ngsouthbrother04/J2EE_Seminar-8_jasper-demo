package com.nnama.jasper.config;

import com.nnama.jasper.entity.Department;
import com.nnama.jasper.entity.Employee;
import com.nnama.jasper.entity.Project;
import com.nnama.jasper.repository.DepartmentRepository;
import com.nnama.jasper.repository.EmployeeRepository;
import com.nnama.jasper.repository.ProjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

  @Bean
  CommandLineRunner seedData(EmployeeRepository empRepo,
      DepartmentRepository depRepo,
      ProjectRepository projRepo) {
    return args -> {
      if (depRepo.count() == 0) {
        depRepo.saveAll(Arrays.asList(
            Department.builder().name("Engineering").build(),
            Department.builder().name("Quality Assurance").build(),
            Department.builder().name("Human Resources").build(),
            Department.builder().name("Sales").build(),
            Department.builder().name("Finance").build()));
      }

      if (projRepo.count() == 0) {
        projRepo.saveAll(Arrays.asList(
            Project.builder().name("Apollo").budget(50000).build(),
            Project.builder().name("Zephyr").budget(75000).build(),
            Project.builder().name("Orion").budget(100000).build(),
            Project.builder().name("Helios").budget(25000).build(),
            Project.builder().name("Hyperion").budget(90000).build()));
      }

      if (empRepo.count() == 0) {
        List<Department> deps = depRepo.findAll();
        String[] firstNames = { "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy",
            "Ken", "Laura", "Mallory", "Niaj", "Olivia", "Peggy", "Quentin", "Ruth", "Sybil", "Trent", "Uma", "Victor",
            "Wendy", "Xavier", "Yvonne", "Zach" };
        String[] lastNames = { "Nguyen", "Tran", "Le", "Pham", "Hoang", "Phan", "Vu", "Vo", "Dang", "Bui", "Do", "Ly",
            "Cao", "Ngo", "Duong", "Huynh", "Mai", "Trinh", "Truong", "Dinh" };
        String[] titles = { "Developer", "Senior Developer", "QA Engineer", "QA Lead", "Manager", "Team Lead",
            "Business Analyst", "DevOps Engineer", "Data Engineer", "Product Owner" };

        Random rnd = new Random(42);
        List<Employee> bulk = new ArrayList<>();
        int total = 100;
        for (int i = 0; i < total; i++) {
          String name = firstNames[rnd.nextInt(firstNames.length)] + " " + lastNames[rnd.nextInt(lastNames.length)];
          String designation = titles[rnd.nextInt(titles.length)];
          double salary = 800 + rnd.nextInt(2000) + rnd.nextDouble();
          LocalDate d = LocalDate.of(2020 + rnd.nextInt(5), 1 + rnd.nextInt(12), 1 + rnd.nextInt(28));
          Department dep = deps.get(rnd.nextInt(deps.size()));

          bulk.add(Employee.builder()
              .name(name)
              .designation(designation)
              .salary(salary)
              .doj(d.toString())
              .department(dep)
              .build());
        }
        empRepo.saveAll(bulk);
      }
    };
  }
}
