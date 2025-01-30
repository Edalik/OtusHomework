package ru.otus.jdbc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.jdbc.dto.ProductDTO;
import ru.otus.jdbc.entity.Product;
import ru.otus.jdbc.mapper.ProductMapper;
import ru.otus.jdbc.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public Product upsertProduct(ProductDTO dto) {
        Product product = productMapper.DTOtoEntity(dto);

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

}