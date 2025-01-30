package ru.otus.springcontext.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Cart {

    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public void displayCart() {
        if (products.isEmpty()) {
            System.out.println("Your cart is empty.");
        } else {
            products.forEach(System.out::println);
        }
    }

}