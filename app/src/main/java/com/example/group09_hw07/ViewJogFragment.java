package com.example.group09_hw07;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewJogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewJogFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Jog mJog;

    public ViewJogFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ViewJogFragment newInstance(Jog jog) {
        ViewJogFragment fragment = new ViewJogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, jog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJog = (Jog) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_jog, container, false);
    }

    TextView jogTitle, displayName, timeAndDate;
    Button buttonGoBack;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jogTitle = view.findViewById(R.id.jogTitle);
        displayName = view.findViewById(R.id.displayName);
        timeAndDate = view.findViewById(R.id.timeAndDate);
        buttonGoBack = view.findViewById(R.id.buttonGoBack);

        jogTitle.setText(mJog.title);
        displayName.setText(mJog.username);
        timeAndDate.setText(
                new SimpleDateFormat("MM/dd/yyyy hh:mm aaa").format(mJog.createdAt.toDate())
        );

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.viewMapContainer, ViewJogMapsFragment.newInstance(mJog))
                .commit();

        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new JogsFragment())
                        .commit();
            }
        });
    }

}