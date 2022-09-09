package it.unipi.dsmt.DVoting;


import it.unipi.dsmt.DVoting.crypto.Crypto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * servlet for admin access, authentication and actions
 */
@WebServlet(name = "AdminServlet", value = "/Admin")
@MultipartConfig
public class AdminServlet extends HttpServlet {

    public static final String ActionSUSPEND ="suspend_vote";
    public static final String ActionSTOP ="stop_vote";
    public static final String ActionRESUME="resume_vote";

    public static final String ActionEXIT="exit";

    public static final String ActionSEARCH="search";

    public static final String ActionLIST="get_list";



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(!authenticateAdmin(request.getSession())){
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath());
            return;
        }
        try {
            String action=request.getParameter("action");
            switch (action){
                case ActionEXIT: {
                    request.getSession().invalidate();
                    response.sendRedirect(request.getContextPath());
                    return;
                }
                case ActionSEARCH: {
                    WebAppNetwork net = new WebAppNetwork(request.getSession().getId());
                    String VoterID = request.getParameter("searchVoterID");
                    net.sendCommandToPollingStation(action, VoterID);
                    Voter v = net.receiveVoter();
                    request.getSession().setAttribute("searchVoter", v);
                    break;
                }
                case ActionLIST: {
                    WebAppNetwork net = new WebAppNetwork(request.getSession().getId());
                    net.sendCommandToPollingStation(action);
                    List<Voter> v = net.receiveVoterList();
                    request.getSession().setAttribute("voterList", v);
                    break;
                }
                default: {
                    WebAppNetwork net = new WebAppNetwork(request.getSession().getId());
                    net.sendAtomToPollingStation(action);
                }
            }

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

        try {
            WebAppNetwork net= new WebAppNetwork(session.getId());
            System.out.println(net.pingCS());
            net.sendAtomToPollingStation("get_status");
            return net.receiveString();
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }

    }

    public static int getTurnout(HttpSession session) throws IOException {
        WebAppNetwork net= new WebAppNetwork(session.getId());
        net.sendAtomToPollingStation("get_turnout");
        return net.receiveInt();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

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
