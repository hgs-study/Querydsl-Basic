package com.hgstudy.customer.api;

import com.hgstudy.customer.application.CustomerService;
import com.hgstudy.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/customers")
    public String save(){
        Customer customer = new Customer("firstName","hyun");
        customerService.save(customer);

        return "저장되었습니다.";
    }

    @GetMapping("/customers/{lastName}")
    public Customer findByFirstName(@PathVariable String lastName){
        return customerService.findByLastName(lastName);
    }
}
