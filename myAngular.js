var myApp = angular.module("myApp", []);
var next_page_token;
var place_data = new Array(3);
var place_data_page = 0;
var map;
var service;
var data_places;
var place_id;
var previous_place_id = "";
var place_details;
var date = new Date();
var today = date.getDay();
var current_lat;
var current_lon;
var favorite_items = [];
var twitter_content = "https://twitter.com/intent/tweet?hashtags=TravelAndEntertainmentSearch";
var page = 0; // favorite page

// autocomplete
var placeSearch, autocomplete;
var componentForm = {
    street_number: 'short_name',
    route: 'long_name',
    locality: 'long_name',
    administrative_area_level_1: 'short_name',
    country: 'long_name',
    postal_code: 'short_name'
};

function initAutocomplete() {
    // Create the autocomplete object, restricting the search to geographical
    // location types.
    autocomplete = new google.maps.places.Autocomplete(
        /** @type {!HTMLInputElement} */
        (document.getElementById('inputLocation')), {
            types: ['geocode']
        });
    autocomplete_map_from = new google.maps.places.Autocomplete(
        /** @type {!HTMLInputElement} */
        (document.getElementById('map_from')), {
            types: ['geocode']
        });

    // When the user selects an address from the dropdown, populate the address
    // fields in the form.
    autocomplete.addListener('place_changed', fillInAddress);
    autocomplete_map_from.addListener('place_changed', fillInAddress);
}

function fillInAddress() {
    // Get the place details from the autocomplete object.
    var place = autocomplete.getPlace();

    for (var component in componentForm) {
        document.getElementById(component).value = '';
        document.getElementById(component).disabled = false;
    }

    // Get each component of the address from the place details
    // and fill the corresponding field on the form.
    for (var i = 0; i < place.address_components.length; i++) {
        var addressType = place.address_components[i].types[0];
        if (componentForm[addressType]) {
            var val = place.address_components[i][componentForm[addressType]];
            document.getElementById(addressType).value = val;
        }
    }
}

// Bias the autocomplete object to the user's geographical location,
// as supplied by the browser's 'navigator.geolocation' object.
function geolocate() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            var geolocation = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            var circle = new google.maps.Circle({
                center: geolocation,
                radius: position.coords.accuracy
            });
            autocomplete.setBounds(circle.getBounds());
        });
    }
}

myApp.controller("formController", function ($scope, $http) {
    var url = "http://ip-api.com/json";
    $http.get(url).then(function (response) {
        console.log(response.data);
        $scope.lat = response.data.lat;
        $scope.lon = response.data.lon;
        current_lat = response.data.lat;
        current_lon = response.data.lon;
    });

    $scope.makeRequest = function () {
        // progress half
        document.getElementById("progress_bar").style.display = 'block';
        document.getElementById("progress_half").style.display = 'block';
        document.getElementById("progress_done").style.display = 'none';
        place_data_page = 0;
        var xmlDocument;
        if ($scope.location != "other_location") {
            $scope.location = "current_location";
        }
        xmlDocument = {
            "keyword": $scope.keyword,
            "category": $scope.category,
            "distance": $scope.distance,
            "location": $("input[name='location']:checked").val(),
            "input_location": document.getElementById("inputLocation").value,
            "latitude": $scope.lat,
            "longitude": $scope.lon
        };
        console.log(xmlDocument);

        var xmlRequest = $.ajax({
            url: 'http://newapplication-env.us-east-2.elasticbeanstalk.com/a',
            //            processData: false,
            type: 'GET',
            data: xmlDocument,
            dataType: "json",
            success: function (data) {
                console.log("success");
                console.log(data);
                place_data[place_data_page] = data;
                showTable(data);
            },
            fail: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log("error");
                var text = "<div class = \'alert alert-danger\' role = \'alert\' id = \'alert_danger_places\'>Failed to get search results.</div>";
                document.getElementById("alerts").innerHTML = text;
                document.getElementById("alerts").style.display = 'block';
                document.getElementById("page_buttons_results").style.display = 'none';
                document.getElementById("page_buttons_fav").style.display = 'none';
                document.getElementById("Details_button").style.display = 'none';
                document.getElementById("details").style.display = 'none';
                document.getElementById("showPlaces").style.display = 'none';
            }
        });

        //        xmlRequest.done(function (data) {
        //            console.log(data);
        //            place_data[place_data_page] = data;
        //            showTable(data);
        //        });
        //
        //        xmlRequest.fail(function (XMLHttpRequest, textStatus, errorThrown) {
        //            console.log("error");
        //            var text = "<div class = \'alert alert-danger\' role = \'alert\' id = \'alert_danger_places\'>Failed to get search results.</div>";
        //            document.getElementById("alerts").innerHTML = text;
        //            document.getElementById("alerts").style.display = 'block';
        //            document.getElementById("page_buttons_results").style.display = 'none';
        //            document.getElementById("page_buttons_fav").style.display = 'none';
        //            document.getElementById("Details_button").style.display = 'none';
        //            document.getElementById("details").style.display = 'none';
        //            document.getElementById("showPlaces").style.display = 'none';
        //        });
    }
});

function requestDetails(place_id, latitude, longitude, results_fav) {
    var request = {
        placeId: place_id
    };
    console.log("request:" + JSON.stringify(request));

    map = new google.maps.Map(document.getElementById('map'), {
        center: {
            lat: latitude,
            lng: longitude
        },
        zoom: 15
    });
    var marker = new google.maps.Marker({
        position: {
            lat: latitude,
            lng: longitude
        },
        map: map,
    });
    marker.setMap(map);
    service = new google.maps.places.PlacesService(map);

    service.getDetails(request, callback);

    function callback(place, status) {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
            place_details = place;
            showDetails(place_details, place_id, results_fav);
            document.getElementById("Details").disabled = false;
            document.getElementById("Details").onclick = function () {
                showDetails(place_details, place_id, results_fav);
            };
        } else {
            var text = "<div class = \'alert alert-danger\' role = \'alert\' id = \'alert_danger_details\'>Failed to get search results.</div>";
            document.getElementById("alerts").innerHTML = text;
            document.getElementById("alerts").style.display = 'block';
            document.getElementById("showPlaces").style.display = 'none';
            document.getElementById("page_buttons_results").style.display = 'none';
            document.getElementById("page_buttons_fav").style.display = 'none';
            document.getElementById("Details_button").style.display = 'none';
            document.getElementById("details").style.display = 'none';
        }
    }
}

