package it.unipi.dsmt.DVoting;

import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.PrivateKey;
import java.security.PublicKey;

// TODO change NAME because this run on voting machines! Polling station only run erlang!
@WebServlet(name = "PollingStationServlet", value = "/PollingStationServlet")
public class PollingStationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/index.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DoPost PollingStation");
        PrintWriter writer = response.getWriter();
        String message = "<html><body><h1> JAVA </h1></body></html>";
        writer.write(message);
        Network n;
        try {
            n=new Network(request.getSession().getId());
            String vote = request.getParameter("vote");
            writer.println(vote);
//        if(n.test())
//            message = "<h1> OK </h1>";
//        else
//            message = "<h1> NOPE </h1>";
            PublicKey pk;
            String filePath=this.getClass().getResource("/cs_keys/cs_cert.pem").getPath();
            pk= Crypto.getPublicKeyFromCertificate(filePath);
            writer.write("<h1> ok key </h1>");
            // TODO add voter ID and voter key signature for authentication
            byte[] cph=Crypto.encrypt(pk, vote.getBytes());
            n.sendBytes(cph);
            // TODO prevent multiple votes
            // TODO add acknowledge response from polling station
            writer.println("vote set");
            writer.close();
        } catch (FileNotFoundException  | NullPointerException e) {
            e.printStackTrace();

        }
    }
}
