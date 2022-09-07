<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page import="it.unipi.dsmt.DVoting.AdminServlet" %>
<%@ page import="it.unipi.dsmt.DVoting.Voter" %>
<%@ page import="java.util.List" %>

<%--
  Created by IntelliJ IDEA.
  User: yuri
  Date: 30/08/22
  Time: 11:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
<head>
    <title>Title</title>
</head>
<body> <%      // prevent access from unregistered users
    if(!AdminServlet.authenticateAdmin(request.getSession())) {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath());
    }
    String votingStatus=AdminServlet.getVotingStatus(request.getSession());
    int turnout= AdminServlet.getTurnout(request.getSession());
   %>

<form action = "<%=request.getContextPath()%>/Admin" method = "get" >
    <fieldset>
        <legend>Status: <%= votingStatus%> Turnout: <%= turnout%></legend>
        <input type = "hidden" name = "action" id="action"/>
    </fieldset>
    <input type = "submit" value = "Suspend" onclick="document.getElementById('action').value='<%=AdminServlet.ActionSUSPEND%>'" />
    <input type = "submit" value = "Resume" onclick="document.getElementById('action').value='<%=AdminServlet.ActionRESUME%>'" />
    <input type = "submit" value = "Stop" onclick="document.getElementById('action').value='<%=AdminServlet.ActionSTOP%>'" />
    <input type = "submit" value = "Exit" onclick="document.getElementById('action').value='<%=AdminServlet.ActionEXIT%>'" />
<%--    <fieldset> <legend>Search voter</legend>--%>
<%--        <input type="text" value="" id="searchVoterID" name="searchVoterID">--%>
<%--        <input type = "submit" value = "Search" onclick="document.getElementById('action').value='<%=AdminServlet.ActionSEARCH%>'" />--%>
<%--        <% try{--%>
<%--            Voter v= (Voter) request.getSession().getAttribute("searchVoter");%>--%>
<%--            name:<%=v.getName()%><br>--%>
<%--            surname:<%=v.getSurname()%><br>--%>
<%--            date fo birth:<%=v.getDob().toString()%><br>--%>
<%--            has voted:<%=v.getHasVoted().toString()%>--%>
<%--        <% } catch(Exception ignored){}%>--%>
    <fieldset>
        <legend> <input type = "submit" value = "Update List" onclick="document.getElementById('action').value='<%=AdminServlet.ActionLIST%>'" /></legend>
        <table >
            <thead>
            <tr>
                <th>id</th> <th>name</th><th>surname</th><th>Date of birth</th><th>has voted</th>
            </tr>
            </thead>
        <% try{
            List<Voter> l= (List<Voter>) request.getSession().getAttribute("voterList");
            for (Voter voter : l) {
                %> <tr>
            <td><%=voter.getId() %></td>
            <td><%=voter.getName() %></td>
            <td><%=voter.getSurname() %></td>
            <td><%=voter.getDob() %></td>
            <td><%=voter.getHasVoted() %></td>
                </tr><%
            }
        } catch(Exception e){
            e.printStackTrace();
        }%>
        </table >
    </fieldset>
</form>

</body>
</html>
