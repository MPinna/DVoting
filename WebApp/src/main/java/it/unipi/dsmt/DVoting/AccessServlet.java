package it.unipi.dsmt.DVoting;

import it.unipi.dsmt.DVoting.crypto.Crypto;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.PrivateKey;

@WebServlet(name = "AccessServlet", value = "/Access")
@MultipartConfig
public class AccessServlet  extends HttpServlet {
    public static boolean authenticateUser(HttpSession session){
        try {
            session.getAttribute("VoterKey");
            session.getAttribute("VoterID");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        try {
            Part filePart = request.getPart("key");
            InputStream fileContent = filePart.getInputStream();
            PrivateKey pk=Crypto.getPrivateKey(fileContent);
            request.getSession().setAttribute("VoterKey", pk);
            request.getSession().setAttribute("VoterID", request.getParameter("VoterID"));

        }catch (Exception e){
            e.printStackTrace();
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath());
        }
        response.sendRedirect(request.getContextPath()+"/pages/ballot.jsp");



    }

}
