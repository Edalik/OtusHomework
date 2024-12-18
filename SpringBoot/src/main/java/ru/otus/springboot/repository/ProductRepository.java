package ru.otus.springboot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.springboot.database.ProductDatabase;
import ru.otus.springboot.entity.Product;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepository {

    private final ProductDatabase productDatabase;

    public List<Product> getAllProducts() {
        return productDatabase.products;
    }

    public Optional<Product> getProductById(Integer id) {
        return productDatabase.products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    public Product addProduct(Product product) {
        product.setId(productDatabase.getSequenceNext());
        productDatabase.products.add(product);

        return product;
    }

}