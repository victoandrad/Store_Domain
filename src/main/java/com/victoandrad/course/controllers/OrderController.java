package com.victoandrad.course.controllers;

import com.victoandrad.course.entities.Order;
import com.victoandrad.course.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    // ==============================
    // FIELDS
    // ==============================

    private final OrderService service;

    // ==============================
    // CONSTRUCTORS
    // ==============================

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    // ==============================
    // METHODS
    // ==============================

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        List<Order> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    // ==============================

    @GetMapping(value = "/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        Order obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    // ==============================

    @PostMapping
    public ResponseEntity<Order> insert(@RequestBody Order obj) {
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
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }
}
