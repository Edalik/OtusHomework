package ru.otus.patterns.proxy;

public class Main {
    public static void main(String[] args) {
        ItemsServiceProxy itemsServiceProxy = new ItemsServiceProxy();
        itemsServiceProxy.saveItems();
        itemsServiceProxy.increasePrices();
    }
}