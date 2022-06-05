package com.evans.repository;

import org.springframework.stereotype.Repository;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

import com.evans.repository.Bill;
import java.lang.Long;

//@Repository
public interface BillRepository extends CrudRepository<Bill, Long> {

  List<Bill> findAll();
}