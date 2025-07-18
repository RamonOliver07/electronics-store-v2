package com.ecommerce.bo;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dto.CreateProductDTO;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.entity.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductBO {
    
    @Inject
    ProductDAO productDAO;
    
    @Transactional
    public ProductDTO createProduct(CreateProductDTO createProductDTO) {
        validateCreateProduct(createProductDTO);
        
        Product product = new Product(
            createProductDTO.name,
            createProductDTO.description,
            createProductDTO.price,
            createProductDTO.stock,
            createProductDTO.category,
            createProductDTO.brand,
            createProductDTO.imageUrl
        );
        
        productDAO.persist(product);
        return convertToDTO(product);
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, CreateProductDTO updateProductDTO) {
        validateCreateProduct(updateProductDTO);
        
        Optional<Product> productOpt = productDAO.findByIdOptional(id);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Product product = productOpt.get();
        product.name = updateProductDTO.name;
        product.description = updateProductDTO.description;
        product.price = updateProductDTO.price;
        product.stock = updateProductDTO.stock;
        product.category = updateProductDTO.category;
        product.brand = updateProductDTO.brand;
        product.imageUrl = updateProductDTO.imageUrl;
        
        productDAO.persist(product);
        return convertToDTO(product);
    }
    
    public List<ProductDTO> getAllProducts() {
        return productDAO.findActiveProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> getProductsByCategory(String category) {
        return productDAO.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> searchProducts(String name) {
        return productDAO.searchByName(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductDTO> getProductById(Long id) {
        return productDAO.findByIdOptional(id)
                .filter(product -> product.active)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public void deactivateProduct(Long id) {
        Optional<Product> productOpt = productDAO.findByIdOptional(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.active = false;
            productDAO.persist(product);
        }
    }
    
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productDAO.findByIdOptional(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.stock >= quantity) {
                product.stock -= quantity;
                productDAO.persist(product);
            } else {
                throw new RuntimeException("Insufficient stock");
            }
        } else {
            throw new RuntimeException("Product not found");
        }
    }
    
    private void validateCreateProduct(CreateProductDTO dto) {
        if (dto.name == null || dto.name.trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (dto.price == null || dto.price.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Product price must be greater than zero");
        }
        if (dto.stock == null || dto.stock < 0) {
            throw new RuntimeException("Product stock cannot be negative");
        }
        if (dto.category == null || dto.category.trim().isEmpty()) {
            throw new RuntimeException("Product category is required");
        }
    }
    
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.id,
            product.name,
            product.description,
            product.price,
            product.stock,
            product.category,
            product.brand,
            product.imageUrl,
            product.active
        );
    }
}