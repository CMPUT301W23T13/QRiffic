package com.example.qriffic;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qriffic.databinding.FragmentSplashBinding;
import com.google.firebase.FirebaseApp;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Represents a splash screen. Performs preliminary checks
 * for the existence of a username file, and navigates to profileCreate
 * if one does not exist. Otherwise navigates to UserProfile.
 */
public class FragmentSplash extends Fragment {

    private FragmentSplashBinding binding;
    private String username;
    private DBAccessor dba;
    private NavController navController;
    public FragmentSplash() {
        // Required empty public constructor
    }
    private UsernamePersistent usernamePersistent;

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @return A new instance of fragment FragmentSplash.
//     */
//    public static FragmentSplash newInstance() {
//        FragmentSplash fragment = new FragmentSplash();
//        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        usernamePersistent = new UsernamePersistent(getActivity().getApplicationContext());

        return binding.getRoot();
    }

    /**
     * Checks if a username has been saved and navigates to profileCreate if not
     * or to UserProfile if it has.
     */
    @Override
    public void onResume() {
        super.onResume();
        this.navController = Navigation.findNavController(getView());
        FirebaseApp.initializeApp(getActivity()); // initialize firebase
        dba = new DBAccessor();


        //DBACCESSOR TEST ZONE STARTS
        String testUsername = "test1234";
        QRCode testQR = new QRCode("testtt", null, testUsername, null, "test comment here");
        PlayerProfile testProfile = new PlayerProfile(testUsername, "testuuid", "username@outlook.com", "999.999.9999", new ArrayList<QRCode>());

        dba.setPlayer(testProfile);
        dba.addQR(testProfile.getUsername(), testQR);
        dba.deleteQR(testQR);
        //DBACCESSOR TEST ZONE ENDS


        // uncomment to delete uniqueID file and test 1st visit or not
        //usernamePersistent.deleteUsername();
        //
        String username = usernamePersistent.fetchUsername();
        Bundle bundle = new Bundle();
        if (username == null) {  // handle profile creation if necessary
            this.navController.navigate(R.id.action_fragmentSplash_to_ProfileCreate, bundle);
            Toast.makeText(getActivity(), "1st visit", Toast.LENGTH_SHORT).show();
        } else {
            bundle.putString("username", username);
            //send bundle to main activity

            //Intent intent = new Intent(getActivity(), MainActivity.class);
            //intent.putExtra("username", username);

            this.navController.navigate(R.id.action_fragmentSplash_to_userProfile, bundle);
            Toast.makeText(getActivity(), "not 1st visit", Toast.LENGTH_SHORT).show();
        }
    }

}
