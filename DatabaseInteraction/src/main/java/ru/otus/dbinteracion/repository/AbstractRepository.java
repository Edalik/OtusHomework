package ru.otus.dbinteracion.repository;

import ru.otus.dbinteracion.annotation.RepositoryTable;
import ru.otus.dbinteracion.connection.DataSource;
import ru.otus.dbinteracion.exception.RepositoryException;
import ru.otus.dbinteracion.util.EntityFieldsUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AbstractRepository<T> {

    private final DataSource dataSource;

    private final Class<T> entityClass;

    private PreparedStatement psInsert;

    private PreparedStatement psUpdate;

    private PreparedStatement psFindById;

    private PreparedStatement psFindAll;

    private PreparedStatement psDeleteById;

    private final EntityFieldsUtil<T> entityFieldsUtil;

    public AbstractRepository(DataSource dataSource, Class<T> entityClass) {
        this.dataSource = dataSource;
        this.entityClass = entityClass;
        this.entityFieldsUtil = new EntityFieldsUtil<>(entityClass);
        this.prepareStatements();
    }

    public void save(T entity) {
        try {
            fillParameters(entity, psInsert);
            psInsert.executeUpdate();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new RepositoryException("Couldn't save entity: " + entity, e);
        }
    }

    public void update(T entity) {
        try {
            fillParameters(entity, psUpdate);
            psUpdate.setObject(psUpdate.getParameterMetaData().getParameterCount(), entityFieldsUtil.getIdGetter().invoke(entity));
            psUpdate.executeUpdate();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new RepositoryException("Couldn't update entity: " + entity, e);
        }
    }

    public Optional<T> findById(Integer id) {
        try {
            psFindById.setInt(1, id);
            try (ResultSet rs = psFindById.executeQuery()) {
                if (rs.next()) {
                    T entity = entityFieldsUtil.mapResultSetToEntity(rs);
                    return Optional.of(entity);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Couldn't find entity with id = " + id, e);
        }
        return Optional.empty();
    }

    public List<T> findAll() {
        try (ResultSet rs = psFindAll.executeQuery()) {
            return entityFieldsUtil.mapResultSetToList(rs);
        } catch (SQLException e) {
            throw new RepositoryException("Couldn't find all entities", e);
        }
    }

    public void deleteById(Integer id) {
        try {
            psDeleteById.setInt(1, id);
            psDeleteById.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Couldn't delete entity with id = " + id, e);
        }
    }

    private void prepareStatements() {
        if (!entityClass.isAnnotationPresent(RepositoryTable.class)) {
            throw new RepositoryException("Class " + entityClass.getName() + " not annotated with @RepositoryTable");
        }

        String tableName = entityFieldsUtil.getTableName();
        if (tableName.isBlank()) {
            tableName = entityClass.getSimpleName();
        }

        entityFieldsUtil.prepareEntityData(entityClass);

        try {
            Connection connection = dataSource.getConnection();

            String insertQuery = generateInsertQuery(tableName);
            psInsert = connection.prepareStatement(insertQuery);

            String updateQuery = generateUpdateQuery(tableName);
            psUpdate = connection.prepareStatement(updateQuery);

            String findByIdQuery = generateFindByIdQuery(tableName);
            psFindById = connection.prepareStatement(findByIdQuery);

            String findAllQuery = generateFindAllQuery(tableName);
            psFindAll = connection.prepareStatement(findAllQuery);

            String deleteByIdQuery = generateDeleteByIdQuery(tableName);
            psDeleteById = connection.prepareStatement(deleteByIdQuery);
        } catch (SQLException e) {
            throw new RepositoryException("Error preparing SQL-queries for class " + entityClass.getName(), e);
        }
    }

    private String generateInsertQuery(String tableName) {
        List<Field> cachedFields = entityFieldsUtil.getCachedFields();
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        cachedFields.forEach(field -> query.append(entityFieldsUtil.getColumnName(field)).append(", "));
        query.setLength(query.length() - 2);
        query.append(") VALUES (");
        cachedFields.forEach(field -> query.append("?, "));
        query.setLength(query.length() - 2);
        query.append(")");
        return query.toString();
    }

    private String generateUpdateQuery(String tableName) {
        List<Field> cachedFields = entityFieldsUtil.getCachedFields();
        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
        cachedFields.forEach(field -> query.append(entityFieldsUtil.getColumnName(field)).append(" = ?, "));
        query.setLength(query.length() - 2);
        query.append(" WHERE ").append(entityFieldsUtil.getIdName()).append(" = ?");
        return query.toString();
    }

    private String generateFindByIdQuery(String tableName) {
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }

    private String generateFindAllQuery(String tableName) {
        return "SELECT * FROM " + tableName;
    }

    private String generateDeleteByIdQuery(String tableName) {
        return "DELETE FROM " + tableName + " WHERE id = ?";
    }

    private void fillParameters(T entity, PreparedStatement psUpdate) throws InvocationTargetException, IllegalAccessException, SQLException {
        for (int i = 0; i < entityFieldsUtil.getFieldsGetters().size(); i++) {
            Object value = entityFieldsUtil.getFieldValue(entity, i);
            psUpdate.setObject(i + 1, value);
        }
    }

}