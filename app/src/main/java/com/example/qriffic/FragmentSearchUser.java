package com.example.qriffic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A fragment to allow users to search for other users' profiles.
 */
public class FragmentSearchUser extends Fragment {

    private NavController navController;

    public FragmentSearchUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SearchUser.
     */

    public static FragmentSearchUser newInstance() {
        FragmentSearchUser fragment = new FragmentSearchUser();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        ImageButton btnSearch = view.findViewById(R.id.search_button);
        EditText etSearch = view.findViewById(R.id.search_user_edit_text);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            private NavController navController;

            @Override
            public void onClick(View v) {
                // if the EditText is empty, prompt user to enter something
                if (etSearch.getText().toString().isEmpty()) {
                    etSearch.setError("Please enter a username");
                } else {

                    PlayerProfile fetchedPlayer = new PlayerProfile();
                    fetchedPlayer.addListener(new fetchListener() {
                        @Override
                        public void onFetchComplete() {
                            Bundle bundle = new Bundle();
                            bundle.putString("username", etSearch.getText().toString());
                            Navigation.findNavController(v).navigate(R.id.action_nav_searchUser_to_fragmentUserSearchedProfile, bundle);
                        }

                        @Override
                        public void onFetchFailure() {
                            etSearch.setError("User does not exist");
                        }
                    });

                    DBA.getPlayer(fetchedPlayer, etSearch.getText().toString());
                }
            }
        });

        return view;
    }
}