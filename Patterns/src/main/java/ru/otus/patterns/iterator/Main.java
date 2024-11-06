package ru.otus.patterns.iterator;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        Box box = new Box();

        System.out.println("Small First Iterator:");
        Iterator<String> smallFirst = box.getSmallFirstIterator();
        while (smallFirst.hasNext()) {
            System.out.println(smallFirst.next());
        }

        System.out.println("\nColor First Iterator:");
        Iterator<String> colorFirst = box.getColorFirstIterator();
        while (colorFirst.hasNext()) {
            System.out.println(colorFirst.next());
        }
    }

}