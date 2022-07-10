package it.unipi.dsmt.DVoting;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "PollingStationServlet", value = "/PollingStationServlet")
public class PollingStationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = "<html><body><h1> JAVA </h1></body></html>";
        PrintWriter writer = response.getWriter();
        writer.write(message);
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
