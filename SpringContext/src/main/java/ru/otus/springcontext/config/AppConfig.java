package ru.otus.springcontext.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.otus.springcontext.model.Cart;
import ru.otus.springcontext.service.CartService;
import ru.otus.springcontext.repository.ProductRepository;

@Configuration
public class AppConfig {

    @Bean
    public ProductRepository productRepository() {
        return new ProductRepository();
    }

    @Bean
    public CartService cartService(ProductRepository productRepository) {
        return new CartService(productRepository);
    }

    @Bean
    @Scope("prototype")
    public Cart cart() {
        return new Cart();
    }

}