package com.example.qriffic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLeaderboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLeaderboard extends Fragment {

    private ArrayList<LeaderboardEntry> dataList;
    private LeaderboardAdapter leaderboardAdapter;

    public FragmentLeaderboard() {
        // Required empty public constructor
    }

    public static FragmentLeaderboard newInstance() {
        FragmentLeaderboard fragment = new FragmentLeaderboard();
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
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        TextView leaderboardPlayerTitle = view.findViewById(R.id.leaderboard_player_title);
        TextView myName = view.findViewById(R.id.my_name);
        TextView myPoints = view.findViewById(R.id.my_points);
        TextView myRankNumber = view.findViewById(R.id.my_rank_number);
        TextView leaderboardType = view.findViewById(R.id.leaderboard_type);
        ListView leaderboardList = view.findViewById(R.id.leaderboard_list);

        Spinner spinner = (Spinner)view.findViewById(R.id.leaderboard_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.leaderboard_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ViewGroupCompat.setTransitionGroup(leaderboardList, true);
        ViewGroupCompat.setTransitionGroup(spinner, true);

        LeaderboardData data = new LeaderboardData();
        PlayerProfile profile = new PlayerProfile();

        String username = new UsernamePersistent(getContext()).fetchUsername();

        DBA.getPlayer(profile, username);

        profile.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                DBA.getLeaderboard(data, profile);
            }
            @Override
            public void onFetchFailure() {

            }
        });

        data.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                    dataList = data.getTopPlayerPoints();

                    leaderboardPlayerTitle.setText("My Score");

                    myName.setText(profile.getUsername());
                    myPoints.setText(Integer.toString(profile.getTotalScore()));
                    int myRank = 0;
                    myName.setText(profile.getUsername());
                    myPoints.setText(Integer.toString(profile.getTotalScanned()));

                    while (myRank < dataList.size()) {
                        if (Objects.equals(dataList.get(myRank).getId(), profile.getUsername())) {
                            break;
                        }
                        myRank += 1;
                    }
                    if (myRank < dataList.size()) {
                        myRankNumber.setText(Integer.toString(myRank + 1));
                    } else {
                        myRankNumber.setText("N/A");
                    }

                    leaderboardType.setText("Top Points Globally");

                    ArrayList<LeaderboardEntry> adapterList = new ArrayList<>();
                    leaderboardAdapter = new LeaderboardAdapter(getContext(), adapterList);
                    leaderboardAdapter.addAll(data.getTopPlayerPoints());
                    leaderboardList.setAdapter(leaderboardAdapter);
                    leaderboardAdapter.notifyDataSetChanged();
                    startPostponedEnterTransition();
            }

            @Override
            public void onFetchFailure() {
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = (String)parent.getItemAtPosition(position);
                if (Objects.nonNull(dataList)) {
                    if (Objects.equals(choice, "Top Player Scores")) {
                        dataList = data.getTopPlayerPoints();
                        leaderboardPlayerTitle.setText("My Score");

                        myName.setText(profile.getUsername());
                        myPoints.setText(Integer.toString(profile.getTotalScore()));
                        int myRank = 0;
                        while (myRank < dataList.size()) {
                            if (Objects.equals(dataList.get(myRank).getId(), profile.getUsername())) {
                                break;
                            }
                            myRank += 1;
                        }
                        if (myRank < dataList.size()) {
                            myRankNumber.setText(Integer.toString(myRank + 1));
                        } else {
                            myRankNumber.setText("N/A");
                        }

                        leaderboardType.setText("Top Points Globally");

                        leaderboardAdapter.clear();
                        leaderboardAdapter.addAll(data.getTopPlayerPoints());
                        leaderboardAdapter.notifyDataSetChanged();
                    } else if (Objects.equals(choice, "Most QRMons Scanned")) {
                        dataList = data.getTopPlayerScans();
                        leaderboardPlayerTitle.setText("My Scans");
                        int myRank = 0;
                        myName.setText(profile.getUsername());
                        myPoints.setText(Integer.toString(profile.getTotalScanned()));

                        while (myRank < dataList.size()) {
                            if (Objects.equals(dataList.get(myRank).getId(), profile.getUsername())) {
                                break;
                            }
                            myRank += 1;
                        }
                        if (myRank < dataList.size()) {
                            myRankNumber.setText(Integer.toString(myRank + 1));
                        } else {
                            myRankNumber.setText("N/A");
                        }

                        leaderboardType.setText("Most Scans Globally");

                        leaderboardAdapter.clear();
                        leaderboardAdapter.addAll(data.getTopPlayerScans());
                        leaderboardAdapter.notifyDataSetChanged();
                    } else if (Objects.equals(choice, "Top QRMons Globally")) {
                        dataList = data.getTopQRPoints();
                        leaderboardPlayerTitle.setText("My Best QRMon");

                        QRCode qr = profile.getBestQR();
                        if (qr == null) {
                            myName.setText("N/A");
                            myPoints.setText("N/A");
                            myRankNumber.setText("N/A");
                        } else {
                            myName.setText(qr.getName());
                            myPoints.setText(Integer.toString(qr.getScore()));
                            int myRank = 0;
                            while (myRank < dataList.size()) {
                                if (Objects.equals(dataList.get(myRank).getId(), qr.getName())) {
                                    break;
                                }
                                myRank += 1;
                            }
                            myRankNumber.setText(Integer.toString(myRank + 1));
                        }
                        leaderboardType.setText("Top QRMons Globally");

                        leaderboardAdapter.clear();
                        leaderboardAdapter.addAll(data.getTopQRPoints());
                        leaderboardAdapter.notifyDataSetChanged();
                    } else if (Objects.equals(choice, "Top QRMons Regionally")) {
                        leaderboardType.setText("Top QRMons in ...");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }
}