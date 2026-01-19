package com.demo.cqrsproject.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderViewRepository extends JpaRepository<OrderView, String> {
    List<OrderView> findByCustomerId(String customerId);
    List<OrderView> findByStatus(String status);
    List<OrderView> findByCustomerIdAndStatus(String customerId, String status);
}
