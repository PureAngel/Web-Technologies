package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<PlacesReviews> placesReviewsList;
    private static ClickListener clickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView author_name, review_time, review_text;
        public ImageView author_photo;
        public RatingBar author_rating;

        public ViewHolder(View v) {
            super(v);
            author_name = (TextView) v.findViewById(R.id.author_name);
            author_rating = (RatingBar) v.findViewById(R.id.author_rating);
            review_time = (TextView) v.findViewById(R.id.review_time);
            review_text = (TextView) v.findViewById(R.id.review_text);
            author_photo = (ImageView) v.findViewById(R.id.author_photo);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ReviewsAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public ReviewsAdapter(List<PlacesReviews> placesReviewsList) {
        this.placesReviewsList = placesReviewsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_reviews_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlacesReviews placesReviews = placesReviewsList.get(position);
        holder.author_name.setText(placesReviews.getAuthor_name());
        holder.review_text.setText(placesReviews.getText());

        // author photo
        Picasso.get().load(placesReviews.getProfile_photo_url()).into(holder.author_photo);

        // author rating
        holder.author_rating.setRating(placesReviews.getRating());

        // review time
        holder.review_time.setText(placesReviews.getTime());

        final Intent web_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(placesReviews.getAuthor_url()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(web_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesReviewsList.size();
    }
}
