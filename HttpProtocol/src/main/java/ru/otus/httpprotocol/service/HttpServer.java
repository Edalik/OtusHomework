package ru.otus.httpprotocol.service;

import ru.otus.httpprotocol.exception.BusinessException;
import ru.otus.httpprotocol.model.HttpRequest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        try (InputStream input = clientSocket.getInputStream();
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream())) {

            HttpRequest request = parseRequest(input);
            if ("GET".equalsIgnoreCase(request.getMethod()) && "/shutdown".equals(request.getUri())) {
                sendResponse(writer, 200, "Server shutting down");
                stop(serverSocket);
            } else {
                sendResponse(writer, 200, request.toString());
            }

        } catch (Exception e) {
            try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream())) {
                sendResponse(writer, 500, "Internal Server Error");
            } catch (IOException ignored) {
            }
        }
    }

    private HttpRequest parseRequest(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HttpRequest request = new HttpRequest();

        String startLine = reader.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IOException("Empty request");
        }
        String[] requestLine = startLine.split(" ");
        if (requestLine.length != 3) {
            throw new IOException("Invalid request line");
        }
        request.setMethod(requestLine[0]);
        String[] uriParts = requestLine[1].split("\\?");
        request.setUri(uriParts[0]);

        if (uriParts.length > 1) {
            for (String param : uriParts[1].split("&")) {
                String[] keyValue = param.split("=");
                request.getQueryParams().put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }

        String line;
        int totalBytesRead = startLine.length() + 2;
        while (!(line = reader.readLine()).isEmpty()) {
            totalBytesRead += line.length() + 2;
            if (totalBytesRead > maxRequestSize) {
                throw new IOException("Request size exceeds the maximum allowed size");
            }
            String[] header = line.split(": ", 2);
            request.getHeaders().put(header[0], header[1]);
        }

        String contentLengthHeader = request.getHeaders().get("Content-Length");
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            if (contentLength > 0) {
                if (totalBytesRead + contentLength > maxRequestSize) {
                    throw new IOException("Request size exceeds the maximum allowed size");
                }

                char[] bodyBuffer = new char[contentLength];
                int bodyBytesRead = reader.read(bodyBuffer, 0, contentLength);
                if (bodyBytesRead != contentLength) {
                    throw new IOException("Failed to read full request body");
                }
                request.setBody(new String(bodyBuffer, 0, bodyBytesRead));
            }
        }

        return request;
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