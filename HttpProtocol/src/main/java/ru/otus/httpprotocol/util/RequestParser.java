package ru.otus.httpprotocol.util;

import ru.otus.httpprotocol.exception.RequestSizeExceededException;
import ru.otus.httpprotocol.model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {

    public static final String CONTENT_LENGTH = "Content-Length";

    StringBuilder stringBuilder = ThreadLocal.withInitial(StringBuilder::new).get();

    int totalBytesRead;

    public HttpRequest parseRequest(InputStream input, int maxRequestSize) throws IOException, RequestSizeExceededException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HttpRequest request = new HttpRequest();
        totalBytesRead = 0;

        parseStartLine(reader, request, maxRequestSize);

        parseHeaders(reader, request, maxRequestSize);

        parseBody(reader, request, maxRequestSize);

        return request;
    }

    private void parseStartLine(BufferedReader reader, HttpRequest request, int maxRequestSize) throws IOException {
        stringBuilder.append(reader.readLine());
        totalBytesRead += stringBuilder.length();
        if (stringBuilder.isEmpty()) {
            throw new IOException("Empty request");
        } else if (totalBytesRead > maxRequestSize) {
            throw new RequestSizeExceededException();
        }

        int indexOfSpace = stringBuilder.indexOf(" ");
        int indexOfQuestion = stringBuilder.indexOf("?");

        request.setMethod(stringBuilder.substring(0, indexOfSpace));
        request.setUri(stringBuilder.substring(indexOfSpace + 1, indexOfQuestion));

        stringBuilder.delete(0, indexOfQuestion + 1);
        stringBuilder.delete(stringBuilder.lastIndexOf(" "), stringBuilder.length());

        parseQueryParams(request);
    }

    private void parseQueryParams(HttpRequest request) {
        while (!stringBuilder.isEmpty()) {
            int indexOfAmpersand = stringBuilder.indexOf("&");
            int indexOfEquals = stringBuilder.indexOf("=");

            request.getQueryParams().put(stringBuilder.substring(0, indexOfEquals),
                    stringBuilder.substring(indexOfEquals + 1, indexOfAmpersand != -1 ? indexOfAmpersand : stringBuilder.length()));

            stringBuilder.delete(0, indexOfAmpersand != -1 ? indexOfAmpersand + 1 : stringBuilder.length());
        }
    }

    private void parseHeaders(BufferedReader reader, HttpRequest request, int maxRequestSize) throws IOException {
        while (!(stringBuilder.append(reader.readLine()).isEmpty())) {
            totalBytesRead += stringBuilder.length();
            if (totalBytesRead > maxRequestSize) {
                throw new RequestSizeExceededException();
            }

            int indexOfColon = stringBuilder.indexOf(":");
            request.getHeaders().put(stringBuilder.substring(0, indexOfColon), stringBuilder.substring(indexOfColon + 2));

            stringBuilder.setLength(0);
        }
    }

    private void parseBody(BufferedReader reader, HttpRequest request, int maxRequestSize) throws IOException {
        if (request.getHeaders().get(CONTENT_LENGTH) != null) {
            int contentLength = Integer.parseInt(request.getHeaders().get(CONTENT_LENGTH));
            if (contentLength > 0) {
                if (totalBytesRead + contentLength > maxRequestSize) {
                    throw new RequestSizeExceededException();
                }

                while (reader.ready()) {
                    stringBuilder.append((char) reader.read());
                }

                if (stringBuilder.length() != contentLength) {
                    throw new IOException("Failed to read full request body");
                }

                request.setBody(stringBuilder.toString());
            }
        }
    }

}