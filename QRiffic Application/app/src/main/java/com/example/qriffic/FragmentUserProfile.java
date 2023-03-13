package com.example.qriffic;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserProfile extends Fragment {


    public interface OnDataPass {
        String onDataPass(String data);
    }


    private OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }







    FirebaseFirestore db = FirebaseFirestore.getInstance();



    ListView profileList;
    ArrayAdapter<QRCode> pListAdapter;
    ArrayList<QRCode> qrList;






    //initialize variables
    private String username;







    public FragmentUserProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment UserProfile.
     */
    public static FragmentUserProfile newInstance() {
        FragmentUserProfile fragment = new FragmentUserProfile();
        Bundle args = new Bundle();

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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        Bundle bundle = getArguments();

        //initialize the text views
        TextView tvUsername = view.findViewById(R.id.user_name);
        TextView tvEmail = view.findViewById(R.id.profile_email);
        TextView tvPhoneNum = view.findViewById(R.id.profile_phone);
        TextView noScanned = view.findViewById(R.id.user_scanned);
        TextView totalScore = view.findViewById(R.id.user_score);

        //initialize and array list of QR codes
        qrList = new ArrayList<>();
        profileList = view.findViewById(R.id.profileList);
        pListAdapter = new ArrayAdapter<>(getContext(),R.layout.qr_dex_content, qrList);
        profileList.setAdapter(pListAdapter);



        username = bundle.getString("username").replaceAll("[^a-zA-Z0-9!]", "");
        dataPasser.onDataPass(username);
        PlayerProfile playerProfile = new PlayerProfile();
        //get from database the user data based on username
        DocumentReference docRef = db.collection("Players").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        //set the user data to the player profile
                        playerProfile.setEmail(document.getString("email"));
                        playerProfile.setHighScore(document.getLong("highScore").intValue());
                        playerProfile.setLowScore(document.getLong("lowScore").intValue());
                        playerProfile.setPhoneNum(document.getString("phoneNum"));
                        qrList = (ArrayList<QRCode>) document.get("captured");
                        System.out.println("qrList"+qrList);


                        //set the text views to the user data
                        tvUsername.setText(username);
                        tvEmail.setText(playerProfile.getEmail());
                        tvPhoneNum.setText(playerProfile.getPhoneNum());



                        //find number of QR codes in the list
                        Integer numQRCodes = qrList.size();
                        noScanned.setText(numQRCodes.toString());

                        //find total score


                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });





        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileList = view.findViewById(R.id.profileList);
        pListAdapter = new ArrayAdapter<>(getContext(),R.layout.qr_dex_content, qrList);
        PlayerProfile playerProfile = new PlayerProfile();
//        System.out.println("qrList in viewcreated:"+qrList);
//        profileList.setAdapter(pListAdapter);


//        TextView tvHighScore = view.findViewById(R.id.profile_high_score);


        //code to add QR codes to the list goes here



    }


}