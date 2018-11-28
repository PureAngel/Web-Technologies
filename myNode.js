var cors = require('cors')

var http = require('http');

var express = require('express');
var app = express();
app.use(cors());
var key = "AIzaSyC6A-agVKEwgIed6VeKEYHRIblKM6sJRjg";
var request = require('request');

//http.createServer(function (req, res) {
//    res.writeHead(200, {'contentType':'text-plain'});
//    res.end('Hello World!'); //write a response to the client
//    //res.end(); //end the response
//}).listen(8081);

app.get('/a', function (req, res) {
    var keyword = req.query.keyword;
    console.log("keyword2:" + keyword);
    var category = req.query.category;
    console.log("category:" + category);
    var distance = req.query.distance;
    console.log("distance:" + distance);
    var location = req.query.location;
    console.log("location:" + location);
    var latitude;
    var longitude;
    if (distance == "" || distance == undefined) {
        distance = 10 * 1609.344;
    } else {
        distance *= 1609.344;
    }

    var url;

    if (location == "current_location") {
        latitude = req.query.latitude;
        longitude = req.query.longitude;
        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=" + category + "&keyword=" + keyword + "&key=" + key;
        request(url, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                var placeData = body;
                res.send(placeData);
            }
        });
    } else {
        var input_location = req.query.input_location;
        var latlon_request_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + input_location + "&key=" + key;
        request(latlon_request_url, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                var getLocationResponse = body;
                console.log(getLocationResponse);
                getLocationResponse = JSON.parse(getLocationResponse);
                console.log(getLocationResponse.results);
                latitude = getLocationResponse.results[0].geometry.location.lat;
                longitude = getLocationResponse.results[0].geometry.location.lng;
                url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=" + category + "&keyword=" + keyword + "&key=" + key;
                request(url, function (error, response, body) {
                    if (!error && response.statusCode == 200) {
                        var placeData = body;
                        res.send(placeData);
                    }
                });
            }
        });
    }

});

app.get('/next', function (req, res) {
    console.log("next page");
    var next_page_token = req.query.next_page_token;
    console.log(next_page_token);
    var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + next_page_token + "&key=" + key;
    console.log(url);
    request(url, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var placeData = body;
            res.send(placeData);
        }
    });
});

app.get('/yelp_match', function (req, res) {
    var yelp_key = "zAe842igRp0dEfH_VNB3CgLkN9K5CcBft5JQ4u-TFAgioK4_f-2WrGUQVNKbs9zSTGeLYQ4pUZ0drWbUu_KFLpxZ7RmqL3vMVh9Y7B8bREkdP-mkbK2gvbFOopvHWnYx";
    console.log("req:" + req.query);
    console.log("name:" + req.query.name);
    console.log("city:" + req.query.city);
    console.log("state:" + req.query.state);
    console.log("country:" + req.query.country);
    console.log("postal_code:" + req.query.postal_code);
    console.log("latitude:" + req.query.latitude);
    console.log("longitude:" + req.query.longitude);
    'use strict';
    const yelp = require('yelp-fusion');
    //    var yelp_key = "vDnpH_JzFHf6ap6isky154ydSZNH3jnowjdxZwKNE7v8POAluWj9C_rpoFKBHVBFgxYOvW567MGkXq60Kpm1otyqoZmm_fFjpAz3nIJySdNeYa5xAbKz3a8V6Oe7WnYx";

    const client = yelp.client(yelp_key);

    // matchType can be 'lookup' or 'best'
    client.businessMatch('lookup', {
        name: req.query.name,
        city: req.query.city,
        state: req.query.state,
        country: req.query.country,
        latitude: req.query.latitude,
        longitude: req.query.longitude,
        postal_code: req.query.postal_code
    }).then(response => {
        console.log("response:" + JSON.stringify(response.jsonBody));
        res.send(response.jsonBody);
    }).catch(e => {
        console.log(e);
    });
});

app.get('/yelp_review', function (req, res) {
    var yelp_key = "zAe842igRp0dEfH_VNB3CgLkN9K5CcBft5JQ4u-TFAgioK4_f-2WrGUQVNKbs9zSTGeLYQ4pUZ0drWbUu_KFLpxZ7RmqL3vMVh9Y7B8bREkdP-mkbK2gvbFOopvHWnYx";
    'use strict';

    const yelp = require('yelp-fusion');

    const client = yelp.client(yelp_key);

    client.reviews(req.query.alias).then(response => {
        console.log(response.jsonBody.reviews);
        res.send(response.jsonBody.reviews);
    }).catch(e => {
        console.log(e);
    });
});


app.listen(8081);
