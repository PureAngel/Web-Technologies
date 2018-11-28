package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private EditText keyword_input;
    private EditText distance_input;
    private Spinner category_select;
    private RadioGroup location_select;
    private RadioButton location_choice;
    private EditText location_input;
    private Button search;
    private Button clear;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        location_choice = (RadioButton) view.findViewById(R.id.here);

        location_select = (RadioGroup) view.findViewById(R.id.place_choice);
        location_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                location_choice = (RadioButton) view.findViewById(checkedId);
                if (checkedId == R.id.here) {
                    location_input = view.findViewById(R.id.location);
                    location_input.setEnabled(false);
                    location_input.setError(null);
                } else {
                    view.findViewById(R.id.location).setEnabled(true);
                }
            }
        });

        search = (Button) view.findViewById(R.id.search_button);
        clear = (Button) view.findViewById(R.id.clear_button);

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword_input = (EditText) view.findViewById(R.id.keyword_input);
                String keyword = keyword_input.getText().toString();

                distance_input = (EditText) view.findViewById(R.id.distance);
                String distanceStr = distance_input.getText().toString();
                double distance;
                if(distanceStr.equals("")) {
                    distance = 10 * 1609.344;
                    distanceStr += "10";
                } else {
                    distance = Double.parseDouble(distanceStr) * 1609.344;
                }

                category_select = (Spinner) view.findViewById(R.id.spinner);
                String category = category_select.getSelectedItem().toString();

                location_input = (EditText) view.findViewById(R.id.location);
                String input_location = location_input.getText().toString();

                System.out.println("keyword:"+keyword);
                System.out.println("distance:"+distance);
                System.out.println("category:"+category);

                String location;

                String url = "http://xueying-env.us-east-2.elasticbeanstalk.com/a";

                if(location_choice.getText().equals("Current location")) {
                    location = "current_location";
                    url += "?location=" + location + "&latitude=" + "34.0266" + "&longitude=" + "-118.2831" + "&radius=" + distance + "&type=" + URLEncoder.encode(category) + "&keyword=" + URLEncoder.encode(keyword);
                    System.out.println("url:" + url);
                } else {
                    location = "other_location";
                    url += "?location=" + location + "&input_location=" + URLEncoder.encode(input_location) + "&radius=" + distance + "&type=" + URLEncoder.encode(category) + "&keyword=" + URLEncoder.encode(keyword);
                    System.out.println("url:" + url);
                }

                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", response.toString());
                                // call the new page
                                Intent intent = new Intent();
                                intent.setClass(view.getContext(), SearchResultActivity.class);
                                intent.putExtra("places", response.toString());
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                requestQueue.add(objectRequest);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                keyword_input = (EditText) view.findViewById(R.id.keyword_input);
                keyword_input.setText(null);
                keyword_input.setError(null);
                distance_input = (EditText) view.findViewById(R.id.distance);
                distance_input.setText(null);
                location_input = (EditText) view.findViewById(R.id.location);
                location_input.setText(null);
                location_input.setEnabled(false);
                location_input.setError(null);
                category_select = (Spinner) view.findViewById(R.id.spinner);
                category_select.setSelection(0);
                RadioButton option1 = (RadioButton) view.findViewById(R.id.here);
                option1.setChecked(true);
                RadioButton option2 = (RadioButton) view.findViewById(R.id.specific);
                option2.setChecked(false);
            }
        });

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
