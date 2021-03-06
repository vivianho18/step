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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String queryString = request.getQueryString();
    String[] split = queryString.split("="); 
    int limit = Integer.parseInt(split[1]); 
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit); 

    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<HashMap<String, String>> allComments = new ArrayList<HashMap<String, String>>();

    for (Entity entity : results.asIterable(fetchOptions)) {
      HashMap<String, String> comment = new HashMap<String, String>();
      String content = (String) entity.getProperty("content");
      String emailAddress = (String) entity.getProperty("email");
      comment.put("content", content);
      comment.put("email", emailAddress); 
      allComments.add(comment);
    }

    String jsonComment = convertToJson(allComments);
    response.setContentType("application/json");
    response.getWriter().println(jsonComment);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get user input from the form element in HTML page 
    String content = request.getParameter("content");

    // Get the user's email 
    UserService userService = UserServiceFactory.getUserService();
    String emailAddress = userService.getCurrentUser().getEmail();

    // Create an entity instance and store in Datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("email", emailAddress);
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
