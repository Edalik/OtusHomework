package ru.otus.httpprotocol.service;

import ru.otus.httpprotocol.exception.BusinessException;
import ru.otus.httpprotocol.exception.RequestSizeExceededException;
import ru.otus.httpprotocol.model.HttpRequest;
import ru.otus.httpprotocol.util.RequestParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final int port;

    private final int threadPoolSize;

    private final int maxRequestSize;

    private volatile boolean running;

    private final ExecutorService threadPool;

    public HttpServer(String configFileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new FileNotFoundException("Property file '" + configFileName + "' not found in resources");
            }
            properties.load(input);
        }
        this.port = Integer.parseInt(properties.getProperty("port", "8088"));
        this.threadPoolSize = Integer.parseInt(properties.getProperty("threadPoolSize", "10"));
        this.maxRequestSize = Integer.parseInt(properties.getProperty("maxRequestSize", "5242880"));
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        this.running = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(() -> handleClient(clientSocket, serverSocket));
                } catch (IOException e) {
                    if (!running) {
                        break;
                    }
                    System.out.println("Server accept failed");
                    throw new BusinessException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while listening on port " + port);
            throw new BusinessException(e);
        } finally {
            threadPool.shutdown();
            System.out.println("Server stopped.");
        }
    }

    private void handleClient(Socket clientSocket, ServerSocket serverSocket) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(clientSocket.getOutputStream());

            InputStream input = clientSocket.getInputStream();
            RequestParser requestParser = new RequestParser();

            HttpRequest request = requestParser.parseRequest(input, maxRequestSize);
            if ("GET".equalsIgnoreCase(request.getMethod()) && "/shutdown".equals(request.getUri())) {
                sendResponse(writer, 200, "Server shutting down");
                stop(serverSocket);
            } else {
                sendResponse(writer, 200, request.toString());
            }

        } catch (RequestSizeExceededException e) {
            System.out.println(e.getMessage());
            if (writer != null) {
                sendResponse(writer, 400, e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (writer != null) {
                sendResponse(writer, 500, "Internal Server Error");
            }
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }

            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }


    private void sendResponse(PrintWriter writer, int statusCode, String message) {
        writer.println("HTTP/1.1 " + statusCode + " " + (statusCode == 200 ? "OK" : "Internal Server Error"));
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + message.length());
        writer.println();
        writer.print(message);
        writer.flush();
    }

    public void stop(ServerSocket serverSocket) {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing server socket");
            throw new BusinessException(e);
        }
    }

    public static void main(String[] args) {
        try {
            HttpServer server = new HttpServer("server.properties");
            server.start();
        } catch (IOException e) {
            System.out.println("Error while starting server");
            throw new BusinessException(e);
        }
    }

}