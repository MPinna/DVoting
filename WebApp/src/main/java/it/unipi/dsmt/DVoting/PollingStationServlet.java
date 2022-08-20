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

@WebServlet(name = "PollingStationServlet", value = "/PollingStationServlet")
public class PollingStationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DoGet PollingStation");
        PrintWriter writer = response.getWriter();
        String message = "<html><body><h1> JAVA </h1></body></html>";
        Network n;
        try {
            n=new Network(request.getSession().getId());
        }catch (IOException e){
            return;
        }


        if(n.test())
            message = "<h1> OK </h1>";
        else
            message = "<h1> NOPE </h1>";
        writer.write(message);
        PublicKey pk;
        try {
            String filePath=this.getClass().
                    getResource("/cs_keys/cs_cert.pem").getPath();
            pk= Crypto.getPublicKeyFromCertificate(filePath);
        } catch (FileNotFoundException e) {
            writer.write("<h1> wrong key </h1>");
            return;
        }
        writer.write("<h1> ok key </h1>");
        byte[] cph=Crypto.encrypt(pk, "provaeoeoeoe".getBytes());
        n.sendBytes(cph);
        //n.send("boooooooooooo");
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DoPost PollingStation");
        String vote = request.getParameter("vote");
        PrintWriter writer = response.getWriter();
        writer.println(vote);
    }
}
