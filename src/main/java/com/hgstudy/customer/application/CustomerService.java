package com.hgstudy.customer.application;

import com.hgstudy.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepositorySupport customerRepositorySupport;
    private final CustomerRepository customerRepository;


    public void save(Customer customer){
        customerRepository.save(customer);
    }

    public Customer findByLastName(String lastName){
        return customerRepositorySupport.findByLastName(lastName);
    }

}
