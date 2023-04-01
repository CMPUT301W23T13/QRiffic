package com.example.qriffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<LeaderboardEntry> {

    public LeaderboardAdapter(@NonNull Context context, @NonNull ArrayList<LeaderboardEntry> entires) {
        super (context, 0, entires);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_list_item,
                    parent, false);
        } else {
            view = convertView;
        }

        LeaderboardEntry entry = getItem(position);
        if (entry != null) {
            TextView rankNumberView = view.findViewById(R.id.leaderboard_rank_number);
            TextView usernameView = view.findViewById(R.id.leaderboard_username_text);
            TextView pointsView = view.findViewById(R.id.leaderboard_user_points);

            rankNumberView.setText(String.valueOf(position + 1));
            usernameView.setText(entry.getId());
            pointsView.setText(entry.getValue() );
        }

        return view;
    }
}
