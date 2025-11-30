package com.siirio.crud.controller;

import com.siirio.crud.service.BaseCrudService;
import com.siirio.crud.mapper.GenericMapper; // Assuming the GenericMapper exists for full type safety
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// T must be the Entity class, ID must be the primary key type.
// M is the GenericMapper type, necessary for the BaseCrudService definition.
public class BaseCrudController<T, ID, M extends GenericMapper<T>> {

    // Must use the fully parameterized BaseCrudService
    protected final BaseCrudService<T, ID, M> baseCrudService;

    // Use the fully parameterized type in the constructor
    public BaseCrudController(BaseCrudService<T, ID, M> baseCrudService) {
        this.baseCrudService = baseCrudService;
    }

    // --- READ OPERATIONS ---

    @GetMapping // GET /resource
    public List<T> readAll() {
        return baseCrudService.findAll();
    }

    @GetMapping("/{id}") // GET /resource/{id}
    public Optional<T> readOne(@PathVariable ID id) {
        // Must use @PathVariable to bind the URL segment to the ID argument
        return baseCrudService.findById(id);
    }

    // --- CREATE OPERATION ---

    @PostMapping // POST /resource
    public T create(@RequestBody T entity) {
        // Must use @RequestBody to get the entity from the request body
        // Assuming the service has a 'create' or 'save' method
        return baseCrudService.save(entity);
    }

    // --- UPDATE OPERATION ---

    @PutMapping("/{id}") // PUT /resource/{id} (Standard for update)
    public T update(@PathVariable ID id, @RequestBody T entity) {
        // Assuming the service has a dedicated 'update' method for safe logic
        return baseCrudService.update(id, entity);
    }

    // --- DELETE OPERATION ---

    @DeleteMapping("/{id}") // DELETE /resource/{id} (Standard for delete)
    public void delete(@PathVariable ID id) {
        baseCrudService.deleteById(id);
    }
}