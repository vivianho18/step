package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List; 
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<HashMap<String, String>> allComments = new ArrayList<HashMap<String, String>>();

    for (Entity entity : results.asIterable()) {
      HashMap<String, String> comment = new HashMap<String, String>();
      String name = (String) entity.getProperty("name");
      String content = (String) entity.getProperty("content");
      comment.put("name", name);
      comment.put("content", content);
      allComments.add(comment);
    }

    String jsonComment = convertToJson(allComments);
    response.setContentType("application/json");
    response.getWriter().println(jsonComment);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get user input from the form element in HTML page 
    String name = request.getParameter("name"); 
    String content = request.getParameter("content");

    // Create an entity instance and store in Datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("content", content);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    response.sendRedirect("/index.html");
  }

  /**
   * Converts a HashMap List instance into a JSON string using the Gson library.
   * @param list the HashMap List instance to be converted to JSON string format 
   * @return a JSON String containing the contents from HashMap List param
   */
  private String convertToJson(List<HashMap<String, String>> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
