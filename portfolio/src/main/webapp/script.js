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

/**
 * Adds a random nickname to the page.
 */
function addRandomNickname() {
  const nicknames =
      ['Efrat', 'Patchpootcheet', 'Patchpoocheeter', 'Cheeter', 'Tamlugeet', 'Pakpukeet', 'FritFruiteet', 'Lucifer', 'Lucinda', 'Nancy Pelucy',
      'El Monstro', 'Chaplupeet', 'Pupper', 'Puppinda', 'Lucifur', 'Lucy \"The Juice\" Flat Noam'];
 //Then follow the on-screen instructions to set up your repo.


  // Pick a random nickname.
  const nickname = nicknames[Math.floor(Math.random() * nicknames.length)];

  // Add it to the page.
  const nicknameContainer = document.getElementById('nickname-container');
  nicknameContainer.innerText = nickname;
}
