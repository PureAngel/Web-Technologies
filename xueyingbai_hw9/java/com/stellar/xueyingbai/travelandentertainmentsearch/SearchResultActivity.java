package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements MyInterface {
    private RecyclerView search_results_recyclerView;
    private RecyclerView.Adapter search_results_adapter;
    private RecyclerView.LayoutManager search_results_layoutManager;
    private List<PlacesResults> placesResultsList;
    private JSONObject places_json = new JSONObject();
    private JSONArray places_results = new JSONArray();
    private List<JSONObject> places_json_list = new ArrayList<>();
    private SearchResultAdapter adapter;
    private int page = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Toolbar toolbar = (Toolbar) findViewById(R.id.result_toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String places = intent.getStringExtra("places");
        System.out.println("places:" + places);
        try {
            places_json = new JSONObject(places);
            System.out.println("places_json:" + places_json.toString());
            places_results = places_json.getJSONArray("results");
            System.out.println("place_results:"+places_results.toString());

            showPage();
        } catch (JSONException e) {

        }
    }

    protected void showPage() {
        if(!places_json_list.contains(places_json)) {
            places_json_list.add(places_json);
        }
        JSONArray places_results = new JSONArray();
        try {
            places_results = places_json.getJSONArray("results");
        } catch (JSONException e) {

        }
        placesResultsList = new ArrayList<PlacesResults>();
        for(int i = 0; i < places_results.length(); i++) {
            placesResultsList.add(new PlacesResults());
            try {
                //System.out.println(places_results.getJSONObject(i).getString("name"));
                placesResultsList.get(i).setName(places_results.getJSONObject(i).getString("name"));
                System.out.println("name"+i+":"+ placesResultsList.get(i).getName());
                placesResultsList.get(i).setAddress(places_results.getJSONObject(i).getString("vicinity"));
                System.out.println("address"+i+":"+ placesResultsList.get(i).getAddress());
                placesResultsList.get(i).setIcon_url(places_results.getJSONObject(i).getString("icon"));
                System.out.println("icon"+i+":"+ placesResultsList.get(i).getIcon());
                placesResultsList.get(i).setPlace_id(places_results.getJSONObject(i).getString("place_id"));
            } catch (JSONException e) {

            }

            // next button
            Button next_button = (Button) findViewById(R.id.next_button);
            if(places_json.isNull("next_page_token")) {
                System.out.println("no token");
                next_button.setEnabled(false);
            } else {
                next_button.setEnabled(true);
                next_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page++;
                        getNextPage();
                    }
                });
            }

            // previous page
            Button previous_button = (Button) findViewById(R.id.previous_button);
            if(page == 0) {
                previous_button.setEnabled(false);
            } else {
                previous_button.setEnabled(true);
                previous_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page--;
                        getPreviousPage();
                    }
                });
            }

        }

        search_results_recyclerView = (RecyclerView) findViewById(R.id.search_results_table);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        search_results_recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        search_results_layoutManager = new LinearLayoutManager(this);
        search_results_recyclerView.setLayoutManager(search_results_layoutManager);

        // specify an adapter (see also next example)

        adapter = new SearchResultAdapter(placesResultsList, Volley.newRequestQueue(getApplicationContext()), SearchResultActivity.this, this);
        search_results_recyclerView.setAdapter(adapter);

        //search_results_adapter = (SearchResultAdapter) new SearchResultAdapter(placesResultsList);
        //search_results_recyclerView.setAdapter(search_results_adapter);
    }

    protected void getNextPage() {
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            String next_page_token = places_json.getString("next_page_token");
            System.out.println("next_page_token:" + next_page_token);
            String url = "http://xueying-env.us-east-2.elasticbeanstalk.com/next?next_page_token=" + URLEncoder.encode(next_page_token);
            System.out.println("url:"+url);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", response.toString());
                            places_json = response;
                            JSONArray places_results = new JSONArray();
                            try {
                                System.out.println("places_json:" + places_json.toString());
                                places_results = places_json.getJSONArray("results");
                                System.out.println("place_results:"+places_results.toString());

                                showPage();
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
    }

    protected void getPreviousPage() {
        places_json = places_json_list.get(page);
        showPage();
    }

    @Override
    public void show_new_page() {
        showPage();
    }
}