function showTable(data) {
    document.getElementById("showPlaces").style.paddingTop = "0";
    document.getElementById("details").style.display = 'none';
    data_places = data;
    console.log("data:" + data);

    var places = data.results;

    if (places == null || places.length == 0) {
        // no records for places table

        var text = "";
        text += "<div class = \'alert alert-warning\' role = \'alert\' id = \'alert_warning_places\'>No records.</div>";
        document.getElementById("showPlaces").innerHTML = text;
        document.getElementById("showPlaces").style.paddingTop = "100px";

        // progress done
        document.getElementById("progress_half").style.display = 'none';
        document.getElementById("progress_done").style.display = 'block';

        document.getElementById("progress_bar").style.display = 'none';
        document.getElementById("progress_half").style.display = 'none';
        document.getElementById("progress_done").style.display = 'none';

        document.getElementById("alerts").style.display = 'none';
        document.getElementById("page_buttons_results").style.display = 'none';
        document.getElementById("page_buttons_fav").style.display = 'none';
        document.getElementById("Details_button").style.display = 'none';
        document.getElementById("details").style.display = 'none';
        document.getElementById("showPlaces").style.display = 'block';
    } else {
        var text = "";
        text += "<table id = " + "'places'" + ">";
        // output header
        var headers = new Array(6);
        headers[0] = "#";
        headers[1] = "Category";
        headers[2] = "Name";
        headers[3] = "Address";
        headers[4] = "Favorite";
        headers[5] = "Details";

        text += "<thead>";

        text += "<tr id = " + "'placeTableHeader'" + " height = \'50px\'>";
        for (i = 0; i < 6; i++) {
            text += "<th id = \'tableHeader\'>";
            text += "<p id = \'headers\'>" + headers[i] + "</p>";
            text += "</th>";
        }

        text += "</tr>";

        text += "</thead>"

        // output places
        for (i = 0; i < places.length; i++) {

            place_id = places[i].place_id;
            var lat = places[i].geometry.location.lat;
            var lng = places[i].geometry.location.lng;

            text += "<tr class = \'places_row\' id = \'" + place_id + "\' height = \'50px\'>";

            text += "<td font-weight = \'bold\' width = \'40px\'>" + (i + 1) + "</td>";

            text += "<td width = \'80px\'>";
            imgURL = places[i].icon;
            x = 50;
            y = 20;
            text += "<img src='" + imgURL + "' width='" + x + "' height='" + y + "'>";
            text += "</td>";

            text += "<td class = \'hover\' name = \'name\' value = " + place_id + ">";
            //text += "<td class = \'hover\' name = \'name\' value = " + place_id + " onclick = \"showDetails(\'" + place_id + "\')\">";
            text += places[i].name;
            text += "</td>";

            text += "<td class = \'hover\' name = " + "'map' id = \'map" + place_id + "\' value = \'" + places[i].vicinity + "\'>";
            text += places[i].vicinity;
            text += "</td>";

            text += "<td id = \'favor" + place_id + "\' width = \'70px\'><button class = \'btn btn-default\' type = \'button\' id = \'button" + place_id + "\' onclick = \"fav_action(\'" + place_id + "\')\"><i class=\'far fa-star\'></i></button></td>";

            text += "<td id = \'detail" + place_id + "\' width = \'60px\'><button class = \'btn btn-default\' type = \'button\' onclick = \"requestDetails(\'" + place_id + "\'," + lat + "," + lng + ", " + "\'from results\')\"><i class=\'fas fa-chevron-right\'></i></button></td>";

            text += "</tr>";
        }

        text += "</table>";
        document.getElementById("showPlaces").innerHTML = text;
        document.getElementById("showPlaces").style.paddingTop = "0";

        // deal with favorites
        for (var i = 0; i < places.length; i++) {
            for (var j = 0; j < favorite_items.length; j++) {
                if (places[i].place_id == favorite_items[j].place_id) {
                    // change star
                    document.getElementById("button" + places[i].place_id).innerHTML = "<div class = \'fav_star_push\'><i class=\'fas fa-star\'></i><div>";
                    // change background color
                    document.getElementById("" + places[i].place_id).style.backgroundColor = "#f9de9f";
                }
            }
        }

        // progress done
        document.getElementById("progress_half").style.display = 'none';
        document.getElementById("progress_done").style.display = 'block';

        document.getElementById("progress_bar").style.display = 'none';
        document.getElementById("progress_half").style.display = 'none';
        document.getElementById("progress_done").style.display = 'none';

        document.getElementById("Details_button").style.display = 'block';
        document.getElementById("showPlaces").style.display = 'block';
        document.getElementById("showFavorites").style.display = 'none';


        next_page_token = data.next_page_token;
        console.log("next_page_token:" + next_page_token);
        var hasNextPage = next_page_token != 'undefined' && next_page_token != null;
        var hasPreviousPage = place_data_page != 0;
        console.log("page:" + place_data_page);
        if (hasNextPage && !hasPreviousPage) { // first page with next_page_token
            var button_text = "";
            button_text += "<button type = \'button\' id = \'next\' class = \'btn btn-default\' onclick = \'showNext()\'>Next";
            button_text += "</button>";
            document.getElementById("page_buttons_results").innerHTML = button_text;
        } else if (!hasNextPage && hasPreviousPage) { // last page with previous
            var button_text = "";
            button_text += "<button type = \'button\' id = \'previous\' class = \'btn btn-default\' onclick = \'showPrevious()\'>Previous";
            button_text += "</button>";
            document.getElementById("page_buttons_results").innerHTML = button_text;
        } else if (!hasNextPage && !hasPreviousPage) { // first page without next_page_token
            document.getElementById("page_buttons_results").innerHTML = "";
            place_data_page = 0;
        } else { // pages with both next and previous
            var button_text = "";
            button_text += "<table frame = void id = \'button_table\'><tr>"
            button_text += "<td id = \'previous\'><button type = \'button\' id = \'previous\' class = \'btn btn-default\' onclick = \'showPrevious()\'>Previous";
            button_text += "</button></td>";
            button_text += "<td id = \'next\'><button type = \'button\' id = \'next\' class = \'btn btn-default\' onclick = \'showNext()\'>Next";
            button_text += "</button></td></table>";
            document.getElementById("page_buttons_results").innerHTML = button_text;
            //document.getElementById("previous").paddingRight = "15px";
        }
        document.getElementById("page_buttons_results").style.display = 'block';
    }

}

function fav_action(place_id) {
    var place;
    for (var i = 0; i < data_places.results.length; i++) {
        if (place_id == data_places.results[i].place_id) {
            place = data_places.results[i];
            break;
        }
    }
    // add fav
    if (!favorite_items.includes(place)) {
        // change star
        document.getElementById("button" + place_id).innerHTML = "<div class = \'fav_star_push\'><i class=\'fas fa-star\'></i><div>";
        // add fav item
        favorite_items.push(place);
    } else { // remove fav
        // change star
        document.getElementById("button" + place_id).innerHTML = "<div class = \'fav_star_remove\'><i class=\'far fa-star\'></i><div>"
        // remove fav item
        var index = favorite_items.indexOf(place);
        favorite_items.splice(index, 1);
    }
}

