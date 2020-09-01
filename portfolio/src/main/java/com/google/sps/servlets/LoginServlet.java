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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    String urlToRedirectTo = "/";
    UserService userService = UserServiceFactory.getUserService();
    NicknameData nicknameData;
    if (userService.isUserLoggedIn()) {
      String nickname = getUserNickname(userService.getCurrentUser().getUserId());
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL(urlToRedirectTo);
      nicknameData = new NicknameData("yes", logoutUrl, userEmail, nickname);
    } else {
      String loginUrl = userService.createLoginURL(urlToRedirectTo);
      nicknameData = new NicknameData("no", loginUrl, "", "");
    }
    // Create JSON according to the user data
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(nicknameData));
  }

    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String nickname = request.getParameter("nickname");
      String id = userService.getCurrentUser().getUserId();
 
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity entity = new Entity("UserInfo", id);
      entity.setProperty("nickname", nickname);
      // The put() function automatically inserts new data or updates existing data based on ID
      datastore.put(entity);
      // Direct the user to main page
      response.sendRedirect("/index.html");
    }
    
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  public static String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("__key__", Query.FilterOperator.EQUAL, KeyFactory.createKey("UserInfo", id)));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
  
  /** Class containing the login status and nickname of a user*/
  private class NicknameData {
      
    private String loginStatus;
    private String url;
    private String email;
    private String nickname; 

    private NicknameData(String loginStatus, String url, String email, String nickname){
        this.loginStatus = loginStatus;
        this.url = url;
        this.email = email;
        this.nickname = nickname;
    }

    private String getLoginStatus(){
        return this.loginStatus;
    }

    private String getNickname(){
        return this.nickname;
    }

    private String getUrl(){
        return this.url;
    }

    private String getEmail(){
        return this.email;
    }
  }
    
}
