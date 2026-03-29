package com.banking.repository;

import com.banking.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillId(String billId);
    List<Bill> findByCustomerId(Long customerId);
    List<Bill> findByCustomerIdAndStatus(Long customerId, String status);
}
