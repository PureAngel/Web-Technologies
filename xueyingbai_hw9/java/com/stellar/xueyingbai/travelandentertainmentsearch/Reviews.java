package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Reviews.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Reviews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reviews extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private List<PlacesReviews> placesReviewsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReviewsAdapter reviewsAdapter;

    private OnFragmentInteractionListener mListener;

    public Reviews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reviews.
     */
    // TODO: Rename and change types and number of parameters
    public static Reviews newInstance(String param1, String param2) {
        Reviews fragment = new Reviews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("reviews id:"+this.getId());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
//        JSONObject details_result_json = PlaceDetailsActivity.details_result_json;
//        System.out.println("review json:"+details_result_json);
//
//        JSONArray reviews = new JSONArray();
//        try {
//            reviews = details_result_json.getJSONArray("reviews");
//            System.out.println("reviews:"+reviews.toString());
//
//            for(int i = 0; i < reviews.length(); i++) {
//                placesReviewsList.add(new PlacesReviews());
//                try {
//                    placesReviewsList.get(i).setAuthor_name(reviews.getJSONObject(i).getString("author_name"));
//                    placesReviewsList.get(i).setAuthor_url(reviews.getJSONObject(i).getString("author_url"));
//                    placesReviewsList.get(i).setProfile_photo_url(reviews.getJSONObject(i).getString("profile_photo_url"));
//                    placesReviewsList.get(i).setRating(reviews.getJSONObject(i).getInt("rating"));
//                    placesReviewsList.get(i).setText(reviews.getJSONObject(i).getString("text"));
//                    placesReviewsList.get(i).setTime(reviews.getJSONObject(i).getString("time"));
//                } catch (JSONException e) {
//
//                }
//            }
//        } catch (JSONException e) {
//
//        }
//
//        recyclerView = (RecyclerView) view.findViewById(R.id.reviews_table);
//        System.out.println("recyclerView:"+recyclerView);
//
//        reviewsAdapter = new ReviewsAdapter(placesReviewsList);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(reviewsAdapter);
//
//
//        Spinner reviews_type_spinner = (Spinner) view.findViewById(R.id.reviews_type);
//        String reviews_type = reviews_type_spinner.getSelectedItem().toString();
//
//        Spinner reviews_order_spinner = (Spinner) view.findViewById(R.id.reviews_order);
//        String reviews_order = reviews_order_spinner.getSelectedItem().toString();
//
//        //JSONArray reviews = new JSONArray();
//
//        if(reviews_type.equals("Google reviews")) {
//            try {
//                reviews = details_result_json.getJSONArray("reviews");
//                System.out.println("reviews:" + reviews.toString());
//            } catch (JSONException e) {
//
//            }
//
//        } else {
//
//        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
