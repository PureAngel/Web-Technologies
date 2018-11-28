package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<PlacesResults> placesResultsList;
    private static ClickListener clickListener;
    private RequestQueue requestQueue;
    private Context context;
    private MyInterface myInterface;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView name, address;
        public ImageView icon_url;
        public ImageView placeHeart;
        private String placeId;
        public PlacesResults placesResults;

        public ViewHolder(View v, final MyInterface myInterface) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            address = (TextView) v.findViewById(R.id.address);
            icon_url = (ImageView) v.findViewById(R.id.icon_url);
            placeHeart = (ImageView)itemView.findViewById(R.id.place_heart);
            placeHeart.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {
                    placesResults.clickHeart(v.getContext(), placeId);
                    myInterface.show_new_page();
                }
            });
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SearchResultAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchResultAdapter(List<PlacesResults> placesResultsList, RequestQueue requestQueue, Context context, MyInterface myInterface) {
        this.placesResultsList = placesResultsList;
        this.requestQueue = requestQueue;
        this.context = context;
        this.myInterface = myInterface;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_result_list, parent, false);

        return new ViewHolder(itemView, myInterface);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final PlacesResults place = placesResultsList.get(position);
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());

        Picasso.get().load(place.getIcon()).into(holder.icon_url);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.contains(holder.placeId)) {
            holder.placeHeart.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_fill_red));
        } else {
            holder.placeHeart.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_outline_black));
        }
        holder.placesResults= place;
        //holder.context = context;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent details_intent = new Intent(v.getContext(), PlaceDetailsActivity.class);
                details_intent.putExtra("place_id", place.getPlace_id());
                Gson gson = new Gson();
                String placeresults = gson.toJson(place);
                details_intent.putExtra("placeResults", placeresults);
                v.getContext().startActivity(details_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesResultsList.size();
    }
}
