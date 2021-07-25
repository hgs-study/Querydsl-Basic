package com.hgstudy.customer.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import javax.persistence.Column;

@Getter
public class CustomerDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String company;

    @QueryProjection
    public CustomerDto(String firstName, String lastName, String company) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
    }
}
