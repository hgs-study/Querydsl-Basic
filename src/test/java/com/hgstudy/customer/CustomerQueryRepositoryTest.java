package com.hgstudy.customer;

import com.hgstudy.customer.entity.Customer;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import static com.hgstudy.customer.entity.QCustomer.customer;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class CustomerQueryRepositoryTest {


    @Autowired
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    public void init() {
        jpaQueryFactory = new JPAQueryFactory(em);

        Customer saveCustomer1 = new Customer("gs","hyun");
        Customer saveCustomer2 = new Customer("aa","kim");
        Customer saveCustomer3 = new Customer("bb","park");
        Customer saveCustomer4 = new Customer("gs","hyun2");
        Customer saveCustomer5 = new Customer("aa","kim2");
        Customer saveCustomer6 = new Customer("bb","park2");

        em.persist(saveCustomer1);
        em.persist(saveCustomer2);
        em.persist(saveCustomer3);
        em.persist(saveCustomer4);
        em.persist(saveCustomer5);
        em.persist(saveCustomer6);
    }

    @DisplayName("FirstName, LastName 찾기 - and()")
    @Test
    void findByFirstNameAndLastName(){

        Customer findCustomer = jpaQueryFactory
                                    .selectFrom(customer)
                                    .where(customer.lastName.eq("hyun")
                                    .and(customer.firstName.eq("gs")))
                                    .fetchOne();

        assertEquals("gs",findCustomer.getFirstName());
    }


    @DisplayName("LastName (hyun or lee) 찾기 - or()")
    @Test
    void or(){

        Customer findCustomers = jpaQueryFactory
                                        .selectFrom(customer)
                                        .where(customer.lastName.eq("hyun")
                                        .or(customer.lastName.eq("lee")))
                                        .fetchOne();

        assertEquals("hyun",findCustomers.getLastName());
    }

    @DisplayName("lastName 오름차순 , firstName 내림차순 정렬 - orderBy()")
    @Test
    void orderBy(){

        List<Customer> findCustomers = jpaQueryFactory
                                        .selectFrom(customer)
                                        .orderBy(customer.lastName.asc(),customer.firstName.desc())
                                        .fetch();

        assertAll(
                ()->assertEquals("hyun",findCustomers.get(0).getLastName()),
                ()->assertEquals("kim",findCustomers.get(1).getLastName()),
                ()->assertEquals("park",findCustomers.get(2).getLastName())
        );
    }


    @DisplayName("lastName 오름차순 , firstName 내림차순 정렬 - paging()")
    @Test
    void paging(){

        List<Customer> findCustomers = jpaQueryFactory
                                        .selectFrom(customer)
                                        .orderBy(customer.lastName.asc(),customer.firstName.desc())
                                        .offset(4)
                                        .limit(3)
                                        .fetch();

        assertAll(
                ()->assertEquals("hyun2",findCustomers.get(0).getLastName()),
                ()->assertEquals("park2",findCustomers.get(1).getLastName()),
                ()->assertEquals("kim2",findCustomers.get(2).getLastName())
        );
    }






}
