package ru.otus.jdbc.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    private String title;

    private Double price;

}