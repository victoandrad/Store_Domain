package com.victoandrad.course.services;

import com.victoandrad.course.entities.Order;
import com.victoandrad.course.entities.OrderItem;
import com.victoandrad.course.entities.Payment;
import com.victoandrad.course.repositories.OrderItemRepository;
import com.victoandrad.course.repositories.OrderRepository;
import com.victoandrad.course.repositories.ProductRepository;
import com.victoandrad.course.repositories.UserRepository;
import com.victoandrad.course.services.exceptions.DatabaseException;
import com.victoandrad.course.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    // ==============================
    // FIELDS
    // ==============================

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ==============================
    // CONSTRUCTORS
    // ==============================

    @Autowired
    public OrderService(OrderRepository repository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, UserRepository userRepository) {
        this.repository = repository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public Order insert(Order obj) {
        // Validar e carregar o cliente
        if (obj.getClient() == null || obj.getClient().getId() == null) {
            throw new IllegalArgumentException("Client ID is required");
        }
        obj.setClient(userRepository.findById(obj.getClient().getId())
                .orElseThrow(() -> new ResourceNotFoundException(obj.getClient().getId())));

        // Definir o momento do pedido se não foi informado
        if (obj.getMoment() == null) {
            obj.setMoment(Instant.now());
        }

        // Processar o pagamento ANTES de salvar (para usar o cascade corretamente)
        if (obj.getPayment() != null) {
            Payment payment = obj.getPayment();
            if (payment.getMoment() == null) {
                payment.setMoment(Instant.now());
            }
            payment.setOrder(obj);
            obj.setPayment(payment);
        }

        // Salvar o pedido (com payment em cascade)
        Order savedOrder = repository.save(obj);

        // Processar e salvar os itens do pedido
        if (obj.getItems() != null && !obj.getItems().isEmpty()) {
            // Criar uma lista temporária para evitar ConcurrentModificationException
            var itemsToProcess = new java.util.ArrayList<>(obj.getItems());
            obj.getItems().clear(); // Limpar a coleção gerenciada pelo Hibernate
            
            for (OrderItem item : itemsToProcess) {
                // Validar e carregar o produto
                if (item.getProduct() == null || item.getProduct().getId() == null) {
                    throw new IllegalArgumentException("Product ID is required for order items");
                }
                var product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(item.getProduct().getId()));
                
                // Criar novo item associado ao pedido salvo
                OrderItem newItem = new OrderItem(
                    savedOrder,
                    product,
                    item.getQuantity(),
                    item.getPrice() != null ? item.getPrice() : product.getPrice() // Usar preço do produto se não fornecido
                );
                orderItemRepository.save(newItem);
                savedOrder.getItems().add(newItem);
            }
        }

        return savedOrder;
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
