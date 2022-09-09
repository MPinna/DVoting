package it.unipi.dsmt.DVoting;

import it.unipi.dsmt.DVoting.crypto.Crypto;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * servlet for voting expression and forwarding
 */
@WebServlet(name = "BoothServlet", value = "/Booth")
public class BoothServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/index.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("DoPost PollingStation");

        try {
            PrintWriter writer = response.getWriter();
            String message = "<html><body><h1> JAVA </h1></body></html>";
            writer.write(message);
            WebAppNetwork n;
            n=new WebAppNetwork(request.getSession().getId());
            String vote = request.getParameter("vote");
            writer.println(vote);
            PublicKey pk;
            pk= Crypto.getCsPublicKeyFromCertificate();
            writer.write("<h1> ok key </h1>");
            byte[] cph=Crypto.encrypt(pk, vote.getBytes()); // encrypt with cs public key
            // add voter ID and voter key signature for authentication
            PrivateKey pv = (PrivateKey) request.getSession().getAttribute("VoterKey");
            String voterID = (String) request.getSession().getAttribute("VoterID");
            byte[] sign=Crypto.sign(pv,cph);
            n.sendSigned(cph,voterID,sign, "vote");
            writer.println("vote sent");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.getSession(false).invalidate();

    }
}