function showFav() {
    document.getElementById("showPlaces").style.display = 'none';
    document.getElementById("details").style.display = 'none';
    document.getElementById("page_buttons_results").style.display = 'none';

    var number = favorite_items.length;
    console.log("fav_num:" + number);

    if (number != 0) {
        document.getElementById("alerts").style.display = 'none';
        var text = "<table id = " + "'favorites_table'" + ">";
        var page_num;
        if (number % 20 != 0) {
            // Math.floor: get []
            page_num = Math.floor(number / 20); // page start from 0
        } else {
            page_num = Math.floor(number / 20) - 1; // page start from 0
        }
        // output header
        var headers = new Array(6);
        headers[0] = "#";
        headers[1] = "Category";
        headers[2] = "Name";
        headers[3] = "Address";
        headers[4] = "Favorite";
        headers[5] = "Details";

        text += "<thead>";

        text += "<tr id = " + "'placeTableHeader'" + " height = \'50px\'>";
        for (i = 0; i < 6; i++) {
            text += "<th id = \'tableHeader\'>";
            text += "<p id = \'headers\'>" + headers[i] + "</p>";
            text += "</th>";
        }

        text += "</tr>";

        text += "</thead>"

        // output places
        if (number <= 20) {
            for (var i = 0; i < number; i++) {
                place_id = favorite_items[i].place_id;
                var lat = favorite_items[i].geometry.location.lat;
                var lng = favorite_items[i].geometry.location.lng;
                text += "<tr class = \'places_row\' id = \'fav" + place_id + "\' height = \'50px\'>";
                // col 1
                text += "<td width = \'40px\'>" + (i + 1) + "</td>";

                // col2
                text += "<td>";
                imgURL = favorite_items[i].icon;
                x = 50;
                y = 20;
                text += "<img src='" + imgURL + "' width='" + x + "' height='" + y + "'>";
                text += "</td>";

                // col3
                text += "<td class = \'hover\' name = \'name\' value = " + place_id + ">";
                text += favorite_items[i].name;
                text += "</td>";

                // col4
                text += "<td class = \'hover\' name = " + "'map' id = \'map" + place_id + "\' value = \'" + favorite_items[i].vicinity + "\'>";
                text += favorite_items[i].vicinity;
                text += "</td>";

                // col5
                text += "<td id = \'favor" + place_id + "\'><button class = \'btn btn-default\' type = \'button\' id = \'button" + place_id + "\' onclick = \"remove_fav_action(" + i + ", \'" + place_id + "\')\" width = \'70px\'><i class=\"fas fa-trash-alt\"></i></button></td>";

                // col6
                var lat = favorite_items[i].geometry.location.lat;
                var lng = favorite_items[i].geometry.location.lng;
                text += "<td id = \'detail" + place_id + "\'><button class = \'btn btn-default\' type = \'button\' onclick = \"requestDetails(\'" + place_id + "\'," + lat + "," + lng + ", " + "\'from favorites\')\" width = \'60px\'><i class=\'fas fa-chevron-right\'></i></button></td>";
                text += "</tr>";
            }
            text += "</table>";
            document.getElementById("showFavorites").innerHTML = text;
        } else {
            for (var i = page * 20; i < Math.min(page * 20 + 20, number); i++) {
                place_id = favorite_items[i].place_id;
                text += "<tr class = \'places_row\' height = \'50px\'>";
                // col 1
                text += "<td width = \'40px\'>" + (i + 1) + "</td>";

                // col2
                text += "<td>";
                imgURL = favorite_items[i].icon;
                x = 50;
                y = 20;
                text += "<img src='" + imgURL + "' width='" + x + "' height='" + y + "'>";
                text += "</td>";

                // col3
                text += "<td class = \'hover\' name = \'name\' value = " + place_id + ">";
                text += favorite_items[i].name;
                text += "</td>";

                // col4
                text += "<td class = \'hover\' name = " + "'map' id = \'map" + place_id + "\' value = \'" + favorite_items[i].vicinity + "\'>";
                text += favorite_items[i].vicinity;
                text += "</td>";

                // col5
                text += "<td id = \'favor" + place_id + "\'><button class = \'btn btn-default\' type = \'button\' id = \'button" + place_id + "\' onclick = \"remove_fav_action(" + i + ", \'" + place_id + "\')\"><i class=\"fas fa-trash-alt\" width = \'70px\'></i></button></td>";

                // col6
                var lat = favorite_items[i].geometry.location.lat;
                var lng = favorite_items[i].geometry.location.lng;
                text += "<td id = \'detail" + place_id + "\'><button class = \'btn btn-default\' type = \'button\' onclick = \"requestDetails(\'" + place_id + "\'," + lat + "," + lng + ", \'from favorites\')\" width = \'60px\'><i class=\'fas fa-chevron-right\'></i></button></td>";
                text += "</tr>";
            }
            if (page < page_num) { // has next page
                console.log("page:" + page);
                console.log("page_num:" + page_num);
                if (page == 0) { // has no previous page
                    var button_text = "";
                    button_text += "<button type = \'button\' id = \'fav_next\' class = \'btn btn-default\' onclick = \'show_fav_next()\'>Next";
                    button_text += "</button>";
                    document.getElementById("page_buttons_fav").innerHTML = button_text;
                } else { // has both next and previous pages
                    var button_text = "";
                    button_text += "<button type = \'button\' id = \'fav_previous\' class = \'btn btn-default\' onclick = \'show_fav_pre()\'>Previous";
                    button_text += "</button>";
                    button_text += "<button type = \'button\' id = \'fav_next\' class = \'btn btn-default\' onclick = \'show_fav_next()\'>Next";
                    button_text += "</button>";
                    document.getElementById("page_buttons_fav").innerHTML = button_text;
                }
            } else { // has previous page
                var button_text = "";
                button_text += "<button type = \'button\' id = \'fav_previous\' class = \'btn btn-default\' onclick = \'show_fav_pre()\'>Previous";
                button_text += "</button>";
                document.getElementById("page_buttons_fav").innerHTML = button_text;
            }

            text += "</table>";
            document.getElementById("showFavorites").innerHTML = text;
            document.getElementById("Details_button").style.display = 'block';
            document.getElementById("page_buttons_fav").style.display = 'block';
        }
    } else {
        var text = "<div class = \'alert alert-warning\' role = \'alert\' id = \'alert_warning_details\'>No records.</div>";
        document.getElementById("alerts").innerHTML = text;
        document.getElementById("alerts").style.display = 'block';
        document.getElementById("showFavorites").innerHTML = "";
        document.getElementById("Details_button").style.display = 'none';
    }

    for (var i = 0; i < favorite_items.length; i++) {
        if (favorite_items[i].place_id === previous_place_id) {
            if (document.getElementById("fav" + previous_place_id) != undefined && document.getElementById("fav" + previous_place_id) != null) {
                document.getElementById("fav" + previous_place_id).style.backgroundColor = "#f9de9f";
            }
        }
    }

    document.getElementById("showFavorites").style.display = 'block';
}

