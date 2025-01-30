package ru.otus.hibernate.cascade;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import ru.otus.hibernate.cascade.entity.Address;
import ru.otus.hibernate.cascade.entity.Client;
import ru.otus.hibernate.cascade.entity.Phone;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Phone.class)
                .buildSessionFactory();
             Session session = sessionFactory.openSession()
        ) {
            Transaction transaction = session.beginTransaction();

            Address address = new Address("address");
            Phone phone1 = new Phone("+79999999999");
            Phone phone2 = new Phone("+79999999998");
            Set<Phone> phones = Set.of(phone1, phone2);
            Client client = Client.builder()
                    .name("name")
                    .address(address)
                    .phones(new HashSet<>())
                    .build();
            phones.forEach(client::addPhone);

            session.persist(client);

            transaction.commit();

            Client result = session.createQuery("select c from Client c where c.id = 1", Client.class).getSingleResult();

            System.out.println(result);
        }
    }

}