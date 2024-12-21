package ru.otus.hibernate.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

import static ru.otus.hibernate.Main.factory;

public class Repository<T> {

    public T save(T entity) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.doWork((connection) -> session.persist(entity));
            return entity;
        }
    }

    public void deleteById(Class<T> entityClass, Long id) {
        try (Session session = factory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(session.get(entityClass, id));
            transaction.commit();
        }
    }

    public T findById(Class<T> entityClass, Long id) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            return session.get(entityClass, id);
        }
    }

    public List<T> findAll(Class<T> entityClass) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            return session.createQuery("SELECT entity FROM " + entityClass.getSimpleName() + " entity", entityClass).getResultList();
        }
    }

}