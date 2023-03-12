package com.example.qriffic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qriffic.databinding.ActivityMainBinding;
import com.example.qriffic.databinding.FragmentSecondBinding;
import com.example.qriffic.databinding.FragmentSplashBinding;
import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSplash#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSplash extends Fragment {

    private FragmentSplashBinding binding;
    private String uniqueID;
    private DBAccessor dba;
    private NavController navController;

    public FragmentSplash() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSplash.
     */
    public static FragmentSplash newInstance() {
        FragmentSplash fragment = new FragmentSplash();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Deletes a uniqueID file if it exists
     */
    protected void deleteUniqueID() {
        if (fetchUniqueID() != null) {
            getActivity().getApplicationContext().deleteFile("unique-id");
        }
    }

    /**
     * Checks if a UniqueID has already been generated
     *
     * @return true if a UniqueID has already been generated (uniqueID file exists)
     * false otherwise
     */
    protected String fetchUniqueID() {
        try {
            // Try to read the uniqueID from file
            FileInputStream secretIDInputStream = getActivity().getApplicationContext().openFileInput("unique-id");
            byte[] uniqueIDBytes = new byte[36];
            secretIDInputStream.read(uniqueIDBytes);
            uniqueID = "";
            for (int i = 0; i < 36; i++) {
                uniqueID += (char) uniqueIDBytes[i];
            }
        } catch (Exception FileNotFoundException) {
            // No uniqueID file found
            return null;
        }
        return uniqueID;
    }

    /**
     * Generates UniqueID and saves it to a file
     * @return uniqueID: a string representation of a UUID
     */
    protected String generateUniqueID() {
        String uniqueID;
        // Generate uniqueID and save to file
        uniqueID = UUID.randomUUID().toString();
        File secretIDFile = new File(getActivity().getApplicationContext().getFilesDir(), "unique-id");
        try {
            secretIDFile.createNewFile();
            FileOutputStream secretIDOutputStream = new FileOutputStream(secretIDFile);
            secretIDOutputStream.write(uniqueID.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uniqueID;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.navController = Navigation.findNavController(getView());

//        //wait 5 seconds before navigating to next fragment
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        FirebaseApp.initializeApp(getActivity()); // initialize firebase
        dba = new DBAccessor();
//        deleteUniqueID(); // uncomment to delete uniqueID file and test 1st visit or not
        String uid = fetchUniqueID();
        Bundle bundle = new Bundle();

        //TEMPORARY TEST CODE BLOCK (DELETE WHEN DONE)
        ArrayList<QRCode> testList = new ArrayList<QRCode>();
        testList.add(new QRCode("one", null, null));
        testList.add(new QRCode("two", null, null));
        testList.add(new QRCode("three", null, null));

        dba.setPlayer(
                new PlayerProfile("testName", "testUniqueID", "testEmail",
                        "testPhoneNum", 420, 69, testList)
        );
        PlayerProfile fetchedPlayer = new PlayerProfile(null, null, null,
                null, 0, 0, new ArrayList<>());
        //PlayerProfile player = dba.getPlayer("testName");
        dba.getPlayer(fetchedPlayer, "testName");
        //fetchedPlayer.setEmail("testEmail2");
//        dba.setPlayer(fetchedPlayer);
        //END TEMPORARY TEST CODE BLOCK

        //handle profile creation if necessary
        if (uid == null) {
            this.uniqueID = generateUniqueID();
            bundle.putString("secretID", uniqueID);
            this.navController.navigate(R.id.action_fragmentSplash_to_ProfileCreate, bundle);
            Toast.makeText(getActivity(), "1st visit", Toast.LENGTH_SHORT).show();
        } else {
            this.uniqueID = uid;
            bundle.putString("secretID", uniqueID);
            this.navController.navigate(R.id.action_fragmentSplash_to_QRDex, bundle);
            Toast.makeText(getActivity(), "not 1st visit", Toast.LENGTH_SHORT).show();
        }
    }

}