function remove_fav_action(index, place_id) {
    // remove item
    favorite_items.splice(index, 1);
    // repaint favorite table
    showFav();
    // remove fav and deal with showtable display
    document.getElementById("button" + place_id).innerHTML = "<i class=\'far fa-star\'></i>";
}

function showNext() {
    var data = {
        "next_page_token": next_page_token
    };
    var xmlRequest = $.ajax({
        url: 'http://newapplication-env.us-east-2.elasticbeanstalk.com/next',
        //            processData: false,
        type: 'GET',
        data: data,
        dataType: "json"
    });

    xmlRequest.done(function (data) {
        console.log(data);
        place_data[++place_data_page] = data;
        showTable(data);
    });
}

function showPrevious() {
    console.log(place_data);
    console.log("place_data_page:" + place_data_page);
    showTable(place_data[--place_data_page]);
}

function show_fav_next() {
    page++;
    showFav();
}

function show_fav_pre() {
    page--;
    showFav();
}

function showResults() {
    //console.log(document.getElementById("showPlaces").innerHTML);
    if (document.getElementById("showPlaces").innerHTML === "<div class=\"alert alert-warning\" role=\"alert\" id=\"alert_warning_places\">No records.</div>") {
        console.log("warning");
        document.getElementById("showPlaces").style.paddingTop = "100px";
        document.getElementById("Details_button").style.display = 'none';
        document.getElementById("showFavorites").style.display = 'none';
        document.getElementById("page_buttons_fav").style.display = 'none';
        document.getElementById("page_buttons_results").style.display = 'none';
        document.getElementById("details").style.display = 'none';
        document.getElementById("alerts").style.display = 'none';
    } else {
        document.getElementById("showFavorites").style.display = 'none';
        document.getElementById("page_buttons_fav").style.display = 'none';
        document.getElementById("page_buttons_results").style.display = 'none';
        document.getElementById("details").style.display = 'none';
        document.getElementById("page_buttons_results").style.display = 'block';
        document.getElementById("Details_button").style.display = 'block';
        document.getElementById("alerts").style.display = 'none';
    }
    document.getElementById("showPlaces").style.display = 'block';
}

function showDetails(place_details, place_id, result_fav) {
    document.getElementById("Details_button").style.display = 'none';
    document.getElementById("page_buttons_fav").style.display = 'none';
    document.getElementById("showFavorites").style.display = 'none';
    document.getElementById("showPlaces").style.display = 'none';
    document.getElementById("page_buttons_results").style.display = 'none';
    if (previous_place_id != "") {
        if (typeof (document.getElementById(previous_place_id)) != 'undefined' && typeof (document.getElementById(previous_place_id)) != null) {
            document.getElementById(previous_place_id).style.backgroundColor = "white";
        }

        for (var i = 0; i < favorite_items.length; i++) {
            if (favorite_items[i].place_id === previous_place_id && document.getElementById("fav" + previous_place_id) != 'undefined' != document.getElementById("fav" + previous_place_id) == null) {
                document.getElementById("fav" + previous_place_id).style.backgroundColor = "white";
            }
        }
    }
    document.getElementById(place_id).style.backgroundColor = "#f9de9f";
    for (var i = 0; i < favorite_items.length; i++) {
        if (favorite_items[i].place_id === place_id && document.getElementById("fav" + place_id) != 'undefined' != document.getElementById("fav" + place_id) == null) {
            document.getElementById("fav" + place_id).style.backgroundColor = "#f9de9f";
        }
    }

    previous_place_id = place_id;

    // if the item is in favorite, color the star, otherwise uncolor the star
    var i;
    var is_favorite = false;
    for (i = 0; i < favorite_items.length; i++) {
        if (favorite_items[i].place_id == place_id) {
            is_favorite = true;
            break;
        }
    }
    if (is_favorite) {
        document.getElementById("favorite").innerHTML = "<div class = \'fav_star_push\'><i class=\'fas fa-star\'></i><div>";
    } else {
        document.getElementById("favorite").innerHTML = "<i class=\"far fa-star\">";
    }

    document.getElementById("favorite").onclick = function () {
        if (document.getElementById("favorite").innerHTML === "<div class = \'fav_star_push\'><i class=\'fas fa-star\'></i><div>") {
            // change star to remove fav
            document.getElementById("favorite").innerHTML = "<i class=\"far fa-star\">";
        } else { // change star to add fav
            document.getElementById("favorite").innerHTML = "<div class = \'fav_star_push\'><i class=\'fas fa-star\'></i><div>";
        }
        fav_action(place_id);
    }
    //    <a href=' "" + encodeURI(name) + "%20Located%20at%20" + encodeURI(address) + ".%20Website:%20" + encodeURI(website) + "\"%20%20TravelAndEntertainmentSearch' class='twitter-share-button' data-show-count='false'><button class='btn' style='background-color:#00aced !important';><i class='fab fa-twitter' color='white'></i></button></a >
    var name = place_details.name;
    var address = place_details.formatted_address;
    var website = place_details.website;

    twitter_content += "&text=Check%20out%20" + encodeURI(name) + "%20located%20at%20" + encodeURI(address) + ".%20Website:%20" + encodeURI(website);

    // display place name
    var place_name_text = place_details.name;
    document.getElementById("place_name").innerHTML = place_name_text;

    showInfo(place_details);
    document.getElementById("Info_bar").onclick = function () {
        showInfo(place_details);
    }
    document.getElementById("Photos_bar").onclick = function () {
        showPhotos(place_details);
    }
    document.getElementById("Map_bar").onclick = function () {
        showMap(place_details);
    }
    document.getElementById("Reviews_bar").onclick = function () {
        showReviews(place_details);
    }
    document.getElementById("back_to_list").onclick = function () {
        if (result_fav == "from results") {
            document.getElementById("showPlaces").style.display = 'block';
            document.getElementById("page_buttons_results").style.display = 'block';
        } else {
            showFav();
            document.getElementById("showFavorites").style.display = 'block';
            document.getElementById("page_buttons_fav").style.display = 'block';
        }
        document.getElementById("details").style.display = 'none';
        document.getElementById("Details").disabled = false;
        document.getElementById("Details_button").style.display = 'block';
    }
}

