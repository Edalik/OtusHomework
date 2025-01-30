package ru.otus.springboot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.springboot.dto.ProductDTO;
import ru.otus.springboot.entity.Product;
import ru.otus.springboot.mapper.ProductMapper;
import ru.otus.springboot.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        return productMapper.entityToDTO(productRepository.getAllProducts());
    }

    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.getProductById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));

        return productMapper.entityToDTO(product);
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.DTOtoEntity(dto);

        return productMapper.entityToDTO(productRepository.addProduct(product));
    }

}