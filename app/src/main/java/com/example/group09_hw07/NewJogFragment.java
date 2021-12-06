package com.example.group09_hw07;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewJogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewJogFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewJogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewJogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewJogFragment newInstance(String param1, String param2) {
        NewJogFragment fragment = new NewJogFragment();
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
        return inflater.inflate(R.layout.fragment_new_jog, container, false);
    }

    EditText editTextJogTitle;
    Button buttonStartJog, buttonStopJog, buttonCancelJog;
    ConstraintLayout mapsFragmentContainter;
    String jogTitle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Jog");
        editTextJogTitle = view.findViewById(R.id.editTextJogTitle);
        buttonStartJog = view.findViewById(R.id.buttonStartJog);
        buttonStopJog = view.findViewById(R.id.buttonStopJog);
        buttonCancelJog = view.findViewById(R.id.buttonCancelJog);
        mapsFragmentContainter = view.findViewById(R.id.mapsFragmentContainter);

        mapsFragmentContainter.setVisibility(View.INVISIBLE);
        buttonStopJog.setVisibility(View.INVISIBLE);

        buttonCancelJog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new JogsFragment())
                        .commit();
            }
        });

        buttonStartJog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jogTitle = editTextJogTitle.getText().toString();
                if(jogTitle.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage("Please enter jog title to start jog")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();
                } else {
                    editTextJogTitle.setVisibility(View.INVISIBLE);
                    buttonStartJog.setVisibility(View.INVISIBLE);
                    buttonCancelJog.setVisibility(View.INVISIBLE);
                    mapsFragmentContainter.setVisibility(View.VISIBLE);
                    buttonStopJog.setVisibility(View.VISIBLE);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mapsFragmentContainter, new MapsFragment())
                            .commit();
                }
            }
        });

        buttonStopJog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsFragment mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapsFragmentContainter);
                mapsFragment.stopLocationUpdates(jogTitle);
            }
        });

    }
}