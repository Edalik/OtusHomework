package ru.otus.springboot.entity;

import lombok.Data;

@Data
public class Product {

    private Integer id;

    private String title;

    private Double price;

}