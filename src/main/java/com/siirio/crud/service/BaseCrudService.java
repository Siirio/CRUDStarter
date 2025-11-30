package com.siirio.crud.service;
import com.siirio.crud.mapper.GenericMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BaseCrudService<T, ID, M extends GenericMapper<T>> {
    protected final JpaRepository<T, ID> jpaRepository;
    protected final M mapper;

    public BaseCrudService(JpaRepository<T, ID> jpaRepository, M mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    // Create
    public T save(T entity) {
        return jpaRepository.save(entity);
    }

    // Read
    public List<T> findAll() { return jpaRepository.findAll(); }
    public Optional<T> findById(ID id) { return jpaRepository.findById(id); }

    //Update
    public T update(ID id, T newEntity) {
        T existingEntity = jpaRepository.findById(id).orElseThrow(() -> new RuntimeException("No such ID exists"));
        mapper.updateFromDto(newEntity, existingEntity);
        return jpaRepository.save(existingEntity);
    }

    // Delete
    public void deleteById(ID id) {
        jpaRepository.deleteById(id);
    }
}