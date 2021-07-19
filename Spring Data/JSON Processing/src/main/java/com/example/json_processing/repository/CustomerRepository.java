package com.example.json_processing.repository;

import com.example.json_processing.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c ORDER BY c.birthDate, c.youngDriver")
    List<Customer> findAllOrderByBirthDateYoungDriver();

    @Query("SELECT c FROM Customer c " +
            "WHERE (SELECT COUNT(s) FROM Sale s WHERE s.customer.id = c.id) > 0")
    List<Customer> findAllWithMoreThanOneCarBought();
}
