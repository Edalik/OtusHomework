package ru.otus.jdbc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    public ProductDTO upsertProduct(ProductDTO dto) {
        Product product = productMapper.DTOtoEntity(dto);

        return productMapper.entityToDTO(productRepository.save(product));
    }

    public List<ProductDTO> getAllProducts() {
        return productMapper.entityToDTO(productRepository.findAll());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));

        return productMapper.entityToDTO(product);
    }

    public ResponseEntity<?> deleteProductById(Long id) {
        productRepository.deleteById(id);

        return ResponseEntity.status(204).build();
    }

}