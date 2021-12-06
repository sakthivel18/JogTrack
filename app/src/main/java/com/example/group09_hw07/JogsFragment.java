package com.example.group09_hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JogsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "JogsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public JogsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JogsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JogsFragment newInstance(String param1, String param2) {

        JogsFragment fragment = new JogsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_jogs, container, false);
    }

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button buttonLogout, buttonNewJog;
    RecyclerView mRecyclerView;
    JogsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    List<Jog> mJogs = new ArrayList<>();
    IJogsFragment mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Previous Jogs");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonNewJog = view.findViewById(R.id.buttonNewJog);
        mRecyclerView = view.findViewById(R.id.jogsRecyclerView);

        buttonNewJog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new NewJogFragment())
                        //.addToBackStack(null)
                        .commit();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new LoginFragment())
                        .commit();
            }
        });
        mJogs.clear();
        db.collection("jogs")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            Jog jog = new Jog();
                            jog.uid = (String) doc.get("uid");
                            jog.username = (String) doc.get("username");
                            jog.title = (String) doc.get("title");
                            jog.createdAt = (Timestamp) doc.get("createdAt");
                            jog.points = (ArrayList<GeoPoint>) doc.get("points");
                            mJogs.add(jog);
                        }
                        Log.d(TAG, "Previous jogs: " + mJogs);
                        mAdapter.notifyDataSetChanged();
                    }
                });

        mLayoutManager = new LinearLayoutManager(getActivity());
        Log.d(TAG, "Previous jogs: " + mJogs);
        mAdapter = new JogsAdapter(mJogs, mListener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    public static class JogsAdapter extends RecyclerView.Adapter<JogsAdapter.ViewHolder> {

        List<Jog> jogs = new ArrayList<>();
        IJogsFragment listener;
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewJogTitle, textViewUsername, textViewJogTime;
            IJogsFragment listener;
            Jog jog;
            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                textViewJogTitle = view.findViewById(R.id.textViewJogTitle);
                textViewUsername = view.findViewById(R.id.textViewUsername);
                textViewJogTime = view.findViewById(R.id.textViewJogTime);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.viewJog(jog);
                    }
                });
            }

        }

        public JogsAdapter(List<Jog> jogs, IJogsFragment listener) {
            this.jogs = jogs;
            this.listener = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.jogs_list_item, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            try {
                Jog jog = jogs.get(position);
                viewHolder.jog = jog;
                viewHolder.listener = this.listener;
                viewHolder.textViewJogTitle.setText(jog.title);
                viewHolder.textViewUsername.setText(jog.username);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
                viewHolder.textViewJogTime.setText(sdf.format(jog.createdAt.toDate()));
            } catch (Exception ex) {
                Log.d(TAG, "onBindViewHolder: " + ex.getMessage());
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return jogs.size();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IJogsFragment) {
            mListener = (IJogsFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    interface IJogsFragment {
        void viewJog(Jog mJog);
    }
}