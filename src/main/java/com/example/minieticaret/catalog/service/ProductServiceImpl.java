package com.example.minieticaret.catalog.service;

import com.example.minieticaret.catalog.domain.Category;
import com.example.minieticaret.catalog.domain.Product;
import com.example.minieticaret.catalog.dto.ProductRequest;
import com.example.minieticaret.catalog.dto.ProductResponse;
import com.example.minieticaret.catalog.mapper.ProductMapper;
import com.example.minieticaret.catalog.repository.CategoryRepository;
import com.example.minieticaret.catalog.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("SKU zaten kullaniliyor");
        }
        Category category = findCategory(request.categoryId());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        if (request.images() != null) {
            product.setImages(request.images());
        }
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findProduct(id);
        if (!product.getSku().equals(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("SKU zaten kullaniliyor");
        }
        Category category = findCategory(request.categoryId());
        productMapper.updateEntity(request, product);
        product.setCategory(category);
        if (request.images() != null) {
            product.setImages(request.images());
        }
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = findProduct(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        return productMapper.toResponse(findProduct(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Urun bulunamadi"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> list(UUID categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Urun bulunamadi"));
    }

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadi"));
    }
}
