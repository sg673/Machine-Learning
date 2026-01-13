package com.portfolio.nn.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.nn.model.CNNModel;

@Repository
public interface CNNRepo extends JpaRepository<CNNModel, Object> {

}
