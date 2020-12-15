package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List; 
import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private List<String> comments;

  public void init() {
    comments = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String jsonComment = convertToJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(jsonComment);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Add author name then comment content to comments array list 
    String name = request.getParameter("name"); 
    String content = request.getParameter("content"); 
    comments.add(name);
    comments.add(content);

    response.sendRedirect("/index.html");
  }

  /**
   * Converts a List instance into a JSON string using the Gson library.
   * @param list the List instance to be converted to JSON string format 
   * @return a JSON String containing the contents from List param
   */
  private String convertToJson(List<String> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
