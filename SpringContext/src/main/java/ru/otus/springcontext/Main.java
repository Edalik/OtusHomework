package ru.otus.springcontext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.otus.springcontext.config.AppConfig;
import ru.otus.springcontext.model.Cart;
import ru.otus.springcontext.service.CartService;

@ComponentScan
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        CartService cartService = context.getBean(CartService.class);

        System.out.println("Products:");
        cartService.showAllProducts();

        Cart cart1 = context.getBean(Cart.class);
        cartService.addToCart(cart1, 1);
        cartService.addToCart(cart1, 2);
        cartService.addToCart(cart1, 3);
        System.out.println("Cart 1:");
        cart1.displayCart();

        Cart cart2 = context.getBean(Cart.class);
        cartService.addToCart(cart2, 4);
        cartService.addToCart(cart2, 5);
        System.out.println("Cart 2:");
        cart2.displayCart();

        cartService.removeFromCart(cart1, 3);
        System.out.println("Cart 1 after removal:");
        cart1.displayCart();
    }

}