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
 * Adds a random greeting to the page.
 */
function addRandomFact() {
  const greetings =
      ['I have never broken a bone', 'I don\'t like chocolate', 'I can speak Chinese', 'I like pineapple on pizza'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function getCommentsFromServer(number=2) {
  // Create query string 
  let url = "/comments?number=" + String(number);

  fetch(url).then(response => response.json()).then((serverData) => {
    document.getElementById('comment-container').innerHTML = ""
    let comments = "";
    
    if (serverData.length == 0) {
      comments += "No comments have been posted yet :( Be the first!"
    } else {
      for (let i = 0; i < serverData.length; i++) {
        let name = serverData[i].name; 
        let content = serverData[i].content; 
        comments += name + ": " + content + "<br><br>";
      }
    }
    document.getElementById('comment-container').innerHTML = comments;
  });
}

function deleteComments() {
  const request = new Request('/delete-comments', {'method': 'POST'});
  fetch(request).then(response => {
      getCommentsFromServer();  
    }
  );
}

/** Creates a map and adds it to the page. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 37.422, lng: -122.084}, zoom: 16});
}
