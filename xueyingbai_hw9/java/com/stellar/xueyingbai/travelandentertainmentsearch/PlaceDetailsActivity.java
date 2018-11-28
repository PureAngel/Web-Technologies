package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PlaceDetailsActivity extends AppCompatActivity implements
        Info.OnFragmentInteractionListener, Photos.OnFragmentInteractionListener,
        Map.OnFragmentInteractionListener, Reviews.OnFragmentInteractionListener {
    public static JSONObject details_result_json = new JSONObject();
    public static String address = "";
    public static String place_id = "";
    private List<PlacesReviews> placesReviewsList = new ArrayList<>();
    private List<PlacesReviews> yelpReviewsList = new ArrayList<>();
    private List<PlacesReviews> reviewsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReviewsAdapter reviewsAdapter;
    TextView address_textview;
    TextView phone_number_textview;
    TextView price_level_textview;
    RatingBar ratingBar;
    TextView google_page_textview;
    TextView website_textview;
    public static String yelp_review_url = "";
    public static JSONObject business = new JSONObject();
    public static JSONObject business_review_json = new JSONObject();
    public static JSONArray yelp_reviews_json = new JSONArray();

    String twitter_url = "https://twitter.com/intent/tweet";
    String details_website = "";

    public static double latitude;
    public static double longitude;

    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.review
    };
    TabLayout tabLayout;

    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosList = new ArrayList<>();;
    public ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    public ImageView[] myImageViews;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_details);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        tabLayout = (TabLayout) findViewById(R.id.details_tablayout);

        tabLayout.addTab(tabLayout.newTab().setText("INFO"));
        tabLayout.addTab(tabLayout.newTab().setText("PHOTOS"));
        tabLayout.addTab(tabLayout.newTab().setText("MAP"));
        tabLayout.addTab(tabLayout.newTab().setText("REVIEWS"));
        setupTabIcons();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager details_pager = (ViewPager) findViewById(R.id.details_pager);
        details_pager.setOffscreenPageLimit(4);
        final PagerAdapter details_pageradapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        details_pager.setAdapter(details_pageradapter);
        details_pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //System.out.println("detail json："+ details_result_json);
                //System.out.println("position:"+tab.getPosition());
                details_pager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Intent intent = getIntent();
        place_id = intent.getStringExtra("place_id");
        String url = "http://xueying-env.us-east-2.elasticbeanstalk.com/details?place_id=" + place_id;
        System.out.println("url:" + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("details"+response);
                        try {
                            details_result_json = response.getJSONObject("result");
                            System.out.println("details result:"+details_result_json);

                            TextView place_name_textview = (TextView) findViewById(R.id.place_name);
                            place_name_textview.setText(details_result_json.getString("name"));
                            address = details_result_json.getString("formatted_address");
                            address_textview = (TextView) findViewById(R.id.address_text);
                            System.out.println("address_textview:"+address_textview);
                            phone_number_textview = (TextView) findViewById(R.id.phone_number_text);
                            System.out.println("phone_number_textview:"+phone_number_textview);
                            price_level_textview = (TextView) findViewById(R.id.price_level_text);
                            System.out.println("price_level_textview:"+price_level_textview);
                            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                            System.out.println("ratingBar:"+ratingBar);
                            google_page_textview = (TextView) findViewById(R.id.google_page_text);
                            System.out.println("google_page_textview:"+google_page_textview);
                            website_textview = (TextView) findViewById(R.id.website_text);
                            System.out.println("website_textview:"+website_textview);

                            JSONObject detailobj = response.getJSONObject("result");
                            JSONObject geometry = detailobj.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            String lat = location.getString("lat");
                            String lng = location.getString("lng");
                            latitude = Double.parseDouble(lat);
                            longitude = Double.parseDouble(lng);

                            setInfoView();
                            setPhotoView();
                            setMapView();
                            setReviewsView();
                        } catch (JSONException e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(objectRequest);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
//        tabLayout.getTabAt(0).setCustomView(getTabView(0));
//        tabLayout.getTabAt(1).setCustomView(getTabView(1));
//        tabLayout.getTabAt(2).setCustomView(getTabView(2));
//        tabLayout.getTabAt(3).setCustomView(getTabView(3));
    }

//    public View getTabView(int position) {
//        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
//        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
//        img_title.setImageResource(tabIcons[position]);
//        return view;
//    }

    public JSONObject getDetails_result_json() {
        return details_result_json;
    }

    protected void setInfoView() {

        try {
            String name = details_result_json.getString("name");
            String address = details_result_json.getString("formatted_address");
            String phone_number = details_result_json.getString("formatted_phone_number");
            int price_level = details_result_json.getInt("price_level");
            double rating = details_result_json.getDouble("rating");
            System.out.println("rating:"+rating);
            String google_page = details_result_json.getString("url");
            String website = details_result_json.getString("website");


            //TextView address_textview1 = (TextView) findViewById(R.id.address_text);
            address_textview.setText(address);
            phone_number_textview.setText(phone_number);
            google_page_textview.setText(google_page);
            website_textview.setText(website);

            // draw dollars (price level）
            String dollars = "";
            for(int i = 0; i < price_level; i++) {
                dollars += "$";
            }
            price_level_textview.setText(dollars);

            // rating stars
            ratingBar.setRating((float)rating);

            details_website = details_result_json.getString("website");
            twitter_url += "?text=Check%20out%20" + URLEncoder.encode(name)
                    + "%20located%20at%20" + URLEncoder.encode(address)
                    + ".%0aWebsite:%20" + URLEncoder.encode(details_website);
            ImageView shareButton = (ImageView) findViewById(R.id.share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent twitter_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
                    startActivity(twitter_intent);
                }
            });
        } catch (JSONException e) {

        }
    }

    protected void setPhotoView() {
        ImageView imageView0 = (ImageView) findViewById(R.id.image0);
        ImageView imageView1 = (ImageView) findViewById(R.id.image1);
        ImageView imageView2 = (ImageView) findViewById(R.id.image2);
        ImageView imageView3 = (ImageView) findViewById(R.id.image3);
        ImageView imageView4 = (ImageView) findViewById(R.id.image4);
        ImageView imageView5 = (ImageView) findViewById(R.id.image5);
        ImageView imageView6 = (ImageView) findViewById(R.id.image6);
        ImageView imageView7 = (ImageView) findViewById(R.id.image7);
        ImageView imageView8 = (ImageView) findViewById(R.id.image8);
        ImageView imageView9 = (ImageView) findViewById(R.id.image9);
        myImageViews = new ImageView[]{imageView0,imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9};
        geoDataClient = Places.getGeoDataClient(this, null);
        final Task<PlacePhotoMetadataResponse> photoResponse = geoDataClient.getPlacePhotos(place_id);
        photoResponse.addOnCompleteListener
                (new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        PlacePhotoMetadataResponse photos = task.getResult();
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if(photoMetadataBuffer.getCount() > 0){
                            for(int i = 0; i < photoMetadataBuffer.getCount(); i++){
                                photosList.add(photoMetadataBuffer.get(i).freeze());
                                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                                getPhoto(photoMetadata,i);
                            }
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }

    protected void setMapView() {

    }

    protected void setReviewsView() {

        final Spinner reviews_type_spinner = (Spinner) findViewById(R.id.reviews_type);
        final Spinner reviews_order_spinner = (Spinner) findViewById(R.id.reviews_order);

        showGoogleReviews();

        // spinner order
        reviews_order_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("position："+position);
                        String reviews_type = reviews_type_spinner.getSelectedItem().toString();
                        if(reviews_type.equals("Google reviews")) {
                            reviewsList = placesReviewsList;
                        } else {
                            reviewsList = yelpReviewsList;
                        }
                        if(position == 1) {
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    if(placesReviews1.getRating() > placesReviews2.getRating()) {
                                        return -1;
                                    } else if(placesReviews1.getRating() < placesReviews2.getRating()) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else if(position == 2) {
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    if(placesReviews1.getRating() < placesReviews2.getRating()) {
                                        return -1;
                                    } else if(placesReviews1.getRating() > placesReviews2.getRating()) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else if(position == 3) {
                            //System.out.println("select:"+reviews_type_spinner.getSelectedItem().toString());
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String time1 = placesReviews1.getTime();
                                    String time2 = placesReviews2.getTime();
                                    Long unix_time1 = 0L;
                                    Long unix_time2 = 0L;

                                    try {
                                        unix_time1 = simpleDateFormat.parse(time1).getTime();
                                        unix_time2 = simpleDateFormat.parse(time2).getTime();
                                    } catch (ParseException e) {

                                    }

                                    if(unix_time1 > unix_time2) {
                                        return -1;
                                    } else if (unix_time1 < unix_time2) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else if(position == 4) {
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String time1 = placesReviews1.getTime();
                                    String time2 = placesReviews2.getTime();
                                    Long unix_time1 = 0L;
                                    Long unix_time2 = 0L;

                                    try {
                                        unix_time1 = simpleDateFormat.parse(time1).getTime();
                                        unix_time2 = simpleDateFormat.parse(time2).getTime();
                                    } catch (ParseException e) {

                                    }

                                    if(unix_time1 < unix_time2) {
                                        return -1;
                                    } else if (unix_time1 > unix_time2) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else {
                            if(reviews_type_spinner.getSelectedItem().toString().equals("Google reviews")) {
                                try {
                                    JSONArray myReviews = details_result_json.getJSONArray("reviews");
                                    for(int i = 0; i < myReviews.length(); i++) {
                                        //placesReviewsList.add(new PlacesReviews());
                                        try {
                                            placesReviewsList.get(i).setAuthor_name(myReviews.getJSONObject(i).getString("author_name"));
                                            placesReviewsList.get(i).setAuthor_url(myReviews.getJSONObject(i).getString("author_url"));
                                            placesReviewsList.get(i).setProfile_photo_url(myReviews.getJSONObject(i).getString("profile_photo_url"));
                                            placesReviewsList.get(i).setRating(myReviews.getJSONObject(i).getInt("rating"));
                                            placesReviewsList.get(i).setText(myReviews.getJSONObject(i).getString("text"));

                                            // review time
                                            String unix_time = myReviews.getJSONObject(i).getString("time");
                                            int utc_offset = details_result_json.getInt("utc_offset");
                                            long unix_seconds = Long.parseLong(unix_time) - utc_offset * 60;
                                            Date date = new Date(unix_seconds * 1000L);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            String review_time = dateFormat.format(date);
                                            placesReviewsList.get(i).setTime(review_time);
                                        } catch (JSONException e) {

                                        }
                                    }
                                } catch (JSONException e) {

                                }
                            } else {
                                JSONArray myReviews = yelp_reviews_json;
                                yelpReviewsList.clear();
                                for (int i = 0; i < yelp_reviews_json.length(); i++) {
                                    yelpReviewsList.add(new PlacesReviews());
                                    try {
                                        yelpReviewsList.get(i).setAuthor_name(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("name"));
                                        yelpReviewsList.get(i).setAuthor_url(yelp_reviews_json.getJSONObject(i).getString("url"));
                                        yelpReviewsList.get(i).setProfile_photo_url(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("image_url"));
                                        yelpReviewsList.get(i).setRating(yelp_reviews_json.getJSONObject(i).getInt("rating"));
                                        yelpReviewsList.get(i).setText(yelp_reviews_json.getJSONObject(i).getString("text"));
                                        yelpReviewsList.get(i).setTime(yelp_reviews_json.getJSONObject(i).getString("time_created"));

                                        recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
                                        System.out.println("recyclerView:" + recyclerView);

                                        reviewsAdapter = new ReviewsAdapter(yelpReviewsList);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(reviewsAdapter);
                                    } catch (JSONException e) {

                                    }
                                }
                            }
                        }
                        recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
                        System.out.println("recyclerView:"+recyclerView);

                        reviewsAdapter = new ReviewsAdapter(reviewsList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(reviewsAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        // spinner type
        reviews_type_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0) { // google reviews
                            reviewsList = placesReviewsList;
                            //showGoogleReviews();
                        } else { // yelp reviews
                            if(yelpReviewsList.size() == 0) {
                                getYelpMatch();
                                reviewsList = yelpReviewsList;
                            } else {
                                reviewsList = yelpReviewsList;
                            }
                        }

                        if (reviews_order_spinner.getSelectedItem().toString().equals("Highest rating")) {
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    if(placesReviews1.getRating() > placesReviews2.getRating()) {
                                        return -1;
                                    } else if(placesReviews1.getRating() < placesReviews2.getRating()) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else if (reviews_order_spinner.getSelectedItem().toString().equals("Lowest rating")) {
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    if(placesReviews1.getRating() < placesReviews2.getRating()) {
                                        return -1;
                                    } else if(placesReviews1.getRating() > placesReviews2.getRating()) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else if (reviews_order_spinner.getSelectedItem().toString().equals("Most recent")) {
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String time1 = placesReviews1.getTime();
                                    String time2 = placesReviews2.getTime();
                                    Long unix_time1 = 0L;
                                    Long unix_time2 = 0L;

                                    try {
                                        unix_time1 = simpleDateFormat.parse(time1).getTime();
                                        unix_time2 = simpleDateFormat.parse(time2).getTime();
                                    } catch (ParseException e) {

                                    }

                                    if(unix_time1 > unix_time2) {
                                        return -1;
                                    } else if (unix_time1 < unix_time2) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        } else if(reviews_order_spinner.getSelectedItem().toString().equals("Least recent")){
                            Collections.sort(reviewsList, new Comparator<PlacesReviews>() {
                                @Override
                                public int compare(PlacesReviews placesReviews1, PlacesReviews placesReviews2) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String time1 = placesReviews1.getTime();
                                    String time2 = placesReviews2.getTime();
                                    Long unix_time1 = 0L;
                                    Long unix_time2 = 0L;

                                    try {
                                        unix_time1 = simpleDateFormat.parse(time1).getTime();
                                        unix_time2 = simpleDateFormat.parse(time2).getTime();
                                    } catch (ParseException e) {

                                    }

                                    if(unix_time1 < unix_time2) {
                                        return -1;
                                    } else if (unix_time1 > unix_time2) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        }

                        recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
                        System.out.println("recyclerView:"+recyclerView);

                        reviewsAdapter = new ReviewsAdapter(reviewsList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(reviewsAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    private void showGoogleReviews() {
        JSONArray reviews = new JSONArray();
        try {
            reviews = details_result_json.getJSONArray("reviews");
            System.out.println("reviews:"+reviews.toString());
            placesReviewsList.clear();

            if(reviews.length() == 0) {
                showNoReviews();
            } else {
                showReviews();
            }

            for(int i = 0; i < reviews.length(); i++) {
                placesReviewsList.add(new PlacesReviews());
                try {
                    placesReviewsList.get(i).setAuthor_name(reviews.getJSONObject(i).getString("author_name"));
                    placesReviewsList.get(i).setAuthor_url(reviews.getJSONObject(i).getString("author_url"));
                    placesReviewsList.get(i).setProfile_photo_url(reviews.getJSONObject(i).getString("profile_photo_url"));
                    placesReviewsList.get(i).setRating(reviews.getJSONObject(i).getInt("rating"));
                    placesReviewsList.get(i).setText(reviews.getJSONObject(i).getString("text"));

                    // review time
                    String unix_time = reviews.getJSONObject(i).getString("time");
                    int utc_offset = details_result_json.getInt("utc_offset");
                    long unix_seconds = Long.parseLong(unix_time) - utc_offset * 60;
                    Date date = new Date(unix_seconds * 1000L);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String review_time = dateFormat.format(date);
                    placesReviewsList.get(i).setTime(review_time);
                } catch (JSONException e) {

                }
            }
        } catch (JSONException e) {

        }

        recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
        System.out.println("recyclerView:"+recyclerView);

        reviewsAdapter = new ReviewsAdapter(placesReviewsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reviewsAdapter);
    }

    private void getYelpMatch() {
        //business = new JSONObject();
        String name = "";
        String address1 = "";
        String city = "";
        String state = "";
        String country = "";
        double latitude;
        double longitude;
        String postal_code = "";
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            // name
            name = details_result_json.getString("name");

            // address1
            String formatted_address = details_result_json.getString("formatted_address");
            address1 = formatted_address;

            // city
            //city = formatted_address.substring(formatted_address.indexOf(", ") + 2, formatted_address.indexOf(", ", 1));

            for(int i = 0; i < details_result_json.getJSONArray("address_components").length(); i++) {
                String type = details_result_json.getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0);
                if(type.equals("administrative_area_level_1")) {
                    state = details_result_json.getJSONArray("address_components").getJSONObject(i).getString("short_name");
                } else if(type.equals("country")) {
                    country = details_result_json.getJSONArray("address_components").getJSONObject(i).getString("short_name");
                } else if(type.equals("postal_code")) {
                    postal_code = details_result_json.getJSONArray("address_components").getJSONObject(i).getString("short_name");
                } else if(type.equals("locality")) {
                    city = details_result_json.getJSONArray("address_components").getJSONObject(i).getString("short_name");
                }
            }

            latitude = details_result_json.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            longitude = details_result_json.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            String url = "http://xueying-env.us-east-2.elasticbeanstalk.com/yelp_match";
            url += "?name=" + URLEncoder.encode(name);
            url += "&city=" + URLEncoder.encode(address1);
            //url += "&city=" + URLEncoder.encode(city);
            url += "&state=" + URLEncoder.encode(state);
            url += "&country=" + URLEncoder.encode(country);
            url += "&latitude=" + latitude;
            url += "&longitude=" + longitude;
            url += "&postal_code=" + URLEncoder.encode(postal_code);

            System.out.println("yelp match url:"+url);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("yelp match response:"+response.toString());
                            try {
                                business = response.getJSONArray("businesses").getJSONObject(0);
                                System.out.println("business:"+business.toString());

                                getYelpReviews();
                            } catch (JSONException e) {

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            requestQueue.add(objectRequest);
        } catch (JSONException e) {

        }
        if(business.length() == 0) {
            showNoReviews();
        }
    }

    private void getYelpReviews() {
        //yelp_reviews_json = new JSONArray();
        final RequestQueue review_requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            String alias = business.getString("alias");
            yelp_review_url = "http://xueying-env.us-east-2.elasticbeanstalk.com/yelp_review";
            yelp_review_url += "?alias=" + URLEncoder.encode(alias);

            System.out.println("yelp review url:"+yelp_review_url);

            JsonArrayRequest yelp_review_request = new JsonArrayRequest(Request.Method.GET, yelp_review_url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            System.out.println("response:"+response.toString());
                            yelp_reviews_json = response;
                            if(yelp_reviews_json.length() == 0) {
                                showNoReviews();
                            } else {
                                showReviews();
                                showYelpReviews();
                            }
//                            yelpReviewsList.clear();
//                            for(int i = 0; i < yelp_reviews_json.length(); i++) {
//                                yelpReviewsList.add(new PlacesReviews());
//                                try {
//                                    yelpReviewsList.get(i).setAuthor_name(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("name"));
//                                    yelpReviewsList.get(i).setAuthor_url(yelp_reviews_json.getJSONObject(i).getString("url"));
//                                    yelpReviewsList.get(i).setProfile_photo_url(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("image_url"));
//                                    yelpReviewsList.get(i).setRating(yelp_reviews_json.getJSONObject(i).getInt("rating"));
//                                    yelpReviewsList.get(i).setText(yelp_reviews_json.getJSONObject(i).getString("text"));
//                                    yelpReviewsList.get(i).setTime(yelp_reviews_json.getJSONObject(i).getString("time_created"));
//
//                                    recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
//                                    System.out.println("recyclerView:"+recyclerView);
//
//                                    reviewsAdapter = new ReviewsAdapter(yelpReviewsList);
//                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                                    recyclerView.setLayoutManager(mLayoutManager);
//                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                                    recyclerView.setAdapter(reviewsAdapter);
//                                } catch (JSONException e) {
//
//                                }
//                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("error:"+error);
                            showNoReviews();
                        }
                    }
            );
            review_requestQueue.add(yelp_review_request);
        } catch (JSONException e) {
            showNoReviews();
        }
        if(yelp_reviews_json.length() == 0) {
            showNoReviews();
        }
    }

    private void showYelpReviews() {
        yelpReviewsList.clear();
        for(int i = 0; i < yelp_reviews_json.length(); i++) {
            yelpReviewsList.add(new PlacesReviews());
            try {
                yelpReviewsList.get(i).setAuthor_name(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("name"));
                yelpReviewsList.get(i).setAuthor_url(yelp_reviews_json.getJSONObject(i).getString("url"));
                yelpReviewsList.get(i).setProfile_photo_url(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("image_url"));
                yelpReviewsList.get(i).setRating(yelp_reviews_json.getJSONObject(i).getInt("rating"));
                yelpReviewsList.get(i).setText(yelp_reviews_json.getJSONObject(i).getString("text"));
                yelpReviewsList.get(i).setTime(yelp_reviews_json.getJSONObject(i).getString("time_created"));

                recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
                System.out.println("recyclerView:"+recyclerView);

                reviewsAdapter = new ReviewsAdapter(yelpReviewsList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(reviewsAdapter);
            } catch (JSONException e) {

            }
        }
//        yelpReviewsList.clear();
//        for(int i = 0; i < yelp_reviews_json.length(); i++) {
//            yelpReviewsList.add(new PlacesReviews());
//            try {
//                yelpReviewsList.get(i).setAuthor_name(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("name"));
//                yelpReviewsList.get(i).setAuthor_url(yelp_reviews_json.getJSONObject(i).getString("url"));
//                yelpReviewsList.get(i).setProfile_photo_url(yelp_reviews_json.getJSONObject(i).getJSONObject("user").getString("image_url"));
//                yelpReviewsList.get(i).setRating(yelp_reviews_json.getJSONObject(i).getInt("rating"));
//                yelpReviewsList.get(i).setText(yelp_reviews_json.getJSONObject(i).getString("text"));
//                yelpReviewsList.get(i).setTime(yelp_reviews_json.getJSONObject(i).getString("time_created"));
//
//                recyclerView = (RecyclerView) findViewById(R.id.reviews_table);
//                System.out.println("recyclerView:"+recyclerView);
//
//                reviewsAdapter = new ReviewsAdapter(yelpReviewsList);
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                recyclerView.setLayoutManager(mLayoutManager);
//                recyclerView.setItemAnimator(new DefaultItemAnimator());
//                recyclerView.setAdapter(reviewsAdapter);
//            } catch (JSONException e) {
//
//            }
//        }
    }

    private void showNoReviews() {
        findViewById(R.id.reviews_table).setVisibility(View.GONE);
        findViewById(R.id.no_reviews).setVisibility(View.VISIBLE);
    }

    private void showReviews() {
        findViewById(R.id.reviews_table).setVisibility(View.VISIBLE);
        findViewById(R.id.no_reviews).setVisibility(View.GONE);
    }

    // Request photos and metadata for the specified place.
    private void getPhoto(PlacePhotoMetadata photoMetadata, final int i){
        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                myImageViews[i].invalidate();
                myImageViews[i].setImageBitmap(photoBitmap);
            }
        });
    }
}
