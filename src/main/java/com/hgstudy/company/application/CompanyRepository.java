package com.hgstudy.company.application;

import com.hgstudy.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    Company findByDepartment(String department);
}
