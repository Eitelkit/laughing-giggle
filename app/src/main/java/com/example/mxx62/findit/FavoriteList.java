package com.example.mxx62.findit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoriteList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView favor_recycle;
    private myFirebaseRecyclerAdapter favor_adapter;
    private RestaurantData myRest = new RestaurantData();
    private DatabaseReference refavor;
    private FirebaseAuth firebaseAuth;


    public FavoriteList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteList.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteList newInstance(String param1, String param2) {
        FavoriteList fragment = new FavoriteList();
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
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        refavor = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid()).child("Favorite").getRef()

        favor_recycle = (RecyclerView) view.findViewById(R.id.favorlist);
        final LinearLayoutManager my_layoutMag = new LinearLayoutManager(view.getContext());
        my_layoutMag.setOrientation(LinearLayoutManager.VERTICAL);
        my_layoutMag.scrollToPosition(0);
        favor_recycle.setLayoutManager(my_layoutMag);
        favor_recycle.setHasFixedSize(false);
        favor_adapter = new myFirebaseRecyclerAdapter(listItem.class, R.layout.card_view,
                myFirebaseRecyclerAdapter.myViewHolder.class, (refavor.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("Favorite").getRef()), getActivity());
        favor_recycle.setAdapter(favor_adapter);
        // Inflate the layout for this fragment
        if (myRest.getSize() == 0) {
            myRest.setAdapter(favor_adapter);
            myRest.setContext(getActivity());
            myRest.initializeDataFromCloud();
        }

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = (viewHolder.getAdapterPosition());
                HashMap reviewItem = myRest.getItem(position);
                myRest.removeItemFromServer(reviewItem);
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(favor_recycle);
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
