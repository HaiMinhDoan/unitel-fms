package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Customer;
import com.unitel.fms.backend.repositories.CustomerRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService extends BaseServiceImpl<Customer, UUID> {

    private final CustomerRepository customerRepository = (CustomerRepository) getRepository();

    @Autowired
    private EntityManager entityManager;

    public CustomerService(CustomerRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}
