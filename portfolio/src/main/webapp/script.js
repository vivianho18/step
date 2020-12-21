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
      {center: {lat: -33.888385, lng: 151.244747}, zoom: 12, 
      mapId: 'c41864c7bb66540a'});

  const uniInfoWindow = 
    '<h3>University of Sydney</h3>' +
    '<p>This is where I go to university</p>'

  const highSchoolInfoWindow =
    '<h3>Kincoppal Rose Bay</h3>' +
    '<p>This is where I went to high school</p>'

  const japaneseInfoWindow = 
    '<h3>Sushi Den Vaucluse</h3>' +
    '<p>This is my favourite local Japanese restaurant'

  addLandmark(
      map, -33.888813, 151.188131, 'University of Sydney', uniInfoWindow, 'A'
  )

  addLandmark(
      map, -33.863366, 151.271159, 'Kincoppal Rose Bay', highSchoolInfoWindow, 'B'
  )

  addLandmark(
      map, -33.859159, 151.278378, 'Sushi Den Vaucluse', japaneseInfoWindow, 'C'
  )
}

/** Adds a marker (with drop animation) that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description, label) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title, label: label, animation: google.maps.Animation.DROP});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}
