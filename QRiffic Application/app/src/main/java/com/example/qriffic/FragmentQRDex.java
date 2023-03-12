package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qriffic.databinding.FragmentSecondBinding;

import java.util.ArrayList;

public class FragmentQRDex extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // START TEST BLOCK, TO BE REMOVED
        PlayerProfile fetchedPlayer = new PlayerProfile(null, null, null,
            null, 0, 0, new ArrayList<>());

        fetchedPlayer.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                // For testing purposes
                System.out.println("Player: " + fetchedPlayer.getUsername() + " "
                    + fetchedPlayer.getUniqueID() + " " + fetchedPlayer.getEmail() + " "
                    + fetchedPlayer.getPhoneNum() + " " + fetchedPlayer.getHighScore() + " "
                    + fetchedPlayer.getLowScore() + " " + fetchedPlayer.getCaptured().size());
            }

            public void onFetchFailure() {
                // For testing purposes
                System.out.println("Fetch failed");
            }
        });

        DBAccessor dba = new DBAccessor();
        dba.getPlayer(fetchedPlayer, "testName");
        // END TEST BLOCK

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}