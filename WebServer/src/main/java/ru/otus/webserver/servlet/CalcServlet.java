package ru.otus.webserver.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@WebServlet(urlPatterns = {"/add", "/subtract", "/multiply", "/div"})
public class CalcServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Double result = getResult(req);
            sendResponse(resp, result.toString());
            log.info("Successfully returned answer: {}", result);
        } catch (NumberFormatException | IllegalStateException e) {
            sendResponse(resp, "Internal Server Error");
            log.error("Error parsing request", e);
        } catch (IOException e) {
            log.error("Error sending response", e);
        }
    }

    private static Double getResult(HttpServletRequest req) {
        Double numberOne = Double.parseDouble(req.getParameter("number-one"));
        Double numberTwo = Double.parseDouble(req.getParameter("number-two"));

        String requestURI = req.getRequestURI();
        String endpoint = requestURI.substring(requestURI.lastIndexOf("/"));
        return switch (endpoint) {
            case "/add" -> numberOne + numberTwo;
            case "/subtract" -> numberOne - numberTwo;
            case "/multiply" -> numberOne * numberTwo;
            case "/div" -> numberOne / numberTwo;
            default -> throw new IllegalStateException("Unexpected endpoint: " + endpoint);
        };
    }

    private static void sendResponse(HttpServletResponse resp, String result) throws IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.printf(result);
        out.close();
    }

}