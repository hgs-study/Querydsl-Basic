package com.hgstudy.customer.application;

import com.hgstudy.customer.entity.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.hgstudy.customer.entity.QCustomer.customer;

@Repository
public class CustomerRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomerRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Customer.class);
        this.queryFactory = jpaQueryFactory;
    }

    public Customer findByLastName(String lastName){
        return queryFactory.selectFrom(customer)
                           .where(customer.lastName.eq(lastName))
                           .fetchOne();
    }
}
