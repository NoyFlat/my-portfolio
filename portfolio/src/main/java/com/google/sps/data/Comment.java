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

  private final String name;
  private final String likedGame;
  private final String content;
  private final long timestamp;
  private final long id;

  public Comment(long id, String name, String likedGame, String content, long timestamp){
      this.id = id;
      this.name = name;
      this.likedGame = likedGame;
      this.content = content;
      this.timestamp = timestamp;
  }
  public long getId(){
      return this.id;
  }

  public String getName(){
      return this.name;
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
