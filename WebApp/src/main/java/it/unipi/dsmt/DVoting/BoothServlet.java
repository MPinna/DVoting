package it.unipi.dsmt.DVoting;

import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

@WebServlet(name = "BoothServlet", value = "/Booth")
public class BoothServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/index.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DoPost PollingStation");

        try {
            PrintWriter writer = response.getWriter();
            String message = "<html><body><h1> JAVA </h1></body></html>";
            writer.write(message);
            Network n;
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
            byte[] cph=Crypto.encrypt(pk, vote.getBytes());
            // add voter ID and voter key signature for authentication
            PrivateKey pv = (PrivateKey) request.getSession().getAttribute("VoterKey");
            String voterID = (String) request.getSession().getAttribute("VoterID");
            byte[] sign=Crypto.sign(pv,cph);
            n.sendSigned(cph,voterID,sign);
            writer.println("vote sent");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.getSession(false).invalidate();
        //response.sendRedirect(request.getContextPath());

    }
}
