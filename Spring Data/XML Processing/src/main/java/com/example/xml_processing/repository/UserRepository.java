package com.example.xml_processing.repository;

import com.example.xml_processing.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u " +
            "WHERE (SELECT COUNT(p) FROM Product p WHERE p.seller.id = u.id AND p.buyer.id IS NOT NULL) > 0 " +
            "ORDER BY u.lastName, u.firstName")
    List<User> findAllWithMoreThanOneSoldProduct();

    @Query("SELECT u FROM User u WHERE u.soldProducts.size > 0 " +
            "ORDER BY u.soldProducts.size DESC, u.lastName")
    List<User> findAllBySoldProductsNotEmptyOrderBySoldProductsSizeDesc();
}
