package com.hgstudy.company;

import com.hgstudy.company.application.CompanyQueryRepository;
import com.hgstudy.company.application.CompanyRepository;
import com.hgstudy.company.entity.Company;
import com.hgstudy.company.entity.QCompany;
import com.hgstudy.company.form.CompanyDto;
import com.hgstudy.company.form.CompanyForm;
import com.hgstudy.company.form.QCompanyDto;
import com.hgstudy.customer.application.CustomerRepository;
import com.hgstudy.customer.entity.Customer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.hgstudy.company.entity.QCompany.company;
import static com.hgstudy.customer.entity.QCustomer.customer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class CompanyRepositoryTest {


    @Autowired
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;

    JPAQuery jpaQuery;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CustomerRepository customerRepository;

    //@BeforeEach
//    public void init() {
//        Company saveCompany1 = new Company("HGS","D001");
//        Company saveCompany2 = new Company("LG","D002");
//        Company saveCompany3 = new Company("SSG","D003");
//        Company saveCompany4 = new Company("KAKAO","D004");
//        Company saveCompany5 = new Company("NAVER","D005");
//        Company saveCompany6 = new Company("LINE","D006");
//
//        companyRepository.save(saveCompany1);
//        companyRepository.save(saveCompany2);
//        companyRepository.save(saveCompany3);
//        companyRepository.save(saveCompany4);
//        companyRepository.save(saveCompany5);
//        companyRepository.save(saveCompany6);
//    }

    @BeforeEach
    void setUp(){
        jpaQueryFactory = new JPAQueryFactory(em);
        jpaQuery = new JPAQuery(em);
    }

    @Test
    @DisplayName("부서 찾기")
    void findByDepartment(){
        final Company company = new Company("hgstudy_","D007");
        companyRepository.save(company);

        final Company findCompany = jpaQueryFactory
                                    .selectFrom(QCompany.company)
                                    .where(QCompany.company.department.eq("D007"))
                                    .fetchOne();

        assertEquals(company.getName(),findCompany.getName());
    }


    @Test
    @DisplayName("부서, 고객 조인(Projection.constructor)")
    void joinCustomerAndCompanyForProjection(){
        final Company company01 = new Company("kakao","D007");
        final Customer customer01 = new Customer("gs","hyun","kakao");
        companyRepository.save(company01);
        customerRepository.save(customer01);

        List<Company> findCompanies = jpaQueryFactory
                                        .select(Projections.constructor(Company.class,
                                                company.name,
                                                company.department
                                                ))
                                        .from(company)
                                        .leftJoin(customer).on(company.name.eq(customer.company))
                                        .where(company.name.eq(customer.company))
                                        .fetch();

        assertEquals(findCompanies.get(0).getDepartment(),"D007");
    }

    @Test
    @DisplayName("부서, 고객 조인(Tuple)")
    void joinCustomerAndCompanyForTuple(){
        final Company company01 = new Company("kakao","D007");
        final Customer customer01 = new Customer("gs","hyun","kakao");
        companyRepository.save(company01);
        customerRepository.save(customer01);

        List<Tuple> findCompaniesAndCustomers = jpaQueryFactory
                                                    .select(company.name, customer.firstName,customer.lastName)
                                                    .from(company)
                                                    .leftJoin(customer).on(company.name.eq(customer.company))
                                                    .where(company.name.eq(customer.company))
                                                    .fetch();

        assertEquals(findCompaniesAndCustomers.get(0).get(company.name),company01.getName());
        assertEquals(findCompaniesAndCustomers.get(0).get(customer.firstName),customer01.getFirstName());
        assertEquals(findCompaniesAndCustomers.get(0).get(customer.lastName),customer01.getLastName());
    }

    @Test
    @DisplayName("부서, 고객 조인 (@QueryProjection)")
    void joinCustomerAndCompanyForQueryProjection(){
        final Company company01 = new Company("kakao","D007");
        final Customer customer01 = new Customer("gs","hyun","kakao");
        companyRepository.save(company01);
        customerRepository.save(customer01);

        List<CompanyDto> findCompaniesAndCustomers = jpaQueryFactory
                                                .select(new QCompanyDto(company.name, company.department))
                                                .from(company)
                                                .leftJoin(customer).on(company.name.eq(customer.company))
                                                .where(company.name.eq(customer.company))
                                                .fetch();

        assertEquals(findCompaniesAndCustomers.get(0).getName(),company01.getName());
        assertEquals(findCompaniesAndCustomers.get(0).getDepartment(),company01.getDepartment());
    }



}
