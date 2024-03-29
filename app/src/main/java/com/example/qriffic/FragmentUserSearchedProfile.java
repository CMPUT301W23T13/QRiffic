package com.example.qriffic;

import android.os.Bundle;
import android.os.SystemClock;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserSearchedProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserSearchedProfile extends Fragment {
    private long mLastClickTime = 0;
    private ListView profileListView;
    private ArrayList<QRCode> dataList;
    private QRCodeAdapter qrAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<QRCode> qrList;

    public FragmentUserSearchedProfile() {
        // Required empty public constructor
    }
//

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProfile.
     */
    public static FragmentUserSearchedProfile newInstance() {
        FragmentUserSearchedProfile fragment = new FragmentUserSearchedProfile();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
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
        View view = inflater.inflate(R.layout.fragment_user_searched_profile, container, false);
        Bundle bundle = getArguments();


        //initialize the text views
        TextView tvUsername = view.findViewById(R.id.user_name);
        TextView tvEmail = view.findViewById(R.id.profile_email);
        TextView tvPhoneNum = view.findViewById(R.id.profile_phone);
        TextView tvEmptyQRMon = view.findViewById(R.id.empty_qrmon_label);
        TextView noScanned = view.findViewById(R.id.user_scanned);
        TextView totalScore = view.findViewById(R.id.user_score);
        TextView highScore = view.findViewById(R.id.topQRName2);
        TextView lowScore = view.findViewById(R.id.topQRName3);
        TextView topQRName = view.findViewById(R.id.topQRName);
        TextView botQRName = view.findViewById(R.id.botQRName);
        LinearLayout topQRLayout = view.findViewById(R.id.top_score_linear_layout);
        LinearLayout botQRLayout = view.findViewById(R.id.bot_score_linear_layout);

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
                ViewGroupCompat.setTransitionGroup(profileListView, true);
                profileListView.setAdapter(qrAdapter);

                if (qrList.size() > 0) {
                    lowScore.setText(playerProfile.getLowScore() + "pts");
                    highScore.setText(playerProfile.getHighScore() + "pts");
                    totalScore.setText(playerProfile.getTotalScore() + "pts");
                    tvEmptyQRMon.setVisibility(View.GONE);
                } else {
                    tvEmptyQRMon.setVisibility(View.VISIBLE);
                    lowScore.setText("N/A");
                    highScore.setText("N/A");
                }

                //set name for lowest score and  highest score
                QRCode topQR = playerProfile.getBestQR();
                topQRName.setText(topQR.getName());

                //set image for highest score
                String highurl = "https://www.gravatar.com/avatar/" + topQR.getIdHash() + "?s=55&d=identicon&r=PG%22";
                Glide.with(getContext())
                        .load(highurl)
                        .centerCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into((ImageView) view.findViewById(R.id.imageTop));

                QRCode worstQR = playerProfile.getWorstQR();
                botQRName.setText(worstQR.getName());

                //set image for lowest score
                String lowurl = "https://www.gravatar.com/avatar/" + worstQR.getIdHash() + "?s=55&d=identicon&r=PG%22";
                Glide.with(getContext())
                        .load(lowurl)
                        .centerCrop()
                        .error(R.drawable.ic_launcher_background)
                        .into((ImageView) view.findViewById(R.id.imageBot));

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
                bundle.putBoolean("isUser", false);
                Navigation.findNavController(view).navigate(R.id.action_fragmentUserSearchedProfile_to_nav_QRDetail, bundle);
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
                bundle.putBoolean("isUser", false);
                Navigation.findNavController(view).navigate(R.id.action_fragmentUserSearchedProfile_to_nav_QRDetail, bundle);
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
                bundle.putBoolean("isUser", false);
                Navigation.findNavController(view).navigate(R.id.action_fragmentUserSearchedProfile_to_nav_QRDetail, bundle);
            }
        });

        String username = getArguments().getString("username");
        DBA.getPlayer(playerProfile, username);

        return view;
    }

}