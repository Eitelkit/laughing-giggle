package com.example.mxx62.findit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewlistFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewlistFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewlistFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView review_recycle;
    private ReviewRecyclerAdapter review_adapter;
    private ReviewData myReview = new ReviewData();
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;

    public ReviewlistFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewlistFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewlistFrag newInstance(String param1, String param2) {
        ReviewlistFrag fragment = new ReviewlistFrag();
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

        View view = inflater.inflate(R.layout.fragment_reviewlist, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        review_recycle=(RecyclerView)view.findViewById(R.id.reviewlistrecycler);

        final LinearLayoutManager my_layoutMag = new LinearLayoutManager(view.getContext());
        my_layoutMag.setOrientation(LinearLayoutManager.VERTICAL);
        my_layoutMag.scrollToPosition(0);
        review_recycle.setLayoutManager(my_layoutMag);
        review_recycle.setHasFixedSize(false);
        review_adapter = new ReviewRecyclerAdapter(ReviewItem.class, R.layout.review_card,
                ReviewRecyclerAdapter.reViewHolder.class, reference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("Review").getRef(), getActivity());
        review_recycle.setAdapter(review_adapter);
        // Inflate the layout for this fragment
        if (myReview.getSize() == 0) {
            myReview.setAdapter(review_adapter);
            myReview.setContext(getActivity());
            myReview.initializeDataFromCloud();
        }

        review_adapter.setOnItemClickListener(new ReviewRecyclerAdapter.OnReviewListItemClickListener() {
            @Override
            public void onImageClick(View v, int position) {
                HashMap reviewItem = myReview.getItem(position);
                myReview.removeItemFromServer(reviewItem);
            }
        });

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = (viewHolder.getAdapterPosition());
                HashMap reviewItem = myReview.getItem(position);
                myReview.removeItemFromServer(reviewItem);
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(review_recycle);
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
