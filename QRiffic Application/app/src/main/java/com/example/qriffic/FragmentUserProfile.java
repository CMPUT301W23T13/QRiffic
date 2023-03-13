package com.example.qriffic;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.utilities.Score;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.checkerframework.checker.nullness.qual.NonNull;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserProfile extends Fragment {



    DBAccessor dba = new DBAccessor();


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





//    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//
//    private String username;
//    private String email;
//    private Integer score;
//
//
    public FragmentUserProfile() {
        // Required empty public constructor
    }
//
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
        TextView highScore = view.findViewById(R.id.topQRName2);
        TextView lowScore = view.findViewById(R.id.topQRName3);
        TextView topQRName = view.findViewById(R.id.topQRName);
        TextView botQRName = view.findViewById(R.id.botQRName);

        //initialize and array list of QR codes
        qrList = new ArrayList<QRCode>();
        profileList = view.findViewById(R.id.profileList);




        username = bundle.getString("username").replaceAll("[^a-zA-Z0-9!]", "");
        dataPasser.onDataPass(username);

        username = bundle.getString("username");
        System.out.println("username"+username);

        PlayerProfile playerProfile = new PlayerProfile();


        //get from database the user data based on username
        DocumentReference docRef = db.collection("Players").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint({"RestrictedApi", "SetTextI18n"})
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
                        ArrayList<QRCode> QRAdapterList = new ArrayList<QRCode>();


//                        pListAdapter.clear();




                        //find total score
                        long totalScoreInt = 0;

                        //create array for lowest score
                        long[] lowScoreArray = new long[qrList.size()];
                        String[] NameArray = new String[qrList.size()];

                        //make a dictionary for the scores and names
                        HashMap<String, Long> NameMap = new HashMap<>();

                        for (int i = 0; i < qrList.size(); i++) {
                            Object obj = qrList.get(i);
                            if (obj instanceof HashMap) {
                                HashMap<String, Object> qrMap = (HashMap<String, Object>) obj;
                                long score = (long) qrMap.get("score");
                                String name = (String) qrMap.get("name");

                                NameMap.put(name, score);

    //                            //use NameMap hashmap to add to adapter
    //                            pListAdapter.add(new QRCode());
    //
    //                            //notifying the adapter that data has been added or changed
    //                            pListAdapter.notifyDataSetChanged();




                                //add to array
                                lowScoreArray[i] = score;
                                NameArray[i] = name;

                                //get total score

                                totalScoreInt += score;

                                totalScore.setText(String.valueOf(totalScoreInt)+"pts");

                                //update highest score in database
                                if (score > playerProfile.getHighScore()) {
                                    playerProfile.setHighScore((int) score);
                                    document.getReference().update("highScore", score);
                                }

                                //update lowest score in database
                                if (score < playerProfile.getLowScore()) {
                                    playerProfile.setLowScore((int) score);
                                    document.getReference().update("lowScore", score);
                                }


                            }


                        }

                        for (int i = 0; i < qrList.size(); i++) {
                            QRAdapterList.add(new QRCode(NameArray[i], lowScoreArray[i]));
                            //notify the adapter that data has been added or changed
                            pListAdapter.notifyDataSetChanged();
                            }

//                        pListAdapter = new QRcodeAdapter(requireContext(), QRAdapterList);
//                        profileList.setAdapter(pListAdapter);



                        //sort array
                        Arrays.sort(lowScoreArray);
                        //set lowest score for player and database
                        if (lowScoreArray.length > 0) {
                            playerProfile.setLowScore((int) lowScoreArray[0]);
                            document.getReference().update("lowScore", lowScoreArray[0]);
                        } else {
                            playerProfile.setLowScore(-1);
                            document.getReference().update("lowScore", -1);
                        }

                        //set the text view for lowest score
                        if (playerProfile.getLowScore() == -1) {
                            lowScore.setText("N/A");
                        } else {
                            lowScore.setText(String.valueOf(playerProfile.getLowScore()));
                        }
                        highScore.setText(String.valueOf(playerProfile.getHighScore()));

                        //set name for lowest score and  highest score
                        for (Map.Entry<String, Long> entry : NameMap.entrySet()) {

                            if (entry.getValue() == (playerProfile.getHighScore())) {
                                topQRName.setText(entry.getKey());
                            }
                            if (entry.getValue()==(playerProfile.getLowScore())) {
                                botQRName.setText(entry.getKey());
                            }
                        }

//                        pListAdapter = new QRcodeAdapter(requireContext(), qrList);
//                        profileList.setAdapter(pListAdapter);



//





                        //set the text views to the user data
                        tvUsername.setText(username);
                        tvEmail.setText(playerProfile.getEmail());
                        tvPhoneNum.setText(playerProfile.getPhoneNum());





                        //find number of QR codes in the list
                        Integer numQRCodes = qrList.size();
                        noScanned.setText(numQRCodes.toString());



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



    }


}