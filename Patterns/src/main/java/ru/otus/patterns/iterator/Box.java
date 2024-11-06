package ru.otus.patterns.iterator;

import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@NoArgsConstructor
public class Box {

    private final Matryoshka red = new Matryoshka("red");

    private final Matryoshka green = new Matryoshka("green");

    private final Matryoshka blue = new Matryoshka("blue");

    private final Matryoshka magenta = new Matryoshka("magenta");

    private final List<Matryoshka> matryoshkas = List.of(red, green, blue, magenta);

    public Iterator<String> getSmallFirstIterator() {
        return new Iterator<>() {
            private int currentMatryoshka;

            private int currentItem;

            @Override
            public boolean hasNext() {
                return currentItem < 10;
            }

            @Override
            public String next() {
                String result = getResult(hasNext(), currentMatryoshka, currentItem);

                currentMatryoshka++;
                if (currentMatryoshka == 4) {
                    currentItem++;
                    currentMatryoshka = 0;
                }
                return result;
            }
        };
    }

    public Iterator<String> getColorFirstIterator() {
        return new Iterator<>() {
            private int currentMatryoshka = 0;

            private int currentItem = 0;

            @Override
            public boolean hasNext() {
                return currentMatryoshka < 4;
            }

            @Override
            public String next() {
                String result = getResult(hasNext(), currentMatryoshka, currentItem);

                currentItem++;
                if (currentItem == 10) {
                    currentItem = 0;
                    currentMatryoshka++;
                }

                return result;
            }
        };
    }

    private String getResult(boolean hasNext, int currentMatryoshka, int currentItem) {
        if (!hasNext) {
            throw new NoSuchElementException("There is no next element");
        }

        return matryoshkas.get(currentMatryoshka).getItems().get(currentItem);
    }

}