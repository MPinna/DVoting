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
        String targetJSP = "index.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("DoPost PollingStation");
        PrintWriter writer = response.getWriter();
        try {

            String message = "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Login</title>\n" +
                    "    <link href=\"css/index.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                    "</head>";
            writer.write(message);
            WebAppNetwork n;
            n=new WebAppNetwork(request.getSession().getId());
            String vote = request.getParameter("vote");
            PublicKey pk;
            pk= Crypto.getCsPublicKeyFromCertificate();
            byte[] cph=Crypto.encrypt(pk, vote.getBytes()); // encrypt with cs public key
            writer.write("<h1> your vote has been correctly encrypted </h1>");
            // add voter ID and voter key signature for authentication
            PrivateKey pv = (PrivateKey) request.getSession().getAttribute("VoterKey");
            String voterID = (String) request.getSession().getAttribute("VoterID");
            byte[] sign=Crypto.sign(pv,cph);
            n.sendSigned(cph,voterID,sign, "vote");
            writer.println("<h1> your vote has been correctly sent </h1>");
            writer.println("<a href="+request.getContextPath()+"> click here to exit </a>");
            writer.close();
        } catch (Exception e) {
            writer.println("something went wrong, contact administrator");
            e.printStackTrace();
        }
        writer.println("<a href="+request.getContextPath()+"> exit </a>");
        request.getSession(false).invalidate();

    }
}
