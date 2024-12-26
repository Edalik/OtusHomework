package ru.otus.jdbc.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.jdbc.entity.Product;

public interface ProductRepository extends ListCrudRepository<Product, Long> {

}