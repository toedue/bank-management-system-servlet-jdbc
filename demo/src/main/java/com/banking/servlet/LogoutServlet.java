package com.banking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// This servlet handles when someone wants to logout
public class LogoutServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get the session and delete it
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Send user back to login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
