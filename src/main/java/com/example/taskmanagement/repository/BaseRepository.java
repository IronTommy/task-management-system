package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, UUID> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
    void delete(T entity);

    void deleteAll();


    T getById(UUID uuid);


}