function showInfo(place_details) {
    if (today == 0) {
        today = 7;
    }
    document.getElementById("photos").style.display = 'none';
    document.getElementById("Map").style.display = 'none';
    document.getElementById("reviews").style.display = 'none';
    document.getElementById("alerts").style.display = 'none';

    // display place address
    if (typeof (place_details.formatted_address) == "undefined") {
        document.getElementById("info_row_1").style.display = 'none';
    } else {
        var address_text = place_details.formatted_address;
        document.getElementById("info_address").innerHTML = address_text;
    }

    // display place phone number
    if (typeof (place_details.international_phone_number) == "undefined") {
        document.getElementById("info_row_2").style.display = 'none';
    } else {
        var phone_text = place_details.international_phone_number;
        document.getElementById("info_phone").innerHTML = phone_text;
    }

    // display place price level
    if (typeof (place_details.price_level) == "undefined") {
        document.getElementById("info_row_3").style.display = 'none';
    } else {
        var price_text = "";
        var i;
        for (var i = 0; i < place_details.price_level; i++) {
            price_text += "$";
        }
        document.getElementById("info_price").innerHTML = price_text;
    }

    // display place rating(stars)
    if (typeof (place_details.rating) == "undefined") {
        document.getElementById("info_row_4").style.display = 'none';
    } else {
        var rating_text = "" + place_details.rating;
        document.getElementById("rating_text").innerHTML = rating_text;
        var starPercentage = (parseFloat(place_details.rating) / 5) * 100;
        var starPercentageRounded = Math.round(starPercentage / 10) * 10 + "%";
        document.querySelector(".stars-inner").style.width = starPercentageRounded;
    }

    // display place google url
    if (typeof (place_details.url) == "undefined") {
        document.getElementById("info_row_5").style.display = 'none';
    } else {
        var google_text = "<a href = \'" + place_details.url + "\' target = \'_blank\'>" + place_details.url + "</a>";
        document.getElementById("info_google").innerHTML = google_text;
    }

    // display place website
    if (typeof (place_details.website) == "undefined") {
        document.getElementById("info_row_6").style.display = 'none';
    } else {
        var website_text = "<a href = \'" + place_details.website + "\' target = \'_blank\'>" + place_details.website + "</a>";
        document.getElementById("info_website").innerHTML = website_text;
    }

    // display place hours
    if (typeof (place_details.opening_hours) == "undefined") {
        document.getElementById("info_row_7").style.display = 'none';
    } else {
        var opening_hours = place_details.opening_hours;
        var is_open = opening_hours.open_now;

        var open_local_time_text = "";
        var week = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
        // deal with modal
        // Get the modal
        var modal = document.getElementById('myModal');
        // Get the link
        var link = document.getElementById('daily_open_hours_link');
        // Get the <span> element that closes the modal
        var span = document.getElementsByClassName("close")[0];
        // When the user clicks on <span> (x), close the modal
        span.onclick = function () {
            modal.style.display = "none";
        }
        // When the user clicks the link, open the modal 
        link.onclick = function () {
            modal.style.display = "block";
        }

        document.getElementById("close").onclick = function () {
            modal.style.display = 'none';
        }

        if (is_open) {
            open_local_time_text += "Open now";
            open_local_time_text += place_details.opening_hours.weekday_text[today - 1].substring((place_details.opening_hours.weekday_text[today % 7].indexOf(": ") + 2));
        } else {
            open_local_time_text += "Closed";
        }
        for (var i = today - 1; i < (today - 1) + 7; i++) {
            var index = i - (today - 1) + 1;
            document.getElementById("day" + index).innerHTML = week[i % 7];
            document.getElementById("hours" + index).innerHTML = "" + place_details.opening_hours.weekday_text[i % 7].substring(place_details.opening_hours.weekday_text[i % 7].indexOf(": ") + 2);
        }
        document.getElementById("row1").style.fontWeight = 'bold';
        document.getElementById("open_local_time").innerHTML = open_local_time_text;

        //console.log("hours:" + JSON.stringify(place_details.opening_hours.open_now));
        //console.log("hours:" + JSON.stringify(place_details.opening_hours.weekday_text));
    }

    document.getElementById("details").style.display = "block";
    document.getElementById("info").style.display = "block";
}

function showPhotos(place_details) {
    console.log("place_details:");
    console.log(place_details);
    console.log("show photos");
    document.getElementById("info").style.display = 'none';
    document.getElementById("Map").style.display = 'none';
    document.getElementById("reviews").style.display = 'none';
    var photos = place_details.photos;
    if (!(typeof (photos) == "undefined" || photos.length == 0)) {
        var len = photos.length;
        console.log("length:" + len);
        var photo_url = new Array(len);
        for (var i = 0; i < len; i++) {
            photo_url[i] = photos[i].getUrl({
                'maxWidth': photos[i].width,
                'maxHeight': photos[i].height
            });
            //console.log(photo_url[i]);
        }

        var tm = new Date();
        var photo_text1 = "";
        var photo_text2 = "";
        var photo_text3 = "";
        var photo_text4 = "";
        for (var i = 0; i < len; i++) {
            if (i % 4 == 0) {
                photo_text1 += "<div><a href = \'" + photo_url[i] + "\' target = \'_blank\'><img class = \'img-thumbnail\' src = \'" + photo_url[i] + "?time = " + tm.getMilliseconds() + "\' width = \'100%\'></a></div>";
            } else if (i % 4 == 1) {
                photo_text2 += "<div><a href = \'" + photo_url[i] + "\' target = \'_blank\'><img class = \'img-thumbnail\' src = \'" + photo_url[i] + "?time = " + tm.getMilliseconds() + "\' width = \'100%\'></a></div>";
            } else if (i % 4 == 2) {
                photo_text3 += "<div><a href = \'" + photo_url[i] + "\' target = \'_blank\'><img class = \'img-thumbnail\' src = \'" + photo_url[i] + "?time = " + tm.getMilliseconds() + "\' width = \'100%\'></a></div>";
            } else {
                photo_text4 += "<div><a href = \'" + photo_url[i] + "\' target = \'_blank\'><img class = \'img-thumbnail\' src = \'" + photo_url[i] + "?time = " + tm.getMilliseconds() + "\' width = \'100%\'></a></div>";
            }
        }

        document.getElementById("photo_col_1").innerHTML = photo_text1;
        document.getElementById("photo_col_2").innerHTML = photo_text2;
        document.getElementById("photo_col_3").innerHTML = photo_text3;
        document.getElementById("photo_col_4").innerHTML = photo_text4;

        document.getElementById("alerts").style.display = 'none';
        document.getElementById("photos").style.display = 'block';
    } else {
        var text = "";
        text += "<div class = \'alert alert-warning\' role = \'alert\' id = \'alert_warning_photos\'>No records.</div>";
        document.getElementById("alerts").innerHTML = text;
        document.getElementById("alerts").style.display = 'block';
        document.getElementById("photos").style.display = 'none';
    }
    document.getElementById("details").style.display = "block";
    
}

