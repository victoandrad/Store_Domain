package com.victoandrad.course.services;

import com.victoandrad.course.entities.Order;
import com.victoandrad.course.repositories.OrderRepository;
import com.victoandrad.course.services.exceptions.DatabaseException;
import com.victoandrad.course.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    // ==============================
    // FIELDS
    // ==============================

    private final OrderRepository repository;

    // ==============================
    // CONSTRUCTORS
    // ==============================

    @Autowired
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // ==============================
    // METHODS
    // ==============================

    public List<Order> findAll() {
        return repository.findAll();
    }

    // ==============================

    public Order findById(Long id) {
        Optional<Order> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // ==============================

    public Order insert(Order obj) {
        return repository.save(obj);
    }

    // ==============================

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch(EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    // ==============================

    public Order update(Long id, Order obj) {
        try {
            Order entity = repository.getReferenceById(id);
            updateData(entity, obj);
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // ==============================

    private void updateData(Order entity, Order obj) {
        entity.setMoment(obj.getMoment());
        entity.setStatus(obj.getStatus());
        entity.setClient(obj.getClient());
        entity.setPayment(obj.getPayment());
    }
}
