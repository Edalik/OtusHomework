package ru.otus.jdbc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("PRODUCT")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    private Long id;

    private String title;

    private Double price;

}