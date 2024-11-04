package ru.otus.patterns.iterator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class Matryoshka {

    private final List<String> items;

    public Matryoshka(String color) {
        items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(color + i);
        }
    }

}