package ru.otus.patterns.iterator;

import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

@NoArgsConstructor
public class Box {

    private final Matryoshka red = new Matryoshka("red");

    private final Matryoshka green = new Matryoshka("green");

    private final Matryoshka blue = new Matryoshka("blue");

    private final Matryoshka magenta = new Matryoshka("magenta");

    // expected: "red0", "green0", "blue0", "magenta0", "red1", "green1", "blue1", "magenta1",...
    public Iterator<String> getSmallFirstIterator() {
        return new Iterator<>() {
            private int currentMatryoshka = 0;

            private int currentItem = 0;

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

    // expected: "red0", "red1", ..., "red9", "green0", "green1", ..., "green9", ...
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
            throw new NoSuchElementException();
        }

        String result = null;

        if (currentMatryoshka == 0) {
            result = red.getItems().get(currentItem);
        } else if (currentMatryoshka == 1) {
            result = green.getItems().get(currentItem);
        } else if (currentMatryoshka == 2) {
            result = blue.getItems().get(currentItem);
        } else if (currentMatryoshka == 3) {
            result = magenta.getItems().get(currentItem);
        }
        return result;
    }

}