package com.example.mxx62.findit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WriteReviewFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WriteReviewFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriteReviewFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText reviewText;
    private RatingBar rateBar;
    private Button subbtn;

    private DatabaseReference reRef;
    private FirebaseAuth reAuth;
    FrameLayout temp, temp2;

    public WriteReviewFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @param param2 Parameter 2.
     * @return A new instance of fragment WriteReviewFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteReviewFrag newInstance(String param1, String param2) {
        WriteReviewFrag fragment = new WriteReviewFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,  param1);
        args.putString(ARG_PARAM2,  param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reRef = FirebaseDatabase.getInstance().getReference();
        reAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 =  getArguments().getString(ARG_PARAM1);
            mParam2 =  getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_review, container, false);

        reviewText = (EditText)view.findViewById(R.id.reviewholder);
        rateBar = (RatingBar)view.findViewById(R.id.reviewRate);
        subbtn = (Button)view.findViewById(R.id.submitReview);

        subbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reviewText.getText().toString() == "" ){
                    Snackbar bar = Snackbar.make(v, "Sorry, review is empty", Snackbar.LENGTH_LONG);
                    bar.show();
                    return;
                }
                else if (rateBar.getRating() == 0.0){
                    Snackbar bar = Snackbar.make(v, "Sorry, rate is empty", Snackbar.LENGTH_LONG);
                    bar.show();
                    return;
                }
                else {
                    final String name = mParam2;
                    final String container = reviewText.getText().toString();
                    final String rate = String.valueOf(rateBar.getRating());
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String time = df.format(c.getTime());

                    if (reAuth.getCurrentUser() != null){
                        reRef.child("users").child(reAuth.getCurrentUser().getUid())
                                .child("Review").child(mParam2).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    reRef.child("users").child(reAuth.getCurrentUser().getUid())
                                            .child("Review").push().setValue(new ReviewItem(container, rate, time, name));

                                }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    closeFrag();
                }

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

    private void closeFrag(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this).commit();
        getActivity().finish();

    }


    @Override
    public void onStop() {
        super.onStop();

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
