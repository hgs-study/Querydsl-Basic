package com.hgstudy.customer.application;

import com.hgstudy.customer.entity.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.hgstudy.customer.entity.QCustomer.customer;

@Repository
@RequiredArgsConstructor
public class CustomerQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Customer findByLastName(String lastName){
        return queryFactory.selectFrom(customer)
                           .where(customer.lastName.eq(lastName))
                           .fetchOne();
    }
}
