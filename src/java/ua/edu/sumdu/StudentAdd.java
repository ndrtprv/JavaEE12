/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package ua.edu.sumdu;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Andrii
 */
@WebServlet(name = "Default", urlPatterns = "/", loadOnStartup = 1)
public class StudentAdd extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, IllegalArgumentException {
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(pw);
            pw.print(e.getMessage());
        }
        
        Connection conn = null;
        try {
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3311/university", "root", "mpg123");
            
            if (!"".equals(request.getParameter("firstName")) && 
                !"".equals(request.getParameter("lastName")) && 
                isValidAge(request.getParameter("age")) && 
                !"".equals(request.getParameter("group")) && 
                !"".equals(request.getParameter("faculty"))) {
                
                PreparedStatement ps = (PreparedStatement) conn.prepareStatement("INSERT INTO Student(firstName, lastName, age, email, group_, faculty)VALUES(?, ?, ?, ?, ?, ?)");
                ps.setString(1,request.getParameter("firstName"));
                ps.setString(2,request.getParameter("lastName"));
                ps.setInt(3,Integer.parseInt(request.getParameter("age")));
                ps.setString(4,request.getParameter("email"));
                ps.setString(5,request.getParameter("group"));
                ps.setString(6,request.getParameter("faculty"));
                // update db so new data could be saved  
                ps.executeUpdate();
                // reload the web page 
                response.sendRedirect("./");
            }
            
            Statement s = (Statement) conn.createStatement();
            ResultSet resultSet = s.executeQuery("Select * From Student");

            List<Student> students = new LinkedList<>();
            // get data from db and add it to the list 'students'
            while(resultSet.next()){
                Student student = new Student(
                        resultSet.getString(2),resultSet.getString(3),
                        resultSet.getInt(4),resultSet.getString(5),
                        resultSet.getString(6),resultSet.getString(7)
                );
                students.add(student);
            }
            // set list as an attribute 'students' on jsp 
            request.setAttribute("students",students);
            request.getRequestDispatcher("view.jsp").forward(request,response);
        } catch (SQLException e) {
            pw.print(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            // close connection
            if (conn != null){
                try{
                    conn.close();
                } catch (SQLException ex) {
                    ex.getMessage();
                }
            }
        }
    }
    
    private boolean isValidAge(String age) {
        if (age == null) {
            return false;
        }
        try {
            int a = Integer.parseInt(age);
            
            return a >= 15 && a <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
