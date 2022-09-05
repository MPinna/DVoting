package it.unipi.dsmt.DVoting;


import it.unipi.dsmt.DVoting.crypto.Crypto;
import it.unipi.dsmt.DVoting.network.Network;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@WebServlet(name = "AdminServlet", value = "/Admin")
@MultipartConfig
public class AdminServlet extends HttpServlet {

    public static String ActionSUSPEND ="suspend_vote";
    public static String ActionSTOP ="stop_vote";
    public static String ActionRESUME="resume_vote";

    public static String ActionEXIT="exit";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!authenticateAdmin(request.getSession())){
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath());
            return;
        }
        try {
            if(request.getParameter("action").equals(ActionEXIT)){
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath());
                return;
            }

            Network net= new Network(request.getSession().getId());
            net.sendAtomToPollingStation(request.getParameter("action"));

        }catch (Exception e){
            e.printStackTrace();

        }
        response.sendRedirect(request.getContextPath()+"/pages/dashboard.jsp");
    }
    public static boolean authenticateAdmin(HttpSession session) {
        try {
            session.getAttribute("Admin");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getVotingStatus(HttpSession session)  {
        Network net= null;
        try {
            net = new Network(session.getId());
            System.out.println(net.pingCS());
            net.sendAtomToPollingStation("get_status");
            return net.receiveString();
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }

    }

    public static int getTurnout(HttpSession session) throws IOException {
        Network net= new Network(session.getId());
        net.sendAtomToPollingStation("get_turnout");
        return net.receiveInt();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getSession().invalidate();
        try {
            String adminName=request.getParameter("name");
            String adminPassword=request.getParameter("password");
            adminPassword =Crypto.digest(adminPassword);
            InputStream is = AdminServlet.class.getResourceAsStream("/admin.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject obj = (JSONObject) jsonParser.parse(new InputStreamReader(is));

            if (obj.get("name").equals(adminName) && obj.get("password").equals(adminPassword)){
                throw new IllegalAccessException("admin name or password are incorrect");
            }
            request.getSession().setAttribute("Admin", adminName);
        }catch (Exception e){
            e.printStackTrace();
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath());
            return;
        }
        response.sendRedirect(request.getContextPath()+"/pages/dashboard.jsp");
    }
}
