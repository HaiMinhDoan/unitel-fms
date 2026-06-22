package com.unitel.fms.backend.services.impl.entity;

import com.unitel.fms.backend.entities.Customer;
import com.unitel.fms.backend.repositories.CustomerRepository;
import com.unitel.fms.backend.services.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitel.fms.backend.exceptions.customize.ConflictException;
import com.unitel.fms.backend.repositories.DispatchRequestRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService extends BaseServiceImpl<Customer, UUID> {

    private final CustomerRepository customerRepository;

    @Autowired
    private DispatchRequestRepository dispatchRequestRepository;

    @Autowired
    private EntityManager entityManager;

    public CustomerService(CustomerRepository repository) {
        super(repository);
        this.customerRepository = repository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public Customer create(Customer entity) {
        if (customerRepository.existsByCode(entity.getCode())) {
            throw new ConflictException("CUSTOMER_EXISTS");
        }
        return super.create(entity);
    }

    @Override
    @Transactional
    public Customer update(UUID id, Customer newEntity) {
        // Kiểm tra mã nếu có thay đổi
        Customer existing = getOne(id).orElseThrow();
        if (!existing.getCode().equals(newEntity.getCode())) {
            if (customerRepository.existsByCode(newEntity.getCode())) {
                throw new ConflictException("CUSTOMER_EXISTS");
            }
        }
        return super.update(id, newEntity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (dispatchRequestRepository.existsByCustomerId(id)) {
            throw new ConflictException("CANNOT_HARD_DELETE_CUSTOMER");
        }
        super.delete(id);
    }
}
