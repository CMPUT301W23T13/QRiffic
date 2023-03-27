package com.example.qriffic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLeaderboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLeaderboard extends Fragment {

    private ArrayList<LeaderboardEntry> dataList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        Bundle bundle = getArguments();

        TextView leaderboardTitle = view.findViewById(R.id.leaderboard_title);
        TextView leaderboardPlayerTitle = view.findViewById(R.id.leaderboard_player_title);
        TextView myPoints = view.findViewById(R.id.my_points);
        TextView myRankNumber = view.findViewById(R.id.my_rank_number);
        TextView leaderboardType = view.findViewById(R.id.leaderboard_type);
        ListView leaderboardList = view.findViewById(R.id.leaderboard_list);

        LeaderboardData data = new LeaderboardData();
        data.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                dataList = data.getTopPlayerPoints();

            }

            @Override
            public void onFetchFailure() {

            }
        });

        DBA.getTopPlayerPoints(data);
        DBA.getTopPlayerScans(data);
        DBA.getTopQRPoints(data);

        return view;
    }
}