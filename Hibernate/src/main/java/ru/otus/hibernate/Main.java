package ru.otus.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.hibernate.Entity.Customer;
import ru.otus.hibernate.Entity.Product;
import ru.otus.hibernate.Entity.Purchase;
import ru.otus.hibernate.repository.Repository;

import java.util.Random;

public class Main {

    private static final Integer startId = 1;
    private static final Integer count = 5;

    public static SessionFactory factory;

    public static Repository<Customer> customerRepository = new Repository<>();

    public static Repository<Product> productRepository = new Repository<>();

    public static Repository<Purchase> purchaseRepository = new Repository<>();

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Purchase.class)
                .buildSessionFactory()
        ) {
            factory = sessionFactory;

            insertCustomers();

            insertProducts();

            insertPurchases();

            printEntries();

            deleteEntries();

            System.out.println("\nAfter deletion\n");

            printEntries();
        }
    }

    private static void insertCustomers() {
        for (int i = startId; i <= count; i++) {
            Customer customer = new Customer("Customer" + i);
            customerRepository.save(customer);
        }
    }

    private static void insertProducts() {
        for (int i = startId; i <= count; i++) {
            Product product = new Product("Product" + i, i);
            productRepository.save(product);
        }
    }

    private static void insertPurchases() {
        for (int i = startId; i <= count; i++) {
            Customer customer = customerRepository.findById(Customer.class, (long) i);
            for (int j = i; j <= count; j++) {
                Product product = productRepository.findById(Product.class, (long) j);
                Purchase purchase = new Purchase(customer, product, product.getPrice());
                purchaseRepository.save(purchase);
            }
        }
    }

    private static void deleteEntries() {
        System.out.println("\nDeletion\n");

        Random rand = new Random();
        for (int i = startId; i <= count; i++) {
            if (rand.nextBoolean()) {
                productRepository.deleteById(Product.class, (long) i);
                System.out.println("Deleted product with id: " + i);
            }
            if (rand.nextBoolean()) {
                customerRepository.deleteById(Customer.class, (long) i);
                System.out.println("Deleted customer with id: " + i);
            }
        }
    }

    private static void printEntries() {
        for (int i = startId; i <= count; i++) {
            printProductsBoughtByCustomer((long) i);
            printCustomersWhoBoughtProduct((long) i);
        }
        printPurchases();
    }

    private static void printPurchases() {
        System.out.println("\nAll purchases\n");
        purchaseRepository.findAll(Purchase.class).forEach(System.out::println);
    }

    private static void printProductsBoughtByCustomer(Long customerId) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();

            Customer customer = session.get(Customer.class, customerId);
            if (customer == null) {
                return;
            }
            System.out.println("Products bought by " + customer + ":");
            for (Product product : customer.getProducts()) {
                System.out.println(" - " + product);
            }

            session.getTransaction().commit();
        }
    }

    private static void printCustomersWhoBoughtProduct(Long productId) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();

            Product product = session.get(Product.class, productId);
            if (product == null) {
                return;
            }
            System.out.println("Customers who bought " + product + ":");
            for (Customer customer : product.getCustomers()) {
                System.out.println(" - " + customer);
            }

            session.getTransaction().commit();
        }
    }

}