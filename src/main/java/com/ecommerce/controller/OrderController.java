package com.ecommerce.controller;

import com.ecommerce.bo.OrderBO;
import com.ecommerce.dto.CreateOrderDTO;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.entity.Order;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController {
    
    @Inject
    OrderBO orderBO;
    
    @Inject
    SecurityIdentity securityIdentity;
    
    @POST
    @RolesAllowed({"USER", "ADMIN"})
    public Response createOrder(CreateOrderDTO createOrderDTO) {
        try {
            Long userId = Long.valueOf(securityIdentity.getPrincipal().getName());
            OrderDTO order = orderBO.createOrder(userId, createOrderDTO);
            return Response.status(Response.Status.CREATED).entity(order).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @GET
    @Path("/my-orders")
    @RolesAllowed({"USER", "ADMIN"})
    public List<OrderDTO> getUserOrders() {
        Long userId = Long.valueOf(securityIdentity.getPrincipal().getName());
        return orderBO.getUserOrders(userId);
    }
    
    @GET
    @RolesAllowed({"ADMIN"})
    public List<OrderDTO> getAllOrders() {
        return orderBO.getAllOrders();
    }
    
    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getOrderById(@PathParam("id") Long id) {
        Optional<OrderDTO> order = orderBO.getOrderById(id);
        if (order.isPresent()) {
            return Response.ok(order.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Order not found")).build();
    }
    
    @PUT
    @Path("/{id}/status")
    @RolesAllowed({"ADMIN"})
    public Response updateOrderStatus(@PathParam("id") Long id, Map<String, String> statusUpdate) {
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusUpdate.get("status"));
            orderBO.updateOrderStatus(id, status);
            return Response.ok(Map.of("message", "Order status updated successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid status")).build();
        }
    }
}