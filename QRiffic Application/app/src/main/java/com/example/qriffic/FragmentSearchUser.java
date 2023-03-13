package com.example.qriffic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.qriffic.databinding.FragmentSearchUserBinding;
import com.example.qriffic.databinding.ProfileCreateBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearchUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearchUser extends Fragment {


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DBAccessor dba;
    private NavController navController;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSearchUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchUser.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearchUser newInstance(String param1, String param2) {
        FragmentSearchUser fragment = new FragmentSearchUser();
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
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        ImageButton btnSearch = view.findViewById(R.id.search_button);
        EditText etSearch = view.findViewById(R.id.search_user_edit_text);
//
        btnSearch.setOnClickListener(new View.OnClickListener() {
            private NavController navController;



            @Override
            public void onClick(View v) {
                // if the EditText is empty, prompt user to enter something
                if (etSearch.getText().toString().isEmpty()) {
                    etSearch.setError("Please enter a username");
                }
//
                else {

                    //query the database and check if the user exists
                    //if the user exists, then display the user's profile
                    //if the user does not exist, then display a message saying that the user does not exist
                    FirebaseApp.initializeApp(getActivity()); // initialize firebase
                    db.collection("Players")
                            .whereEqualTo("username", etSearch.getText().toString())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        etSearch.setError("User does not exist");
                                    }
                                    else {
                                        // display the user's profile
                                        PlayerProfile playerprofile = new PlayerProfile();
                                        FragmentUserProfile fragmentUserProfile = new FragmentUserProfile();
                                        Bundle bundle = new Bundle();
                                        playerprofile.setUsername(etSearch.getText().toString());

                                        //set the player fields using db accessor
                                        dba = new DBAccessor();
                                        dba.setPlayer(playerprofile);
//                                      send the username to the playerprofile fragment
                                        bundle.putString("username", etSearch.getText().toString());
                                        fragmentUserProfile.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,fragmentUserProfile).commit();

                                    }
                                }
                            });





                }
            }
        });

                return view;
    }



}