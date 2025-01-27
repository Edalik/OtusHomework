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

    public HttpRequest parseRequest(InputStream stream, int maxRequestSize) throws IOException, RequestSizeExceededException {
        HttpRequest request = new HttpRequest();
        totalBytesRead = 0;
        stringBuilder.setLength(0);

        parseStartLine(stream, request, maxRequestSize);

        parseHeaders(stream, request, maxRequestSize);

        parseBody(stream, request, maxRequestSize);

        return request;
    }

    private void parseStartLine(InputStream stream, HttpRequest request, int maxRequestSize) throws IOException {
        readLine(stream, maxRequestSize);
        totalBytesRead += stringBuilder.length();
        if (stringBuilder.isEmpty()) {
            throw new IOException("Empty request");
        } else if (totalBytesRead > maxRequestSize) {
            throw new RequestSizeExceededException();
        }

        int indexOfSpace = stringBuilder.indexOf(" ");
        int indexOfQuestion = stringBuilder.indexOf("?");
        boolean areQueryParamsPresent = indexOfQuestion != -1;
        int indexOfUriEnd = areQueryParamsPresent ? indexOfQuestion : stringBuilder.lastIndexOf(" ");

        request.setMethod(stringBuilder.substring(0, indexOfSpace));
        request.setUri(stringBuilder.substring(indexOfSpace + 1, indexOfUriEnd));

        stringBuilder.delete(0, areQueryParamsPresent ? indexOfUriEnd + 1 : indexOfUriEnd);
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

    private void parseHeaders(InputStream stream, HttpRequest request, int maxRequestSize) throws IOException {
        readLine(stream, maxRequestSize);
        while (!stringBuilder.isEmpty()) {
            totalBytesRead += stringBuilder.length();
            if (totalBytesRead > maxRequestSize) {
                throw new RequestSizeExceededException();
            }

            int indexOfColon = stringBuilder.indexOf(":");
            request.getHeaders().put(stringBuilder.substring(0, indexOfColon), stringBuilder.substring(indexOfColon + 2));

            stringBuilder.setLength(0);
            readLine(stream, maxRequestSize);
        }
    }

    private void parseBody(InputStream stream, HttpRequest request, int maxRequestSize) throws IOException {
        if (request.getHeaders().get(CONTENT_LENGTH) != null) {
            int contentLength = Integer.parseInt(request.getHeaders().get(CONTENT_LENGTH));
            if (contentLength > 0) {
                if (totalBytesRead + contentLength > maxRequestSize) {
                    throw new RequestSizeExceededException();
                }

                while (stream.available() > 0) {
                    stringBuilder.append((char) stream.read());
                }

                if (stringBuilder.length() != contentLength) {
                    throw new IOException("Failed to read full request body");
                }

                request.setBody(stringBuilder.toString());
            }
        }
    }

    private void readLine(InputStream stream, int maxRequestSize) throws IOException {
        while (stream.available() > 0) {
            char c = (char) stream.read();
            if (c == '\n') {
                return;
            } else if (c != '\r') {
                stringBuilder.append(c);
            }

            if (stringBuilder.length() + totalBytesRead > maxRequestSize) {
                throw new RequestSizeExceededException();
            }
        }
    }

}