function showMap(place_details) {
    console.log("show map");
    document.getElementById("photos").style.display = 'none';
    document.getElementById("info").style.display = 'none';
    document.getElementById("reviews").style.display = 'none';
    document.getElementById("route_panel").innerHTML = "";
    document.getElementById("street_view").style.display = 'none';
    document.getElementById("alerts").style.display = 'none';

    var origin;
    var destination;

    var latitude;
    var longitude;

    var latlng = place_details.geometry.location;
    latitude = latlng.lat();
    longitude = latlng.lng();

    // From
    var location_select = $('input:radio[name="location"]:checked').val();
    if (location_select === "current_location") {
        document.getElementById("map_from").value = "Your location";
    } else {
        document.getElementById("map_from").value = document.getElementById("inputLocation").value;
    }

    if (document.getElementById("map_from").value === "Your location" || document.getElementById("map_from").value === "My location") {
        // use current location 
        origin = new google.maps.LatLng(current_lat, current_lon);
    } else {
        origin = new google.maps.LatLng(latitude, longitude); //document.getElementById("map_from").value;
    }

    // To
    document.getElementById("map_to").value = place_details.name + ", " + place_details.formatted_address;
    document.getElementById("map_to").readOnly = true;
    destination = place_details.formatted_address;

    // button
    var map_from_value = document.getElementById("map_from").value;
    if (map_from_value === "" || checkSpace(map_from_value)) {
        document.getElementById("get_directions").disabled = true;
    }

    console.log("origin:" + origin);
    var map = new google.maps.Map(document.getElementById('map'), {
        center: origin,
        zoom: 14
    });

    var marker = new google.maps.Marker({
        position: origin,
        map: map,
    });
    marker.setMap(map);

    document.getElementById("toggle_button_1").onclick = function () {
        var panorama = new google.maps.StreetViewPanorama(
            document.getElementById('street_view'), {
                position: latlng, //{lat: 42.345573, lng: -71.098326},
                pov: {
                    heading: 34,
                    pitch: 10
                }
            });
        //document.getElementById('map').style.display = 'none';
        //map.setStreetView(panorama);

        document.getElementById('map').style.display = 'none';
        document.getElementById('route_panel').style.display = 'none';
        document.getElementById('street_view').style.display = 'block';
        document.getElementById('toggle_map_1').style.display = 'none';
        document.getElementById('toggle_map_2').style.display = 'block';
    };

    document.getElementById("toggle_button_2").onclick = function () {
        document.getElementById('map').style.display = 'block';
        document.getElementById('route_panel').style.display = 'block';
        document.getElementById('street_view').style.display = 'none';
        document.getElementById('toggle_map_1').style.display = 'block';
        document.getElementById('toggle_map_2').style.display = 'none';
    };

    document.getElementById("get_directions").onclick = function () {
        document.getElementById("route_panel").innerHTML = "";
        if (document.getElementById("map_from").value === "Your location" || document.getElementById("map_from").value === "My location") {
            // use current location 
            origin = new google.maps.LatLng(current_lat, current_lon);
        } else {
            origin = document.getElementById("map_from").value;
        }
        // get directions
        var directionRequest = {
            origin: origin, //LatLng | String | google.maps.Place,
            destination: destination,
            travelMode: google.maps.TravelMode[document.getElementById("travel_mode").value], //TravelMode,
            //transitOptions: TransitOptions,
            //drivingOptions: DrivingOptions,
            //unitSystem: UnitSystem,
            //waypoints[]: DirectionsWaypoint,
            //optimizeWaypoints: Boolean,
            //provideRouteAlternatives: Boolean,
            //avoidFerries: Boolean,
            //avoidHighways: Boolean,
            //avoidTolls: Boolean,
            //region: String
        }
        var mapOptions = {
            zoom: 14,
            center: latlng
        }
        var map = new google.maps.Map(document.getElementById('map'), mapOptions);

        var directionsService = new google.maps.DirectionsService();
        var directionsDisplay = new google.maps.DirectionsRenderer({
            draggable: true,
            map: map,
            panel: document.getElementById("route_panel")
        });

        directionsDisplay.setMap(map);
        directionsDisplay.setPanel(document.getElementById("route_panel"));

        directionsService.route(directionRequest, function (response, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(response);
                console.log("response:" + JSON.stringify(response));
            }
        });
    }

    document.getElementById("details").style.display = "block";
    document.getElementById("Map").style.display = "block";
    document.getElementById("route_panel").style.display = 'block';
}

function showReviews(place_details) {
    console.log("place_detail:" + place_details);
    var utc_offset = place_details.utc_offset;

    if (typeof (place_details.reviews) == "undefined" || place_details.reviews.length == 0) {
        console.log("no reviews");
        var text = "<div class = \'alert alert-warning\' role = \'alert\' id = \'alert_warning_google_reviews\'>No records.</div>";
        document.getElementById("alerts").innerHTML = text;
        document.getElementById("info").style.display = 'none';
        document.getElementById("photos").style.display = 'none';
        document.getElementById("Map").style.display = 'none';
        document.getElementById("reviews_table").style.display = 'none';
        document.getElementById("yelp_table").style.display = 'none';
        document.getElementById("alerts").style.display = 'block';
        document.getElementById("reviews").style.display = 'block';
    } else {
        // deep copy
        var reviews_array = JSON.parse(JSON.stringify(place_details.reviews));

        document.getElementById("order_option").value = "Default Order";
        document.getElementById("order_option").onchange = function () {
            if (document.getElementById("order_option").value === "Default Order") {
                reviews_array = JSON.parse(JSON.stringify(place_details.reviews));
            }
            if (document.getElementById("reviews_option").value === "Google Reviews") {
                show_review_table(reviews_array, utc_offset);
            } else {
                request_yelp_match(place_details);
            }

        };
        show_review_table(reviews_array, utc_offset);
    }


    document.getElementById("reviews_option").onchange = function () {
        if (document.getElementById("reviews_option").value === "Yelp Reviews") {
            request_yelp_match(place_details);
        } else {
            show_review_table(reviews_array, utc_offset);
        }
    }
}

