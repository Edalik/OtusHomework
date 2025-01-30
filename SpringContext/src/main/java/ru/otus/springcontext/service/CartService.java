package ru.otus.springcontext.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.springcontext.model.Cart;
import ru.otus.springcontext.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;

    public void showAllProducts() {
        productRepository.getProducts().forEach(System.out::println);
    }

    public void addToCart(Cart cart, Integer productId) {
        productRepository.getProductById(productId)
                .ifPresent(cart::addProduct);
    }

    public void removeFromCart(Cart cart, Integer productId) {
        productRepository.getProductById(productId)
                .ifPresent(cart::removeProduct);
    }

}