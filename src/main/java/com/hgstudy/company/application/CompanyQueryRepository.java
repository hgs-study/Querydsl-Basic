package com.hgstudy.company.application;

import com.hgstudy.company.entity.Company;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.hgstudy.company.entity.QCompany.company;

@Repository
@RequiredArgsConstructor
public class CompanyQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Company findByDepartment(String department){
        return jpaQueryFactory
                        .selectFrom(company)
                        .where(company.department.eq(department))
                        .fetchOne();
    }
}