function show_review_table(reviews_array, utc_offset) {
    console.log("show reviews");
    document.getElementById("photos").style.display = 'none';
    document.getElementById("Map").style.display = 'none';
    document.getElementById("info").style.display = 'none';
    document.getElementById("yelp_table").style.display = 'none';
    document.getElementById("alerts").style.display = 'none';

    var review_text = "";
    if (document.getElementById("order_option").value === "Highest Rating") {
        reviews_array.sort(function (review1, review2) {
            return review2.rating - review1.rating;
        });
    } else if (document.getElementById("order_option").value === "Lowest Rating") {
        reviews_array.sort(function (review1, review2) {
            return review1.rating - review2.rating;
        });
    } else if (document.getElementById("order_option").value === "Most Recent") {
        reviews_array.sort(function (review1, review2) {
            return review2.time - review1.time;
        });
    } else if (document.getElementById("order_option").value === "Least Recent") {
        reviews_array.sort(function (review1, review2) {
            return review1.time - review2.time;
        });
    }
    var reviews_num = reviews_array.length;
    for (var i = 0; i < reviews_num; i++) {
        review_text += "<table id = \'Reviews_table\'" + i + " class = \'reviews_table\'>";
        // first row
        review_text += "<tr>";
        if (typeof (reviews_array[i].profile_photo_url) == 'undefined') {
            review_text += "<td id = \'author_photo\' rowspan = \'3\' valign = \'top\'></td><td id = \'author_name\'>" + "<a href = \'" + reviews_array[i].author_url + "\' target = \'_blank\'>" + reviews_array[i].author_name + "</a></td>";
        } else {
            review_text += "<td id = \'author_photo\' rowspan = \'3\' valign = \'top\'><a href=\'" + reviews_array[i].author_url + "\' target = \'_blank\'><img src = \'" + reviews_array[i].profile_photo_url + "\' class = \'img-circle\' width = \'50px\' height = \'50px\'></a></td><td id = \'author_name\'>" + "<a href = \'" + reviews_array[i].author_url + "\' target = \'_blank\'>" + reviews_array[i].author_name + "</a></td>";
        }
        review_text += "</tr>";

        //second row
        review_text += "<tr>";
        review_text += "<td id = \'author_rating\'>";

        review_text += "<div id = \'reviews_star\'>";

        var review_rating = reviews_array[i].rating;
        for (var j = 0; j < review_rating; j++) {
            review_text += "<i class=\"fas fa-star\"></i>";
        }

        review_text += "</div>";

        // utc_offset: deal with timezone
        var unix_timestamp = reviews_array[i].time + utc_offset * 60;
        //        var time = new Date(unix_timestamp * 1000);
        //        var year = time.getFullYear();
        //        var months = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
        //        var month = months[time.getMonth()];
        //        var date = time.getDate();
        //        var hour = time.getHours();
        //        var minute = time.getMinutes();
        //        var second = time.getSeconds();
        //        var format_time = year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;

        var moment_time = moment(unix_timestamp * 1000).format('YYYY-MM-DD HH:mm:ss');
        //console.log("moment:" + moment_time);

        review_text += "<div id = \'rating_time\'>" + moment_time + "</div>";
        review_text += "</td></tr>";

        // third row
        review_text += "<tr>";
        review_text += "<td id = \'author_text\'>" + reviews_array[i].text + "</td>"
        review_text += "</tr>";

        review_text += "</table>"

    }
    document.getElementById("reviews_table").innerHTML = review_text;

    document.getElementById("details").style.display = "block";
    document.getElementById("reviews").style.display = 'block';
    document.getElementById("reviews_table").style.display = 'block';
}

function request_yelp_match(place_details) {
    var utc_offset = place_details.utc_offset;
    // yelp match request
    var formatted_address = place_details.formatted_address;

    var name = place_details.name;
    var city = formatted_address.substring(formatted_address.indexOf(", ") + 2, formatted_address.indexOf(", ", 1));
    var state;
    var country;
    var location = place_details.geometry.location;
    var latitude;
    var longitude;
    latitude = parseFloat(location.lat());
    longitude = parseFloat(location.lng());
    var postalcode;
    var address1 = place_details.formatted_address.substring(0, place_details.formatted_address.indexOf(", "));
    console.log("address1:" + address1);

    //    console.log("latitude:"+latitude);
    //    console.log("longitude:"+longitude);
    for (var i = 0; i < place_details.address_components.length; i++) {
        if (place_details.address_components[i].types[0] === "locality") {
            //city = place_details.address_components[i].short_name;
        }
        if (place_details.address_components[i].types[0] === "administrative_area_level_1") {
            state = place_details.address_components[i].short_name;
        }
        if (place_details.address_components[i].types[0] === "country") {
            country = place_details.address_components[i].short_name;
        }
        if (place_details.address_components[i].types[0] === "postal_code") {
            postalcode = place_details.address_components[i].short_name;
        }
    }

    var yelp_match_request = {
        name: name,
        address1: address1,
        city: city,
        state: state,
        country: country,
        latitude: latitude,
        longitude: longitude,
        postal_code: postalcode
    };
    console.log("request:" + yelp_match_request);
    console.log("name:" + yelp_match_request.name);
    console.log("city:" + yelp_match_request.city);
    console.log("state:" + yelp_match_request.state);
    console.log("country:" + yelp_match_request.country);

    var xmlRequest = $.ajax({
        url: 'http://newapplication-env.us-east-2.elasticbeanstalk.com/yelp_match',
        type: 'GET',
        data: yelp_match_request,
        dataType: "json",
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("error");
            var text = "<div class = \'alert alert-danger\' role = \'alert\' id = \'alert_danger_yelp_match\'>Failed to get search results.</div>";
            document.getElementById("alerts").innerHTML = text;
            document.getElementById("alerts").style.display = 'block';
            document.getElementById("page_buttons_results").style.display = 'none';
            document.getElementById("page_buttons_fav").style.display = 'none';
            document.getElementById("Details_button").style.display = 'none';
            document.getElementById("details").style.display = 'none';
            document.getElementById("showPlaces").style.display = 'none';
        }
    });

    xmlRequest.done(function (data) {
        console.log("data" + JSON.stringify(data));
        if (data.businesses.length > 0) {
            request_yelp_review(data.businesses[0], utc_offset);
        } else {
            var text = "<div class = \'alert alert-warning\' role = \'alert\' id = \'alert_warning_google_reviews\'>No records.</div>";
            document.getElementById("alerts").innerHTML = text;
            document.getElementById("info").style.display = 'none';
            document.getElementById("photos").style.display = 'none';
            document.getElementById("Map").style.display = 'none';
            document.getElementById("reviews_table").style.display = 'none';
            document.getElementById("yelp_table").style.display = 'none';
            document.getElementById("alerts").style.display = 'block';
            document.getElementById("reviews").style.display = 'block';
        }
    });

}

function request_yelp_review(business, utc_offset) {
    console.log("yelp review get");
    var yelp_review_request = {
        alias: business.alias
    }
    var xmlRequest = $.ajax({
        url: 'http://newapplication-env.us-east-2.elasticbeanstalk.com/yelp_review',
        type: 'GET',
        data: yelp_review_request,
        dataType: "json"
    });

    xmlRequest.done(function (data) {
        console.log("data" + JSON.stringify(data));
        show_yelp_table(data, utc_offset);
    });
}

