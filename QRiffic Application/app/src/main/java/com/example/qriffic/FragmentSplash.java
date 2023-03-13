package com.example.qriffic;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qriffic.databinding.FragmentSplashBinding;
import com.google.firebase.FirebaseApp;

import java.io.FileInputStream;


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
        return binding.getRoot();
    }

    /**
     * Deletes a username file if it exists
     */
    protected void deleteUsername() {
        if (fetchUsername() != null) {
            getActivity().getApplicationContext().deleteFile("username");
        }
    }

    /**
     * Checks if a username has been saved
     *
     * @return true if a username has already been saved (username file exists)
     * false otherwise
     */
    protected String fetchUsername() {
        try {
            // Try to read the username from file
            FileInputStream secretIDInputStream = getActivity().getApplicationContext().openFileInput("username");
            byte[] uniqueIDBytes = new byte[36];
            secretIDInputStream.read(uniqueIDBytes);
            username = "";
            for (int i = 0; i < 36; i++) {
                username += (char) uniqueIDBytes[i];
            }
        } catch (Exception FileNotFoundException) {
            // No uniqueID file found
            return null;
        }
        return username;
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
        deleteUsername(); // uncomment to delete uniqueID file and test 1st visit or not
        String username = fetchUsername();
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
