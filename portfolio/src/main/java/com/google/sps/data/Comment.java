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

package com.google.sps.data;

/** Class containing comment in a website */
public final class Comment {
  
  public static class Builder {

    private String email;
    private String likedGame;
    private String content;
    private long timestamp;
    private final long id;

    public Builder(long id){
      this.id = id;
    }

    public Builder withEmail(String email){
      this.email = email;
      return this;
    }

    public Builder hasOpinion(String likedGame){
      this.likedGame = likedGame;
      return this;
    }

    public Builder commentContent(String content){
      this.content = content;
      return this;
    }

    public Builder atTime(long timestamp){
      this.timestamp = timestamp;
      return this;
    }

    public Comment build(){
      return new Comment(this);
    }
  }

  private String email;
  private String likedGame;
  private String content;
  private long timestamp;
  private final long id; 

  private Comment(Builder builder){
      this.id = builder.id;
      this.email = builder.email;
      this.likedGame = builder.likedGame;
      this.content = builder.content;
      this.timestamp = builder.timestamp;
  }

  public long getId(){
      return this.id;
  }

  public String getEmail(){
      return this.email;
  }

  public String getLikedGame(){
      return this.likedGame;
  }

  public String getContent(){
      return this.content;
  }

  public long getTimestamp(){
      return this.timestamp;
  }
}
