package com.portfolio.nn.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.nn.model.Result;


@Repository
public interface ResultRepo extends JpaRepository<Result,String>{

}
