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
    for(var i = 0; i < comments.length; i++){
      commentListElement.appendChild(
        createListElement('Name: ' + comments[i].name));
      commentListElement.appendChild(
        createListElement('Liked the game? ' + comments[i].likedGame));
      commentListElement.appendChild(
        createListElement('Comment: ' + comments[i].content));
    }
  });
}

/** Creates an <p> element containing text. */
function createListElement(text) {
  const pElement = document.createElement('P');
  pElement.innerText = text;
  return pElement;
}

