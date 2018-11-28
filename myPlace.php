<?php
    $test = "yes";
    //echo $test;
    $keyword = "";
    $category = "";
    $distance = "";
    $here = "checked";
    $location = "unchecked";
    $inputLocation = "";
    if(isset($_POST["submit"])) {
        //echo $test;
        
        $keyword = $_POST["keyword"];
        //echo $keyword;
        $category = $_POST["category"];
        //echo $category;
//        $distance = $_POST["distance"];
        //echo $distance;
        $radio = $_POST["location"];
        //echo $radio;
        $inputLocation = $_POST["inputLocation"];
        //echo $inputLocation;
        
        if($radio == "Here") {
            //echo $test;
//            $currentLocationJSON = file_get_contents("http://ip-api.com/json");
//            //echo $currentLocationJSON;
//            $currentLocationJSONObj = json_decode($currentLocationJSON);
//            //echo $currentLocationJSONObj;
//            $longitude = $currentLocationJSONObj->lon;
//            //echo $longitude;
//            $latitude = $currentLocationJSONObj->lat;
            $longitude = $_POST["longitude"];
            $latitude = $_POST["latitude"];          
        } else {
            //echo $test;
            $address = urlencode($inputLocation);
            //echo $address;
            $addressUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=$address&key=AIzaSyC6A-agVKEwgIed6VeKEYHRIblKM6sJRjg";
            $addressUrlContent = file_get_contents($addressUrl);
            //echo $addressUrlContent;
            $addressJSONobj = json_decode($addressUrlContent,true);
            $longitude = $addressJSONobj[results][0][geometry][location][lng];
            //echo $longitude;
            $latitude = $addressJSONobj[results][0][geometry][location][lat];    
            //echo $latitude;
        }  
//        $latitude = "34.0266"; 
//        $longitude = "-118.2831";  
        $endistance = urlencode($distance);
        //echo $endistance;
        $encategory = urlencode($category);
        //echo $encategory;
        $enkeyword = urlencode($keyword);
        
        if($_POST["distance"] == "") {
            $radius = 10 * 1609.344;
        } else {
            $radius = $_POST["distance"] * 1609.344;
        }      
        //echo $radius;
        
        $url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=".$latitude.",".$longitude."&radius=".$radius."&type=".$category."&keyword=".$enkeyword."&key=AIzaSyAYOMHG-XIhq4McVRbWUpt8BBIqhzOvvf4";
        //$url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&type=$encategory&keyword=$enkeyword&key=AIzaSyAYOMHG-XIhq4McVRbWUpt8BBIqhzOvvf4";
        //$url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=34.0266,-118.2831&radius=16093.44&type=default&keyword=usc&key=AIzaSyAYOMHG-XIhq4McVRbWUpt8BBIqhzOvvf4";
        $urlContent = file_get_contents($url);
        echo $urlContent;
        return;     
    }
    if(isset($_POST["name"])) {
        $place_id = $_POST["name"];
        $key = 'AIzaSyCHb97batSX0rdeB5vcim3kkhqRnoJYlwU';
        $url = 'https://maps.googleapis.com/maps/api/place/details/json?placeid='.$place_id.'&key='.$key;
        $jsonObj = file_get_contents($url);
        $jsonRes = json_decode($jsonObj, true);
        $jsonStr = array();
        
        $author_name = array();
        $profile_photo_url = array();
        $text = array();
        $reviews = array();
        $photos = array();
        
        if(array_key_exists("reviews", $jsonRes[result])) {
            for($i = 0; $i < count($jsonRes[result][reviews]); $i++) {
                array_push($author_name, $jsonRes[result][reviews][$i][author_name]);
                array_push($profile_photo_url, $jsonRes[result][reviews][$i][profile_photo_url]);
                array_push($text, $jsonRes[result][reviews][$i][text]);
                array_push($reviews, $jsonRes[result][reviews][$i]);
            }
        }
        
        if(array_key_exists("photos", $jsonRes[result])) {
            for($i = 0; $i < count($jsonRes[result][photos]); $i++) {
                $url_photos = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth='.$jsonRes[result][photos][$i][width].'&photoreference='.$jsonRes[result][photos][$i][photo_reference].'&key='.$key;
                $json_photo = file_get_contents($url_photos);
                file_put_contents('photo'.$i.'.png', $json_photo);
                array_push($photos, "photo".$i.".png");
            }
        }
        
        $jsonStr["author_name"] = $author_name;
        $jsonStr["profile_photo_url"] = $profile_photo_url;
        $jsonStr["text"] = $text;
        $jsonStr["photos"] = $photos;
        $jsonStr["reviews"] = $reviews;
        
        $new_json = json_encode($jsonStr);
        
        echo $new_json;
        return;
    }

    ?>
    <html>

    <head>
        <meta charset="utf-8">
        <title>Travel and Entertainment Search</title>
        <style>
            h1 {
                text-align: center;
                margin: 0;
            }

            hr {
                text-align: center;
                margin: 10px;
            }

            b {
                margin: 10px;
            }

            #formBox {
                width: 600px;
                margin: 0 auto;
                text-align: left;
                border: 1px solid;
                background-color: #CCC;
            }

            input#keyword,
            input#distance,
            input#inputLocation {
                width: 140px;
            }

            select {
                width: 120px;
            }

            #location {
                margin-left: 333px;
            }

            #buttons {
                margin-left: 70px;
            }

            #showPlaces {
                margin-top: 20px;
                text-align: center;
                align-content: center;
                align-self: center;
            }

            #places {
                text-align: left;
                align-self: center;
                border-collapse: collapse;
                width: 90%;
                margin: auto;
            }

            #placeTableHeader {
                text-align: center;
            }

            table,
            tr,
            td {
                border-style: solid;
                border-width: thin;
                border-color: lightgray;
            }

            #headers {
                font-weight: bold;
                text-align: center;
            }

            

            #place_name {
                font-size: 10px;
            }

            #place_name,
            #show_reviews,
            #show_photos,
            #icon_reviews,
            #icon_photos,
            #draw_reviews,
            #draw_photos {
                text-align: center;
            }

            #showMap {
                height: 300px;
                width: 400px;
                position: absolute;
            }

            #travelMode {
                position: absolute;
                background-color: lightgray;
            }

            #author_name {
                text-align: center;
            }

            #photo {
                width: 30px;
                height: 30px;
            }

            #review_table,
            #photo_table {
                width: 600px;
                border-collapse: collapse;
                margin: auto;
            }

            #review_empty,
            #photo_empty {
                text-align: center;
                font-weight: bold;
            }

            #photo_table {
                text-align: center;
            }

            #photos {
                width: 550px;
                margin-bottom: 20px;
                margin-top: 20px;
            }
            
            #noRecord {
                text-align: center;
                background-color: lightgray;
                border-color: black;
                width: 600px;
            }

            #walk:hover {
                background-color: #CCC;
            }

            #bike:hover {
                background-color: #CCC;
            }

            #drive:hover {
                background-color: #CCC;
            }

        </style>


    </head>

    <body>

        <div id="formBox">
            <h1><i>Travel and Entertainment Search</i></h1>
            <hr>
            <form id="myForm" action="<?php echo $_SERVER['PHP_SELF']; ?>" onsubmit="return makeRequest()">
                <b>Keyword</b><input type="text" name="keyword" id="keyword" required><br> <b>Category</b>
                <select name="category" id="category">
                <option value="default">default</option>
                <option value="cafe">cafe</option>
                <option value="bakery">bakery</option>
                <option value="restaurant">restaurant</option>
                <option value="beauty salon">beauty salon</option>
                <option value="casino">casino</option>
                <option value="movie theater">movie theater</option>
                <option value="lodging">lodging</option>
                <option value="airport">airport</option>
                <option value="train station">train station</option>
                <option value="subway station">subway station</option>
                <option value="bus station">bus station</option>
                </select><br>

                <b>Distance(miles)</b>
                <input type="text" name="distance" id="distance" placeholder="10"> <b>from</b>

                <input type="radio" id="here" name="location" value="Here" checked onclick="setInputLocationDisable()"><b>Here</b><br>
                <input type="radio" id="location" name="location" value="location" onclick="setInputLocationRequire()">
                <input type="text" id="inputLocation" name="inputLocation" placeholder="location" disabled><br><br>

                <div id="buttons">
                    <button id="Search" type="submit" name="submit">Search</button>
                    <button id="clear" onclick="clearPage()">Clear</button>
                </div>
            </form>
        </div>
        <div id="showPlaces"></div>
        <div id="showDetails">
            <div id="place_name"></div>

            <div id="show_reviews"></div>
            <div id="icon_reviews"></div>
            <div id="draw_reviews"></div>

            <div id="show_photos"></div>
            <div id="icon_photos"></div>
            <div id="draw_photos"></div>
        </div>
        <div id="showMap"></div>
        <div id="travelMode"></div>
        <script type="text/javascript">
            window.onload = locatePosition;
            document.getElementById('showMap').style.display = "none";

            function locatePosition() {
                var url = "http://ip-api.com/json";
                var jsonObj = "";
                var xmlhttp;
                if (window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                        var jsonObj = xmlhttp.responseText;
                        var json = "";
                        var json = JSON.parse(jsonObj)
                        latitude = json.lat;
                        longitude = json.lon;
                    }
                };

                xmlhttp.open("GET", url, false);
                xmlhttp.send();
                //                return false;
            }

        </script>

        <script type="text/javascript">
            function makeRequest() {
                if (!checkInputValidation()) {
                    return;
                }
                var xmlhttp;
                if (window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                        showTable(xmlhttp.responseText);
                    }
                };
                var form = new FormData();
                form.append("submit", "submitIt");
                form.append("keyword", document.getElementById("keyword").value);
                form.append("category", document.getElementById("category").value);
                form.append("distance", document.getElementById("distance").value);
                if (document.getElementById("here").checked == true) {
                    form.append("location", document.getElementById("here").value);

                }
                if (document.getElementById("location").checked == true) {
                    form.append("location", document.getElementById("location").value);
                    form.append("inputLocation", document.getElementById("inputLocation").value);
                }
                form.append("latitude", latitude);
                form.append("longitude", longitude);
                console.log("keyword = " + form.get("keyword"));
                console.log("category = " + form.get("category"));
                console.log("distance = " + form.get("distance"));

                xmlhttp.open("POST", "<?php echo $_SERVER['PHP_SELF'];?>", false);
                xmlhttp.send(form);
                return false;
            }

            function checkInputValidation() {
                var keyword = document.getElementById("keyword");
                var distance = document.getElementById("distance").value;
                var here = document.getElementById("here");
                var location = document.getElementById("location");
                var inputLocation = document.getElementById("inputLocation");
                var reg = new RegExp("^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$");
                if (distance != null && distance != "") {
                    if (!reg.test(distance)) {
                        alert("Invalid input for distance!");
                        return false;
                    }
                }
                return true;
            }

            function setInputLocationDisable() {
                document.getElementById("inputLocation").disabled = true;
                document.getElementById("inputLocation").required = false;
            }

            function setInputLocationRequire() {
                document.getElementById("inputLocation").disabled = false;
                document.getElementById("inputLocation").required = true;
            }

            function clearPage() {
                document.getElementById("showPlaces").innerHTML = "";
                document.getElementById("showDetails").innerHTML = "";
                document.getElementById("showMap").innerHTML = "";
                document.getElementById("travelMode").innerHTML = "";
                document.getElementById("myForm").reset();
            }

            function showTable(jsonStr) {
                console.log(jsonStr);
                //console.log("showTable");
                var text = "";
                text += "<table id = " + "'places'" + ">";
                json = JSON.parse(jsonStr);
                places = json.results;
                console.log("places: " + places);
                if (places == null || places.length == 0) {
                    text += "<tr id = " + "'noRecord'" + "><td>";
                    text += "No Records have been found";
                    text += "</td></tr>";
                } else {
                    // output header
                    var headers = new Array(3);
                    headers[0] = "Category";
                    headers[1] = "Name";
                    headers[2] = "Address";

                    text += "<tr id = " + "'placeTableHeader'" + ">";
                    for (i = 0; i < 3; i++) {
                        text += "<td id = \'tableHeader\'>";
                        text += "<p id = \'headers\'>" + headers[i] + "</p>";
                        text += "</td>";
                    }

                    text += "</tr>";

                    // output places
                    for (i = 0; i < places.length; i++) {
                        text += "<tr>";

                        var place_id = places[i].place_id;

                        text += "<td>";
                        imgURL = places[i].icon;
                        x = 50;
                        y = 20;
                        text += "<img src='" + imgURL + "' width='" + x + "' height='" + y + "'>";
                        text += "</td>";

                        text += "<td name = \'name\' value = " + place_id + " onclick = \"showDetails(\'" + place_id + "\', \'" + places[i].name + "\')\">";
                        text += places[i].name;
                        text += "</td>";

                        text += "<td name = " + "'map' id = \'map" + i + "\' value = \'" + places[i].vicinity + "\' onclick = \"showMap(\'" + places[i].vicinity + "\', \'map" + i + "\')\"" + ">";
                        text += places[i].vicinity;
                        text += "</td>";

                        text += "</tr>";
                    }
                    // }

                }
                text += "</table>";
                document.getElementById("showPlaces").innerHTML = text;
            }

            function showDetails(place_id, place_name) {

                var xmlhttp;
                var jsonObj;
                if (window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                        jsonObj = xmlhttp.responseText;
                        console.log(jsonObj);
                        var json = JSON.parse(jsonObj);
                        console.log(json);

                        //                        var reviews = json.reviews;
                        //                        var o = new Object(json);
                        var reviews;
                        //                        if (!json.hasOwnProperty('reviews')) {
                        //                            alert("here1");
                        //                            reviews  = new Array();  
                        //                        } else {
                        //                            alert("here2");
                        //                            reviews = json.reviews;
                        //                        }
                        try {
                            reviews = json.reviews;
                        } catch (error) {
                            alert("error");
                            reviews = new Array();
                        }

                        var photos = json.photos;

                        // draw details overview info
                        document.getElementById("places").innerHTML = "";
                        var height = 25;
                        var width = 40;
                        console.log("place_name: " + place_name);
                        console.log(document.getElementById("place_name"));
                        document.getElementById("place_name").innerHTML = "<h1>" + place_name + "</h1>";
                        document.getElementById("show_reviews").innerHTML = "<p>click to show reviews</p>";
                        document.getElementById("show_photos").innerHTML = "<p>click to show photos</p>";
                        document.getElementById("icon_reviews").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" height = " + height + " width = " + width + ">";
                        document.getElementById("icon_photos").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" height = " + height + " width = " + width + ">";

                        document.getElementById("showMap").innerHTML = "";
                        document.getElementById("travelMode").innerHTML = "";
                        drawReviews(reviews);
                        document.getElementById("draw_reviews").style.display = "none";
                        getPhotos(place_id);

                        document.getElementById("draw_photos").style.display = "none";


                        document.getElementById("icon_reviews").onclick = function() {
                            if (document.getElementById("draw_reviews").style.display == "none") {
                                document.getElementById("icon_reviews").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png\" height = " + height + " width = " + width + ">";
                                document.getElementById("draw_reviews").style.display = "inline";
                                document.getElementById("draw_photos").style.display = "none";
                                document.getElementById("icon_photos").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" height = " + height + " width = " + width + ">";
                            } else {
                                document.getElementById("icon_reviews").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" height = " + height + " width = " + width + ">";
                                document.getElementById("draw_reviews").style.display = "none";
                            }
                        }
                        document.getElementById("icon_photos").onclick = function() {
                            if (document.getElementById("draw_photos").style.display == "none") {
                                document.getElementById("icon_photos").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png\" height = " + height + " width = " + width + ">";
                                document.getElementById("draw_photos").style.display = "inline";
                                document.getElementById("draw_reviews").style.display = "none";
                                document.getElementById("icon_reviews").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" height = " + height + " width = " + width + ">";
                            } else {
                                document.getElementById("icon_photos").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png\" height = " + height + " width = " + width + ">";
                                document.getElementById("draw_photos").style.display = "none";
                            }
                        }
                    }
                };
                var form = new FormData();
                form.append("name", place_id);
                xmlhttp.open("POST", "<?php echo $_SERVER['PHP_SELF'];?>", false);
                xmlhttp.send(form);
                return false;
            }

            function drawReviews(reviews) {

                var text = "";
                text += "<table id = \'review_table\'>"
                var num;
                if (reviews.length > 5) {
                    num = 5;
                } else {
                    num = reviews.length;
                }
                if (num == 0) {
                    text += "<tr><td>";
                    text += "<p id = \'review_empty\'>No Reviews Found</p>"
                    text += "</td></tr>";
                }
                for (i = 0; i < num; i++) {
                    text += "<tr><td id = \'author_name\'>";
                    text += "<img src = \"" + reviews[i].profile_photo_url + "\" id = \"photo\">";
                    text += reviews[i].author_name;
                    text += "</td></tr>";

                    text += "<tr><td id = \'text\'>";
                    text += reviews[i].text;
                    text += "</td></tr>";
                }
                text += "</table>";

                document.getElementById("draw_reviews").innerHTML = text;
            }

            function getPhotos(place_id) {
                //document.getElementById("icon_photos").innerHTML = "<img src = \"http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png\" height = " + height + " width = " + width + ">";
                var xmlhttp;
                var jsonObj;
                var photos;
                if (window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                        jsonObj = xmlhttp.responseText;
                        var json = JSON.parse(jsonObj);
                        photos = json.photos;
                        console.log(photos);

                        drawPhotos(photos);
                    }
                }

                var form = new FormData();
                form.append("name", place_id);
                xmlhttp.open("POST", "<?php echo $_SERVER['PHP_SELF'];?>", false);
                xmlhttp.send(form);
                return false;
            }

            function drawPhotos(photos) {
                var text = "";
                text += "<table id = \'photo_table\'>";

                var num;
                if (photos.length > 5) {
                    num = 5;
                } else {
                    num = photos.length;
                }

                if (num == 0) {
                    text += "<tr><td>";
                    text += "<p id = \'photo_empty\'>No Photos Found</p>";
                    text += "</td></tr>";
                } else {
                    for (i = 0; i < num; i++) {
                        text += "<tr><td>";
                        text += "<a href = '" + photos[i] + "' target = \'_blank\'><img src = '" + photos[i] + "' id = \'photos\'></a>";
                        text += "</td></tr>";
                    }
                }

                text += "</table>";

                document.getElementById("draw_photos").innerHTML = text;
            }

            function setMapPosition(tag_id) {
                var map = document.getElementById("showMap");
                console.log("set map position");
                var left = document.getElementById(tag_id).offsetLeft;
                var top = document.getElementById(tag_id).offsetTop;
                console.log("left: " + left);
                console.log("top: " + top);
                map.style.left = left + 90 + "px";
                map.style.top = top + 240 + "px";
            }

            function setTravelModePosition(tag_id) {
                var travel_mode = document.getElementById("travelMode");
                var left = document.getElementById(tag_id).style.left;
                var top = document.getElementById(tag_id).style.top;
                console.log("left: " + left);
                console.log("top: " + top);
                travel_mode.style.left = left;
                travel_mode.style.top = top;
            }

            function drawTravelMode() {
                var text = "";
                text += "<table>";

                text += "<tr><td id = \'walk\'>";
                text += "Walk there";
                text += "</td></tr>";

                text += "<tr><td id = \'bike\'>";
                text += "Bike there";
                text += "</td></tr>";

                text += "<tr><td id = \'drive\'>";
                text += "Drive there";
                text += "</td></tr>";

                text += "</table>";

                document.getElementById("travelMode").innerHTML = text;
            }

        </script>
        <script>
            function showMap(address, tag_id) {

                var map = new google.maps.Map(document.getElementById('showMap'), {
                    zoom: 15,
                    center: {
                        lat: -34.0266, //-34.397,
                        lng: -118.2831 //150.644
                    }
                });
                // document.getElementById('showMap').style.display = "none";

                var geocoder = new google.maps.Geocoder();


                geocoder.geocode({
                    'address': address
                }, function(results, status) {
                    if (status === 'OK') {
                        // alert(address);
                        map.setCenter(results[0].geometry.location);
                        var marker = new google.maps.Marker({
                            map: map,
                            position: results[0].geometry.location
                        });
                        drawTravelMode();
                        showRoute(address, map);
                    } else {
                        //console.log(address);
                        alert('Geocode was not successful for the following reason: ' + status);
                    }

                });

                // document.getElementById('showMap').style.display = "block";
                if (document.getElementById('showMap').style.display == "none") {
                    setMapPosition(tag_id);
                    setTravelModePosition("showMap");
                    document.getElementById('showMap').style.display = "block";
                    document.getElementById('travelMode').style.display = "block";
                } else {
                    document.getElementById('showMap').style.display = "none";
                    document.getElementById('travelMode').style.display = "none";
                }
            }

            function showRoute(address, map) {
                var directionsService = new google.maps.DirectionsService;
                var directionsDisplay = new google.maps.DirectionsRenderer;
                directionsDisplay.setMap(map);
                var origin;
                var destination = address;
                if (document.getElementById("here").checked) {
                    origin = new google.maps.LatLng(latitude, longitude);
                    console.log("origin: " + origin);
                } else {
                    origin = document.getElementById("inputLocation").value;
                    console.log("origin" + origin);
                }
                console.log("destination: " + destination);
                document.getElementById("walk").onclick = function() {
                    directionsService.route({
                        origin: origin,
                        destination: destination,
                        travelMode: 'WALKING' //'DRIVING'
                    }, function(response, status) {
                        if (status === 'OK') {
                            directionsDisplay.setDirections(response);
                        } else {
                            window.alert('Directions request failed due to ' + status);
                        }
                    });
                };
                document.getElementById("bike").onclick = function() {
                    directionsService.route({
                        origin: origin,
                        destination: destination,
                        travelMode: 'BICYCLING' //'DRIVING'
                    }, function(response, status) {
                        if (status === 'OK') {
                            directionsDisplay.setDirections(response);
                        } else {
                            window.alert('Directions request failed due to ' + status);
                        }
                    });
                };
                document.getElementById("drive").onclick = function() {
                    directionsService.route({
                        origin: origin,
                        destination: destination,
                        travelMode: 'DRIVING'
                    }, function(response, status) {
                        if (status === 'OK') {
                            directionsDisplay.setDirections(response);
                        } else {
                            window.alert('Directions request failed due to ' + status);
                        }
                    });
                };


                //                document.getElementById('walk').addEventListener('click', function() {
                //                    console.log("walk route");
                //                    directionsService.route({
                //                        origin: origin,
                //                        destination: destination,
                //                        travelMode: 'WALKING' //'DRIVING'
                //                    }, function(response, status) {
                //                        if (status === 'OK') {
                //                            console.log("get response");
                //                            directionsDisplay.setDirections(response);
                //                        } else {
                //                            window.alert('Directions request failed due to ' + status);
                //                        }
                //                    });
                //                });
                //                document.getElementById('bike').addEventListener('click', function() {
                //                    directionsService.route({
                //                        origin: origin,
                //                        destination: destination,
                //                        travelMode: 'BICYCLING' //'DRIVING'
                //                    }, function(response, status) {
                //                        if (status === 'OK') {
                //                            directionsDisplay.setDirections(response);
                //                        } else {
                //                            window.alert('Directions request failed due to ' + status);
                //                        }
                //                    });
                //                });
                //                document.getElementById('drive').addEventListener('click', function() {
                //                    directionsService.route({
                //                        origin: origin,
                //                        destination: destination,
                //                        travelMode: 'DRIVING'
                //                    }, function(response, status) {
                //                        if (status === 'OK') {
                //                            directionsDisplay.setDirections(response);
                //                        } else {
                //                            window.alert('Directions request failed due to ' + status);
                //                        }
                //                    });
                //                });
            }

        </script>
        <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBYCIMYf2C7imUDRmCq9h1kJjxkrMUfhYY">


        </script>
    </body>

    </html>
