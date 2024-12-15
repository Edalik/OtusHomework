package ru.otus.springcontext.repository;

import lombok.Data;
import ru.otus.springcontext.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class ProductRepository {

    private List<Product> products = new ArrayList<>();

    public ProductRepository() {
        for (int i = 1; i <= 10; i++) {
            products.add(new Product(i, "Product" + i, i * 10d));
        }
    }

    public Optional<Product> getProductById(Integer id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

}