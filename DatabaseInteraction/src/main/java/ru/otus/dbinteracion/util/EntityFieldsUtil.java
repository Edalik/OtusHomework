package ru.otus.dbinteracion.util;

import lombok.Getter;
import lombok.Setter;
import ru.otus.dbinteracion.annotation.RepositoryField;
import ru.otus.dbinteracion.annotation.RepositoryIdField;
import ru.otus.dbinteracion.annotation.RepositoryTable;
import ru.otus.dbinteracion.exception.RepositoryException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityFieldsUtil<T> {

    private final Class<T> entityClass;

    private List<Field> cachedFields;

    private final List<Method> fieldsGetters = new ArrayList<>();

    private final List<Method> fieldsSetters = new ArrayList<>();

    private Field idField;

    private Method idGetter;

    private Method idSetter;

    public EntityFieldsUtil(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void prepareEntityData(Class<T> clazz) {
        cachedFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(RepositoryField.class))
                .collect(Collectors.toList());

        if (cachedFields.isEmpty()) {
            throw new RepositoryException("No fields mapped with annotation @RepositoryField in class: "
                    + clazz.getName());
        }

        for (Field field : cachedFields) {
            String fieldName = field.getName();
            String getterMethodName = "get" + capitalizeFirstLetter(fieldName);
            String setterMethodName = "set" + capitalizeFirstLetter(fieldName);
            try {
                Method getterMethod = clazz.getMethod(getterMethodName);
                fieldsGetters.add(getterMethod);
                Method setterMethod = clazz.getMethod(setterMethodName, field.getType());
                fieldsSetters.add(setterMethod);
            } catch (NoSuchMethodException e) {
                throw new RepositoryException("No getter or setter method found for field: " + fieldName, e);
            }
        }

        idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(RepositoryIdField.class))
                .findFirst()
                .orElseThrow(() -> new RepositoryException("Class " + clazz.getName() + " doesn't have id"));

        try {
            String idFieldName = idField.getName();
            idGetter = clazz.getMethod("get" + capitalizeFirstLetter(idFieldName));
            idSetter = clazz.getMethod("set" + capitalizeFirstLetter(idFieldName), idField.getType());
        } catch (NoSuchMethodException e) {
            throw new RepositoryException("No getter or setter method found for id field: " + idField.getName(), e);
        }
    }

    public Object getFieldValue(T entity, Integer index) throws InvocationTargetException, IllegalAccessException {
        Method getter = fieldsGetters.get(index);
        return getter.invoke(entity);
    }

    public T mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (int i = 0; i < cachedFields.size(); i++) {
                String columnName = getColumnName(cachedFields.get(i));
                Object value = rs.getObject(columnName);
                fieldsSetters.get(i).invoke(entity, value);
            }
            idSetter.invoke(entity, rs.getObject(getIdName()));
            return entity;
        } catch (Exception e) {
            throw new SQLException("Error mapping entity: " + rs, e);
        }
    }

    public List<T> mapResultSetToList(ResultSet rs) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (rs.next()) {
            T entity = mapResultSetToEntity(rs);
            entities.add(entity);
        }
        return entities;
    }

    public String getTableName() {
        RepositoryTable annotation = entityClass.getAnnotation(RepositoryTable.class);
        return annotation.value();
    }

    public String getColumnName(Field field) {
        String name = field.getAnnotation(RepositoryField.class).value();

        return name.isBlank() ? field.getName() : name;
    }

    public String getIdName() {
        String name = idField.getAnnotation(RepositoryIdField.class).value();

        return name.isBlank() ? idField.getName() : name;
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}