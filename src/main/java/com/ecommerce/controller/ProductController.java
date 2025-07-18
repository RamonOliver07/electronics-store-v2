package com.ecommerce.controller;

import com.ecommerce.bo.ProductBO;
import com.ecommerce.dto.CreateProductDTO;
import com.ecommerce.dto.ProductDTO;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {
    
    @Inject
    ProductBO productBO;
    
    @GET
    @RolesAllowed({"ADMIN", "USER"})
    public List<ProductDTO> getAllProducts(@QueryParam("category") String category, @QueryParam("search") String search) {
        if (category != null && !category.trim().isEmpty()) {
            return productBO.getProductsByCategory(category);
        } else if (search != null && !search.trim().isEmpty()) {
            return productBO.searchProducts(search);
        }
        return productBO.getAllProducts();
    }
    
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getProductById(@PathParam("id") Long id) {
        Optional<ProductDTO> product = productBO.getProductById(id);
        if (product.isPresent()) {
            return Response.ok(product.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Product not found")).build();
    }
    
    @POST
    @RolesAllowed({"ADMIN"})
    public Response createProduct(CreateProductDTO createProductDTO) {
        try {
            ProductDTO product = productBO.createProduct(createProductDTO);
            return Response.status(Response.Status.CREATED).entity(product).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response updateProduct(@PathParam("id") Long id, CreateProductDTO updateProductDTO) {
        try {
            ProductDTO product = productBO.updateProduct(id, updateProductDTO);
            return Response.ok(product).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deactivateProduct(@PathParam("id") Long id) {
        productBO.deactivateProduct(id);
        return Response.ok(Map.of("message", "Product deactivated successfully")).build();
    }
}