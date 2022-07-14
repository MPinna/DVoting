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

        PrintWriter writer = response.getWriter();
        String message = "<html><body><h1> JAVA </h1></body></html>";
        Network n=new Network(request.getSession().getId());
        if(n.test())
            message = "<html><body><h1> OK </h1></body></html>";
        else
            message = "<html><body><h1> NOPE </h1></body></html>";
        writer.write(message);
        n.send("provaoeoeoeoeo");
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
