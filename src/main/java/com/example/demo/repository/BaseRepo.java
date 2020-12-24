package com.example.demo.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface BaseRepo<T, I extends Serializable> extends JpaRepository<T, Serializable> {

}