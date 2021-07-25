package com.hgstudy.company;

import com.hgstudy.company.application.CompanyQueryRepository;
import com.hgstudy.company.application.CompanyRepository;
import com.hgstudy.company.entity.Company;
import com.hgstudy.company.entity.QCompany;
import com.hgstudy.company.form.CompanyDto;
import com.hgstudy.company.form.CompanyForm;
import com.hgstudy.company.form.QCompanyDto;
import com.hgstudy.customer.application.CustomerRepository;
import com.hgstudy.customer.dto.CustomerDto;
import com.hgstudy.customer.dto.QCustomerDto;
import com.hgstudy.customer.entity.Customer;
import com.hgstudy.customer.entity.QCustomer;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
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
import org.springframework.util.StringUtils;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.hgstudy.company.entity.QCompany.company;
import static com.hgstudy.customer.entity.QCustomer.customer;
import static org.assertj.core.api.Assertions.assertThat;
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

    //build clean -> classes

    @BeforeEach
    void setUp(){
        jpaQueryFactory = new JPAQueryFactory(em);
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
        final Company saveCompany = new Company("kakao","D007");
        final Customer saveCustomer = new Customer("gs","hyun","kakao");
        companyRepository.save(saveCompany);
        customerRepository.save(saveCustomer);

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
        final Company saveCompany = new Company("kakao","D007");
        final Customer saveCustomer = new Customer("gs","hyun","kakao");
        companyRepository.save(saveCompany);
        customerRepository.save(saveCustomer);

        List<Tuple> findCompaniesAndCustomers = jpaQueryFactory
                                                    .select(company.name, customer.firstName,customer.lastName)
                                                    .from(company)
                                                    .leftJoin(customer).on(company.name.eq(customer.company))
                                                    .where(company.name.eq(customer.company))
                                                    .fetch();

        assertEquals(findCompaniesAndCustomers.get(0).get(company.name),saveCompany.getName());
        assertEquals(findCompaniesAndCustomers.get(0).get(customer.firstName),saveCustomer.getFirstName());
        assertEquals(findCompaniesAndCustomers.get(0).get(customer.lastName),saveCustomer.getLastName());
    }

    @Test
    @DisplayName("부서, 고객 조인 (@QueryProjection)")
    void joinCustomerAndCompanyForQueryProjection(){
        final Company saveCompany = new Company("kakao","D007");
        final Customer saveCustomer = new Customer("gs","hyun","kakao");
        companyRepository.save(saveCompany);
        customerRepository.save(saveCustomer);

        List<CompanyDto> findCompaniesAndCustomers = jpaQueryFactory
                                                        .select(new QCompanyDto(company.name, company.department))
                                                        .from(company)
                                                        .leftJoin(customer).on(company.name.eq(customer.company))
                                                        .where(company.name.eq(customer.company))
                                                        .fetch();

        assertEquals(findCompaniesAndCustomers.get(0).getName(),saveCompany.getName());
        assertEquals(findCompaniesAndCustomers.get(0).getDepartment(),saveCompany.getDepartment());
    }

    @Test
    @DisplayName("부서, 고객 조인 + 조건 추가(@QueryProjection)")
    void joinCustomerAndCompanyForQueryProjectionWhereAny(){

        Company company1 = new Company("kakao", "D007");

        final List<Company> saveCompanies = Arrays.asList(
                new Company("kakao", "D007"),
                new Company("naver", "D008"),
                new Company("daum", "D009")
        );

        final List<Customer> saveCustomer = Arrays.asList(
                new Customer("gs","hyun","kakao"),
                new Customer("uk","kim","kakao"),
                new Customer("sk","lee","naver")
        );
        companyRepository.saveAll(saveCompanies);
        customerRepository.saveAll(saveCustomer);

        List<CompanyDto> findCompaniesAndCustomers = jpaQueryFactory
                                                        .select(new QCompanyDto(company.name, company.department))
                                                        .from(company)
                                                        .leftJoin(customer).on(company.name.eq(customer.company))
                                                        .where(company.name.eq(customer.company)
                                                        .and(customer.company.in("kakao","naver")))
                                                        .groupBy(company.name)
                                                        .fetch();

        assertThat(findCompaniesAndCustomers).hasSize(2);
        assertEquals(findCompaniesAndCustomers.get(0).getDepartment(),"D007");
        assertEquals(findCompaniesAndCustomers.get(1).getDepartment(),"D008");
//        assertEquals(findCompaniesAndCustomers.get(2).getDepartment(),"D008");
    }

    @Test
    @DisplayName("부서, 고객 조인 + 서브쿼리(@QueryProjection)")
    void joinCustomerAndCompanyForQueryProjectionSubquery(){

        Company company1 = new Company("kakao", "D007");

        final List<Company> saveCompanies = Arrays.asList(
                new Company("kakao", "D007"),
                new Company("naver", "D008"),
                new Company("daum", "D009")
        );

        final List<Customer> saveCustomer = Arrays.asList(
                new Customer("gs","hyun","kakao"),
                new Customer("uk","kim","kakao"),
                new Customer("sk","lee","naver")
        );
        companyRepository.saveAll(saveCompanies);
        customerRepository.saveAll(saveCustomer);

        List<CompanyDto> findCompaniesAndCustomers = jpaQueryFactory
                                                        .select(new QCompanyDto(company.name, company.department))
                                                        .from(company)
                                                        .leftJoin(customer).on(company.name.eq(customer.company))
                                                        .where(company.name.eq(customer.company)
                                                        .and(customer.company.eq(
                                                                JPAExpressions //subQuery
                                                                .select(company.name)
                                                                .from(company)
                                                                .where(company.name.eq("kakao"))
                                                        )))
                                                        .fetch();

        assertThat(findCompaniesAndCustomers).hasSize(2);
        assertEquals(findCompaniesAndCustomers.get(0).getDepartment(),"D007");
        assertEquals(findCompaniesAndCustomers.get(1).getDepartment(),"D007");
//        assertEquals(findCompaniesAndCustomers.get(2).getDepartment(),"D008");
    }

    @Test
    @DisplayName("부서, 고객 조인 + 동적쿼리(@QueryProjection)")
    void joinCustomerAndCompanyForQueryProjectionDynamicQuery(){


        Customer customer01 = new Customer("gs","hyun","kakao");
        Customer customer02 = new Customer("ks","lee","naver");
        Customer customer03 = new Customer("gs","kim","kakao");

        final List<Customer> saveCustomers = Arrays.asList(
                customer01, customer02, customer03
        );

        customerRepository.saveAll(saveCustomers);

        BooleanBuilder builder = new BooleanBuilder(); //동적 쿼리
        if(StringUtils.hasText(customer01.getLastName())){
            builder.and(customer.firstName.contains(customer01.getFirstName()));
        }
        if(customer01.getCompany() != null){
            builder.and(customer.company.eq("kakao"));
        }

        List<CustomerDto> findCustomers = jpaQueryFactory
                                            .select(new QCustomerDto(customer.firstName,customer.lastName,customer.company))
                                            .from(customer)
                                            .where(builder)
                                            .fetch();

        assertThat(findCustomers).hasSize(2);
        assertEquals(findCustomers.get(0).getLastName(),"hyun");
        assertEquals(findCustomers.get(1).getLastName(),"kim");
//        assertEquals(findCompaniesAndCustomers.get(2).getDepartment(),"D008");
    }



}
