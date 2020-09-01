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

const nicknames =
      ['Efrat', 'Patchpootcheet', 'Patchpoocheeter', 'Cheeter', 'Tamlugeet', 'Pakpukeet', 'FritFruiteet', 'Lucifer', 'Lucinda', 'Nancy Pelucy',
      'El Monstro', 'Chaplupeet', 'Pupper', 'Puppinda', 'Lucifur', 'Lucy \"The Juice\" Flat Noam'];

const justNames = 
      ['Chupi', 'Yifat', 'Brurit', 'Pachulina', 'Pepper', 'Pumpkin', 'Mina', 'Lucy \"The Juice\" Noam Flat'];

/**
 * Adds a random nickname to the page.
 */

function addRandomNickname() {

  // Pick a random nickname.
  const nickname = nicknames[Math.floor(Math.random() * nicknames.length)];

  // Add it to the page.
  const nicknameContainer = document.getElementById('nickname-container');
  nicknameContainer.innerText = nickname;
}

function startGame(){
  //Hide image
  document.getElementById('image').style = "display:none;";
 
  //Hide response
   document.getElementById('response-container').style = "display:none;";

  //Pick a random button to show the correct nickname
  const correctButtonNumber = Math.floor(Math.random() * 3) + 1;

  //Pick a random nickname
  const nickname = nicknames[Math.floor(Math.random() * nicknames.length)];

  //Pick two different random not-nicknames
  var notNicknameIndexes = [Math.floor(Math.random() * justNames.length), Math.floor(Math.random() * justNames.length)];
  while(notNicknameIndexes[0] == notNicknameIndexes[1]){
    notNicknameIndexes[1] = Math.floor(Math.random() * justNames.length);
  }
  var notNicknames = [justNames[notNicknameIndexes[0]], justNames[notNicknameIndexes[1]]];

  //Display and enable button
  for(var i = 1; i < 4; i++){
    button = document.getElementById("button"+i);
    button.style = "";
    button.disabled = false;
    if(i == correctButtonNumber){
      button.innerText = nickname;
    }
    else{
      button.innerText = notNicknames.pop();
    }
  }
}

function generateResponse(button){
  const responseContainer = document.getElementById('response-container');
  responseContainer.style = "";
  if(nicknames.includes(button.innerText)){
    responseContainer.innerText = "Success!";
    document.getElementById('image').src = "images/IMG-" + Math.floor(Math.random() * 15) + ".jpg";
    document.getElementById('image').style = "";
  }
  else{
    responseContainer.innerText = "Sorry, that's not the correct answer";
  }
 
  //Disable buttons
  for(var i = 1; i < 4; i++){
    document.getElementById("button"+i).disabled = true;
  }
}

/**
 * Fetches comments from the servers and adds them to the DOM.
 */
function getCommentsFromServer() {
  fetch('/data?comment-num=' + document.getElementById("quantity").value).then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-container');
    commentListElement.innerHTML = '';
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
    })
  });
}

/**
 * Creates a comment box with all it's fields
 */
function createCommentElement(comment){
    // Create comment Element
    const commentElement = document.createElement('div');
    commentElement.className = "comment";
    // Add a delete button
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
      deleteComment(comment);
      // Remove the comment from the DOM.
      commentElement.remove();
    });
    // Add all the field to the comment
    commentElement.appendChild(
      createListElement('Name: ' + comment.name, "P"));
    commentElement.appendChild(
      createListElement('Liked the game? ' + comment.likedGame, "P"));
    commentElement.appendChild(
      createListElement('Comment: ' + comment.content, "P"));
    commentElement.appendChild(deleteButtonElement);
    return commentElement;
}

/**
 * Fetches the login status and displays the connect\disconnet url.
 */
function displayLoginBox() {
    fetch('/login').then(response => response.json()).then((userStatus) => {
      const loginElement = document.getElementById('login-container');
      // Create link in html and put the correct url
      const aElement = document.createElement("a");
      aElement.href = userStatus.url;

      if(userStatus.isLoggedIn == true) {
          loginElement.appendChild(
              createListElement("Hello " + userStatus.email, "P"));
          aElement.innerText = "Log out";
          document.getElementById('addCommentForm').style = "";
          if(userStatus.nickname == ""){
            document.getElementById('addNicknameForm').style = "";
          }
          else{
            document.getElementById('addNicknameForm').style = "display:none;";
          }
      }
      else {
         loginElement.appendChild(
              createListElement("Hello stranger", "P"));
          aElement.innerText = "Log in";
          document.getElementById('addCommentForm').style = "display:none;";
          document.getElementById('addNicknameForm').style = "display:none;";          
          document.getElementById('leaveACommentHeading').innerText = "To leave a comment, please log in";
      }
      // Adds the link below the message to user
      loginElement.appendChild(aElement);
    });
}

/**
 * Deletes comment from the servers
 */
function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-data', {method: 'POST', body: params}).then(getCommentsFromServer());
}

/** Creates an element of type "type" containing text. */
function createListElement(text, type) {
  const element = document.createElement(type);
  element.innerText = text;
  return element;
}

/** Calls all the functions that we want when the page loads. */
function callOnloadFunctions(){
  getCommentsFromServer();
  displayLoginBox();
}
