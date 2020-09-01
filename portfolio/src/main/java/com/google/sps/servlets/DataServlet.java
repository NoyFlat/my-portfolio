// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.lang.Integer;
import com.google.sps.data.Comment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    int commentQuantity = Integer.parseInt(request.getParameter("comment-num"));
    // Iterate over all comments in datastore and add them to array
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(commentQuantity))) {
      long id = entity.getKey().getId();
      String email = (String) entity.getProperty("email");
      String opinion = (String) entity.getProperty("opinion");
      String content = (String) entity.getProperty("content");
      long timestamp = (long) entity.getProperty("timestamp");
      Comment comment = new Comment.Builder(id)
                            .withEmail(email)
                            .hasOpinion(opinion)
                            .commentContent(content)
                            .atTime(timestamp)
                            .build();
      comments.add(comment);
    }
    // Display comments
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long timestamp = System.currentTimeMillis();
    // Get user email
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    // Get the input from the form.
    String opinion = getParameter(request, "opinion");
    String content = getParameter(request, "content");
    // Add comment to datastore
    Entity commentEntity = new Entity("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("opinion", opinion);
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("timestamp", timestamp);
    datastore.put(commentEntity);   
    // Direct the user to main page
    response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String parameter){
    String value = request.getParameter(parameter);
    if(value == null){
        return "";
    }
    return value;
  }

}
