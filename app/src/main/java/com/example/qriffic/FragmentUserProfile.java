package com.example.qriffic;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserProfile extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private long mLastClickTime = 0;
    private ListView profileListView;
    private ArrayList<QRCode> dataList;
    private QRCodeAdapter qrAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<QRCode> qrList;
    private final boolean mLocationPermissionGranted = false;

    //Declare the views
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhoneNum;
    private TextView tvEmptyQRMon;
    private TextView noScanned;
    private TextView totalScore;
    private TextView highScore;
    private TextView lowScore;
    private TextView topQRName;
    private TextView botQRName;
    private LinearLayout topQRLayout;
    private LinearLayout botQRLayout;
    private ImageView topQRImage;
    private ImageView botQRImage;

    public FragmentUserProfile() {
        // Required empty public constructor
    }
//

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProfile.
     */
    public static FragmentUserProfile newInstance() {
        FragmentUserProfile fragment = new FragmentUserProfile();
        Bundle args = new Bundle();

        return fragment;
    }

    private final boolean isPermissionRequestedBefore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.fade));

        checkAndRequestPermissions();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postponeEnterTransition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        Bundle bundle = getArguments();


        //initialize the text views
        tvUsername = view.findViewById(R.id.user_name);
        tvEmail = view.findViewById(R.id.profile_email);
        tvPhoneNum = view.findViewById(R.id.profile_phone);
        tvEmptyQRMon = view.findViewById(R.id.empty_qrmon_label);
        noScanned = view.findViewById(R.id.user_scanned);
        totalScore = view.findViewById(R.id.user_score);
        highScore = view.findViewById(R.id.topQRName2);
        lowScore = view.findViewById(R.id.topQRName3);
        topQRName = view.findViewById(R.id.topQRName);
        botQRName = view.findViewById(R.id.botQRName);
        topQRLayout = view.findViewById(R.id.top_score_linear_layout);
        botQRLayout = view.findViewById(R.id.bot_score_linear_layout);
        topQRImage = view.findViewById(R.id.imageTop);
        botQRImage = view.findViewById(R.id.imageBot);

        //initialize an array list of QR codes
        qrList = new ArrayList<>();

        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                startPostponedEnterTransition();
                qrList = new ArrayList<QRCode>(playerProfile.getCaptured().values());

                ArrayList<QRCode> QRAdapterList = new ArrayList<QRCode>();

                //create array for lowest score
                long[] lowScoreArray = new long[qrList.size()];
                String[] NameArray = new String[qrList.size()];
                String[] HashArray = new String[qrList.size()];

                //make a dictionary for the scores and names
                HashMap<String, Integer> NameMap = new HashMap<>();

                for (int i = 0; i < qrList.size(); i++) {
                    NameMap.put(qrList.get(i).getName(), qrList.get(i).getScore());
                    lowScoreArray[i] = qrList.get(i).getScore();
                    NameArray[i] = qrList.get(i).getName();
                    HashArray[i] = qrList.get(i).getIdHash();
                }

                totalScore.setText(playerProfile.getTotalScore() + "pts");

                dataList = new ArrayList<QRCode>();
                qrAdapter = new QRCodeAdapter(getContext(), dataList);

                for (int i = 0; i < qrList.size(); i++) {
                    dataList.add(new QRCode(NameArray[i], lowScoreArray[i], HashArray[i]));
                }

                qrAdapter.notifyDataSetChanged();
                System.out.println("dataList" + dataList);
                System.out.println("qrAdapter" + qrAdapter);

                profileListView = view.findViewById(R.id.profileList);
                profileListView.setAdapter(qrAdapter);

                if (qrList.size() > 0) {
                    lowScore.setText(playerProfile.getLowScore() + "pts");
                    highScore.setText(playerProfile.getHighScore() + "pts");
                    totalScore.setText(playerProfile.getTotalScore() + "pts");
                    tvEmptyQRMon.setVisibility(View.GONE);
                    //set name for lowest score and  highest score
                    QRCode topQR = playerProfile.getBestQR();
                    topQRName.setText(topQR.getName());

                    //set image for highest score
                    if (Objects.equals(topQR.getName(), "400 IQ-RMon")) {
                        Drawable iq = ContextCompat.getDrawable(getContext(), R.drawable.iq);
                        topQRImage.setImageDrawable(iq);
                    } else {

                        String highurl = "https://www.gravatar.com/avatar/" + topQR.getIdHash() + "?s=55&d=identicon&r=PG%22";
                        Glide.with(getContext())
                                .load(highurl)
                                .centerCrop()
                                .error(R.drawable.ic_launcher_background)
                                .into((ImageView) view.findViewById(R.id.imageTop));
                    }

                    QRCode worstQR = playerProfile.getWorstQR();
                    botQRName.setText(worstQR.getName());



                    if (Objects.equals(worstQR.getName(), "400 IQ-RMon")) {
                        Drawable iq = ContextCompat.getDrawable(getContext(), R.drawable.iq);
                        botQRImage.setImageDrawable(iq);
                    } else {
                        //set image for lowest score
                        String lowurl = "https://www.gravatar.com/avatar/" + worstQR.getIdHash() + "?s=55&d=identicon&r=PG%22";
                        Glide.with(getContext())
                                .load(lowurl)
                                .centerCrop()
                                .error(R.drawable.ic_launcher_background)
                                .into((ImageView) view.findViewById(R.id.imageBot));
                    }
                } else {
                    tvEmptyQRMon.setVisibility(View.VISIBLE);
                    lowScore.setText("N/A");
                    highScore.setText("N/A");
                    topQRLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "No QRMon to display", Toast.LENGTH_SHORT).show();
                        }
                    });
                    botQRLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "No QRMon to display", Toast.LENGTH_SHORT).show();
                        }
                    });
                    topQRImage.setVisibility(View.GONE);
                    botQRImage.setVisibility(View.GONE);
                }


                //set the text views to the user data
                tvUsername.setText(playerProfile.getUsername());
                tvEmail.setText(playerProfile.getEmail());
                tvPhoneNum.setText(playerProfile.getPhoneNum());

                noScanned.setText(String.valueOf(playerProfile.getTotalScanned()));
            }

            @Override
            public void onFetchFailure() {

            }
        });


        profileListView = view.findViewById(R.id.profileList);
        ViewGroupCompat.setTransitionGroup(profileListView, true);
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Bundle bundle = new Bundle();
                QRCode qrCode = qrList.get(position);
                bundle.putString("QRID", qrCode.getIdHash());
                bundle.putBoolean("isUser", true);
                Navigation.findNavController(view).navigate(R.id.action_userProfile_to_fragment_QR_Detail, bundle);
            }
        });

        topQRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Bundle bundle = new Bundle();
                QRCode qrCode = playerProfile.getBestQR();
                bundle.putString("QRID", qrCode.getIdHash());
                bundle.putBoolean("isUser", true);
                Navigation.findNavController(view).navigate(R.id.action_userProfile_to_fragment_QR_Detail, bundle);
            }
        });

        botQRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Bundle bundle = new Bundle();
                QRCode qrCode = playerProfile.getWorstQR();
                bundle.putString("QRID", qrCode.getIdHash());
                bundle.putBoolean("isUser", true);
                Navigation.findNavController(view).navigate(R.id.action_userProfile_to_fragment_QR_Detail, bundle);
            }
        });

        DBA.getPlayer(playerProfile, new UsernamePersistent(getContext()).fetchUsername());

        return view;
    }


    // check for location permission
    private boolean checkAndRequestPermissions() {

        //check for camera permission
        int cameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);

        // check for location permission
        int locationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "camera & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                            showDialogOK("Camera and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(getContext(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


}