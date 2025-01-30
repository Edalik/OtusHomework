package ru.otus.springboot.database;

import org.springframework.stereotype.Component;
import ru.otus.springboot.entity.Product;

import java.util.ArrayList;
import java.util.List;


@Component
public class ProductDatabase {

    public List<Product> products = new ArrayList<>();

    public Integer getSequenceNext() {
        return products.size() + 1;
    }

}