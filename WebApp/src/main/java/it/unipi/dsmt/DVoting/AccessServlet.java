package it.unipi.dsmt.DVoting;

import it.unipi.dsmt.DVoting.crypto.Crypto;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

/**
 * servlet for voter access and authentication
 */
@WebServlet(name = "AccessServlet", value = "/Access")
@MultipartConfig
public class AccessServlet  extends HttpServlet {
    /**
     * verify if the voter is authenticated
     * @param session HTTP session
     * @return boolean
     */
    public static boolean authenticateUser(HttpSession session){
        try {
            if(session.getAttribute("VoterKey")==null || session.getAttribute("VoterID")==null)
                return false;
//            System.out.println("ACC:"+session.getAttribute("VoterKey")+" "+session.getAttribute("VoterID"));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            return;
        }
        response.sendRedirect(request.getContextPath()+"/pages/ballot.jsp");



    }

}
