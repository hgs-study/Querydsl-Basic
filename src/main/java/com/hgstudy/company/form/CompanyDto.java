package com.hgstudy.company.form;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CompanyDto {

    private Long id;
    private String name;
    private String department;

    @QueryProjection
    public CompanyDto(String name, String department) {
        this.name = name;
        this.department = department;
    }
}