function show_yelp_table(yelp_reviews, utc_offset) {
    console.log("show yelp reviews");
    document.getElementById("photos").style.display = 'none';
    document.getElementById("Map").style.display = 'none';
    document.getElementById("info").style.display = 'none';
    document.getElementById("reviews_table").style.display = 'none';

    var review_text = "";
    if (document.getElementById("order_option").value === "Highest Rating") {
        yelp_reviews.sort(function (review1, review2) {
            return review2.rating - review1.rating;
        });
    } else if (document.getElementById("order_option").value === "Lowest Rating") {
        yelp_reviews.sort(function (review1, review2) {
            return review1.rating - review2.rating;
        });
    } else if (document.getElementById("order_option").value === "Most Recent") {
        yelp_reviews.sort(function (review1, review2) {
            return moment(review2.time_created, 'YYYY-MM-DD HH:mm:ss').utc() - moment(review1.time_created, 'YYYY-MM-DD HH:mm:ss').utc();
        });
    } else if (document.getElementById("order_option").value === "Least Recent") {
        yelp_reviews.sort(function (review1, review2) {
            return moment(review1.time_created, 'YYYY-MM-DD HH:mm:ss').utc() - moment(review2.time_created, 'YYYY-MM-DD HH:mm:ss').utc();
        });
    }

    var reviews_num = yelp_reviews.length;

    for (var i = 0; i < reviews_num; i++) {
        review_text += "<table id = \'Yelp_table\'" + i + " class = \'yelp_table\'>";
        // first row
        review_text += "<tr>";

        if (typeof (yelp_reviews[i].user.image_url) == 'undefined') {
            review_text += "<td id = \'author_photo\' rowspan = \'3\' valign = \'top\'></td><td id = \'author_name\'>" + "<a href = \'" + yelp_reviews[i].url + "\' target = \'_blank\'>" + yelp_reviews[i].user.name + "</a></td></tr>";
        } else {
            review_text += "<td id = \'author_photo\' rowspan = \'3\' valign = \'top\'><a href=\'" + yelp_reviews[i].url + "\' target=\'_blank\'><img src = \'" + yelp_reviews[i].user.image_url + "\' class = \'img-circle\' width = \'50px\' height = \'50px\'></a></td><td id = \'author_name\'>" + "<a href = \'" + yelp_reviews[i].url + "\' target = \'_blank\'>" + yelp_reviews[i].user.name + "</a></td>";
            review_text += "</tr>";
        }

        //second row
        review_text += "<tr>";
        review_text += "<td id = \'author_rating\'>";

        review_text += "<div id = \'reviews_star\'>";

        var review_rating = yelp_reviews[i].rating;
        for (var j = 0; j < review_rating; j++) {
            review_text += "<i class=\"fas fa-star\"></i>";
        }

        review_text += "</div>";

        var time_created = yelp_reviews[i].time_created;
        var moment_time = moment(moment(time_created, 'YYYY-MM-DD HH:mm:ss').utc() + utc_offset).format('YYYY-MM-DD HH:mm:ss');

        review_text += "<div id = \'rating_time\'>" + moment_time + "</div>";
        review_text += "</td></tr>";

        // third row
        review_text += "<tr>";
        review_text += "<td id = \'author_text\'>" + yelp_reviews[i].text + "</td>"
        review_text += "</tr>";

        review_text += "</table>"

    }
    document.getElementById("yelp_table").innerHTML = review_text;

    document.getElementById("details").style.display = "block";
    document.getElementById("reviews").style.display = 'block';
    document.getElementById("yelp_table").style.display = 'block';
}

function checkInput() {
    if (document.getElementById("current_location").checked == true) {
        setInputLocationDisable();
    }
    if (document.getElementById("other_location").checked == true) {
        setInputLocationRequire();
    }
}

function checkSpace(str) {
    for (var i = 0;
        (str.charAt(i) == ' ') && i < str.length; i++);
    if (i == str.length) {
        return true;
    }
    return false;
}

$(document).ready(function () {
    $('#inputLocation').removeClass("invalid").addClass("valid");
    $('.error').hide();
    $('#other_location').click(function () {
        document.getElementById("search").disabled = true;
        $('#inputLocation').on('input', function () {
            var input = $(this);
            var input_location = input.val().replace(/^ +| +$/g, '');
            if (input_location) {
                input.removeClass("invalid").addClass("valid");
                $('.error').hide();
                $('#search').removeAttr('disabled');
            } else {
                input.removeClass("valid").addClass("invalid");
                $('.error').show();
                $('#search').attr('disabled', 'disabled');
            }
        });
    });
    $('#current_location').click(function () {
        document.getElementById("search").disabled = false;
    });

    $("#open_twitter").click(function () {
        window.open(twitter_content, "Share a link on Twitter", "height=400, width=400, top=100,left=500, toolbar =no, menubar=no, scrollbars=no, resizable=no, location=no, status=no");
    });

    $('#map_from').on('input', function () {
        var input = $(this);
        var from_input = input.val().replace(/^ +| +$/g, '');
        if (from_input) {
            document.getElementById("get_directions").disabled = false;
            document.getElementById("get_directions").style.backgroundColor = "#4379b2";
        } else {
            document.getElementById("get_directions").disabled = true;
            document.getElementById("get_directions").style.backgroundColor = "#83a6cb";
        }
    });

    $('#Results').click(function () {
        document.getElementById("Results").style.backgroundColor = "#2b7af6";
        document.getElementById("Results").style.color = "white";
        document.getElementById("Favorites").style.backgroundColor = "white";
        document.getElementById("Favorites").style.color = "#2b7af6";
    });

    $('#Favorites').click(function () {
        document.getElementById("Favorites").style.backgroundColor = "#2b7af6";
        document.getElementById("Favorites").style.color = "white";
        document.getElementById("Results").style.backgroundColor = "white";
        document.getElementById("Results").style.color = "#2b7af6";
    });
});

function setInputLocationDisable() {
    document.getElementById("inputLocation").disabled = true;
    document.getElementById("inputLocation").required = false;
}

function setInputLocationRequire() {
    document.getElementById("inputLocation").disabled = false;
    document.getElementById("inputLocation").required = true;
}

function clearFunc() {
    console.log("clear");
    document.getElementById("showPlaces").innerHTML = "";
    document.getElementById("page_buttons_results").innerHTML = "";
    document.getElementById("Details_button").style.display = 'none';
    document.getElementById("Details").disabled = true;
    document.getElementById("showPlaces").style.display = 'none';
    document.getElementById("showFavorites").style.display = 'none';
    document.getElementById("page_buttons_results").style.display = 'none';
    document.getElementById("page_buttons_fav").style.display = 'none';
    document.getElementById("details").style.display = 'none';
    document.getElementById("alerts").style.display = 'none';
    previous_place_id = "";
    document.getElementById("results_favorites_button").innerHTML = "<button type = \'button\' id = \'Results\' class = \'btn btn-primary\' align = \'center\' onclick = \'showResults()\'>Results</button><button type = \'button\' id = \'Favorites\' class = \'btn btn-default\' align = \'center\' onclick = \'showFav()\'>Favorites</button>";
    place_data = new Array(3);
    //    document.getElementById("keyword") = "";
    //    document.getElementById("distance").value = "";
}
