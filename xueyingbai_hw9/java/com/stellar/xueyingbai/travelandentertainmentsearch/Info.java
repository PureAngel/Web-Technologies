package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Info.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Info extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Info.
     */
    // TODO: Rename and change types and number of parameters
    public static Info newInstance(String param1, String param2) {
        Info fragment = new Info();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("info id:"+ this.getId());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
//        PlaceDetailsActivity placeDetailsActivity = new PlaceDetailsActivity();
//        //JSONObject details_result_json = placeDetailsActivity.getDetails_result_json();
//
//        TextView address_textview = (TextView) view.findViewById(R.id.address_text);
//        System.out.println("address_textview:"+address_textview);
//        TextView phone_number_textview = (TextView) view.findViewById(R.id.phone_number_text);
//        System.out.println("phone_number_textview:"+phone_number_textview);
//        TextView price_level_textview = (TextView) view.findViewById(R.id.price_level_text);
//        System.out.println("price_level_textview:"+price_level_textview);
//        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
//        System.out.println("ratingBar:"+ratingBar);
//        TextView google_page_textview = (TextView) view.findViewById(R.id.google_page_text);
//        System.out.println("google_page_textview:"+google_page_textview);
//        TextView website_textview = (TextView) view.findViewById(R.id.website_text);
//        System.out.println("website_textview:"+website_textview);
//
//        try {
//            System.out.println("enter try");
//            System.out.println("enter details json:"+PlaceDetailsActivity.details_result_json.toString());
//            System.out.println("get address:"+PlaceDetailsActivity.address);
//            String name = PlaceDetailsActivity.details_result_json.getString("name");
//            System.out.println("get name:"+name);
//            String address = PlaceDetailsActivity.details_result_json.getString("formatted_address");
//            System.out.println("get address:"+address);
//            String phone_number = PlaceDetailsActivity.details_result_json.getString("formatted_phone_number");
//            System.out.println("get phone number:"+phone_number);
//            int price_level = PlaceDetailsActivity.details_result_json.getInt("price_level");
//            double rating = PlaceDetailsActivity.details_result_json.getDouble("rating");
//            System.out.println("rating:"+rating);
//            String google_page = PlaceDetailsActivity.details_result_json.getString("url");
//            String website = PlaceDetailsActivity.details_result_json.getString("website");
//
//
//            //TextView address_textview1 = (TextView) view.findViewById(R.id.address_text);
//            address_textview.setText(address);
//            System.out.println("address_textview get:"+address_textview.getText().toString());
//            phone_number_textview.setText(phone_number);
//            System.out.println("phone number textview get:"+phone_number_textview.getText().toString());
//            google_page_textview.setText(google_page);
//            System.out.println("google page textview get:"+ google_page_textview.getText().toString());
//            website_textview.setText(website);
//            System.out.println("website textview get:"+website_textview.getText().toString());
//
//            // draw dollars (price level）
//            String dollars = "";
//            for(int i = 0; i < price_level; i++) {
//                dollars += "$";
//            }
//            price_level_textview.setText(dollars);
//
//            // rating stars
//            ratingBar.setRating((float)rating);
//        } catch (JSONException e) {
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
