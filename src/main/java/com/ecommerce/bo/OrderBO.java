package com.ecommerce.bo;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.dto.CreateOrderDTO;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderBO {
    
    @Inject
    OrderDAO orderDAO;
    
    @Inject
    UserDAO userDAO;
    
    @Inject
    ProductDAO productDAO;
    
    @Inject
    ProductBO productBO;
    
    @Transactional
    public OrderDTO createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        validateCreateOrder(createOrderDTO);
        
        Optional<User> userOpt = userDAO.findByIdOptional(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        // Create order first
        Order order = new Order(user, total);
        orderDAO.persist(order);
        
        // Process each item
        for (var itemDTO : createOrderDTO.items) {
            Optional<Product> productOpt = productDAO.findByIdOptional(itemDTO.productId);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Product not found: " + itemDTO.productId);
            }
            
            Product product = productOpt.get();
            if (product.stock < itemDTO.quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.name);
            }
            
            BigDecimal itemTotal = product.price.multiply(BigDecimal.valueOf(itemDTO.quantity));
            total = total.add(itemTotal);
            
            OrderItem orderItem = new OrderItem(order, product, itemDTO.quantity, product.price);
            orderItems.add(orderItem);
            
            // Update product stock
            productBO.updateStock(product.id, itemDTO.quantity);
        }
        
        // Update order total and items
        order.total = total;
        order.items = orderItems;
        orderDAO.persist(order);
        
        return convertToDTO(order);
    }
    
    public List<OrderDTO> getUserOrders(Long userId) {
        Optional<User> userOpt = userDAO.findByIdOptional(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return orderDAO.findByUser(userOpt.get()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<OrderDTO> getAllOrders() {
        return orderDAO.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderDAO.findByIdOptional(id)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderDAO.findByIdOptional(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.status = status;
            orderDAO.persist(order);
        }
    }
    
    private void validateCreateOrder(CreateOrderDTO dto) {
        if (dto.items == null || dto.items.isEmpty()) {
            throw new RuntimeException("Order must have at least one item");
        }
        
        for (var item : dto.items) {
            if (item.productId == null) {
                throw new RuntimeException("Product ID is required for all items");
            }
            if (item.quantity == null || item.quantity <= 0) {
                throw new RuntimeException("Quantity must be greater than zero");
            }
        }
    }
    
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        
        return new OrderDTO(
            order.id,
            order.user.id,
            order.user.name,
            order.orderDate,
            order.total,
            order.status.name(),
            itemDTOs
        );
    }
    
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        return new OrderItemDTO(
            item.id,
            item.product.id,
            item.product.name,
            item.quantity,
            item.price
        );
    }
}