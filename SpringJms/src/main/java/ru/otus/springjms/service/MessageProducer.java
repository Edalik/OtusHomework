package ru.otus.springjms.service;

public interface MessageProducer {

    void sendMessage(String destination, String text);

}