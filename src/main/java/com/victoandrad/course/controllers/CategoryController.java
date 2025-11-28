package com.victoandrad.course.controllers;

import com.victoandrad.course.entities.Category;
import com.victoandrad.course.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {

    // ==============================
    // FIELDS
    // ==============================

    private final CategoryService service;

    // ==============================
    // CONSTRUCTORS
    // ==============================

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    // ==============================
    // METHODS
    // ==============================

    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    // ==============================

    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id) {
        Category obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    // ==============================

    @PostMapping
    public ResponseEntity<Category> insert(@RequestBody Category obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    // ==============================

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==============================

    @PutMapping(value = "/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }
}
