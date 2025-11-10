package com.portfolio.nn.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.nn.model.Model;

@Repository
public interface ModelRepo extends JpaRepository<Model, String>{

}
