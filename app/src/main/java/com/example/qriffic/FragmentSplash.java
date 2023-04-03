package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.qriffic.databinding.FragmentSplashBinding;
import com.google.firebase.FirebaseApp;


/**
 * Represents a splash screen. Performs preliminary checks
 * for the existence of a username file, and navigates to profileCreate
 * if one does not exist. Otherwise navigates to UserProfile.
 */
public class FragmentSplash extends Fragment {

    private FragmentSplashBinding binding;
    private String username;
    private NavController navController;

    public FragmentSplash() {
        // Required empty public constructor
    }

    private UsernamePersistent usernamePersistent;

    /**
     * Inflates the layout for this fragment and initializes the usernamePersistent
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // uncomment to delete uniqueID file and test 1st visit or not
        //usernamePersistent.deleteUsername();
        //
        String username = usernamePersistent.fetchUsername();
        Bundle bundle = new Bundle();
        if (username == null) {  // handle profile creation if necessary
            this.navController.navigate(R.id.action_fragmentSplash_to_ProfileCreate, bundle);
        } else {
            bundle.putString("username", username);
            this.navController.navigate(R.id.action_fragmentSplash_to_userProfile, bundle);
        }
    }

}
