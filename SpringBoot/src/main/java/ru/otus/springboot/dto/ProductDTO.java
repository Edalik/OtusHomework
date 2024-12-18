package ru.otus.springboot.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Integer id;

    private String title;

    private Double price;

}