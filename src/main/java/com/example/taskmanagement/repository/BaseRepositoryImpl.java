package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.BaseEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@NoRepositoryBean
public class BaseRepositoryImpl<T extends BaseEntity> extends SimpleJpaRepository<T, UUID> implements BaseRepository<T, UUID> {
    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        entity.setIsDeleted(true);
        super.save(entity);
    }


    @Override
    @Transactional
    public void deleteAll() {
        super.findAll().stream().map(entity -> {
                    entity.setIsDeleted(true);
                    return entity;})
                .forEach(entity -> {
                    super.save(entity);
                });
    }


    @Override
    public T getById(UUID uuid) {
        return super.findById(uuid).orElse(null);
    }
}